/*!COPYRIGHT HEADER! - CONFIDENTIAL 
 *
 * Darwino Inc Confidential.
 *
 * (c) Copyright Darwino Inc. 2014-2016.
 *
 * Notice: The information contained in the source code for these files is the property 
 * of Darwino Inc. which, with its licensors, if any, owns all the intellectual property 
 * rights, including all copyright rights thereto.  Such information may only be used 
 * for debugging, troubleshooting and informational purposes.  All other uses of this information, 
 * including any production or commercial uses, are prohibited. 
 */

package org.openntf.ct.app.user;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.openntf.ct.app.AppDatabaseDef;

import com.darwino.commons.Platform;
import com.darwino.commons.httpclnt.HttpBinaryData;
import com.darwino.commons.json.JsonException;
import com.darwino.commons.json.JsonObject;
import com.darwino.commons.platform.ManagedBeansService;
import com.darwino.commons.security.acl.User;
import com.darwino.commons.security.acl.UserAuthenticator;
import com.darwino.commons.security.acl.UserException;
import com.darwino.commons.security.acl.impl.UserImpl;
import com.darwino.commons.util.NotImplementedException;
import com.darwino.commons.util.StringArray;
import com.darwino.commons.util.StringUtil;
import com.darwino.commons.util.io.StreamUtil;
import com.darwino.commons.util.io.content.TextContent;
import com.darwino.config.user.UserDir;
import com.darwino.j2ee.application.DarwinoJ2EEApplication;
import com.darwino.jsonstore.Cursor;
import com.darwino.jsonstore.Session;
import com.darwino.jsonstore.callback.CursorEntry;


public class UserDirDatabase extends UserDir {

	public static final String DATABASE		= AppDatabaseDef.DATABASE_NAME;
	public static final String STORE		= AppDatabaseDef.STORE_PERSON;


	private boolean allowUnknownUsers;
	
	private boolean delegateLoaded;
	private UserDir delegate;
	
	public UserDirDatabase() {
	}
	
	public boolean isAllowUnknownUsers() {
		return allowUnknownUsers;
	}
	public void setAllowUnknownUsers(boolean allowUnknownUsers) {
		this.allowUnknownUsers = allowUnknownUsers;
	}

	
	//
	// Authenticator
	//
	
	@Override
	public UserAuthenticator getAuthenticator(String provider) throws UserException {
		UserDir d = getDelegate();
		return d!=null ? d.getAuthenticator(provider) : null;
	}
	
	private synchronized UserDir getDelegate() {
		if(!delegateLoaded) {
			delegateLoaded = true;
			// The first name is the app, so we get the others
			String[] beanNames = DarwinoJ2EEApplication.get().getConfigurationBeanNames();
			String[] names = StringArray.remove(beanNames, 0);
			delegate = (UserDir)Platform.getService(ManagedBeansService.class).get(UserDir.BEAN_TYPE, names);
		}
		return delegate;
	}
	
	
	//
	// User directory implementation
	//

	@Override
	protected UserImpl[] _findUsers(String[] dn) throws UserException {
		try {
			UserImpl[] users = new UserImpl[dn.length];
			
			// This is the chicken and egg: this method can be called by the authentication service
			// before the session is created. So we use a temporary, system session as the current user
			// might not yet be available.
			// Moreover, we don't want to execute using the current user credentials, as we need to get
			// access to all the records.
			Session session = DarwinoJ2EEApplication.get().getLocalJsonDBServer().createSystemSession(null);
			try {
				Cursor cursor = session.getDatabase(DATABASE).getStore(STORE).openCursor().query("{pid:'${pid}'}");
				for(int i=0; i<dn.length; i++) {
					CursorEntry e = cursor.param("pid", dn[i]).findOne();
					JsonObject o = e!=null ? (JsonObject)e.getValue() : null;
					if(o!=null) {
						users[i] = createUser(o);
					} else {
						// Try the delegate
						UserImpl u = (UserImpl)getDelegate().findUserByLoginId(dn[i]);
						if(u!=null) {
							users[i] = u;
						} else if(isAllowUnknownUsers()) {
							users[i] = _createUnknownUser(dn[i]);
						}
					}
				}
				return users;
			} finally {
				StreamUtil.close(session);
			}
		} catch(JsonException ex) {
			throw new UserException(ex);
		}
	}
	
	@Override
	protected UserImpl _findUserByLoginId(String id) throws UserException {
		UserDir del = getDelegate();
		if(del!=null) {
			// Try the delegate
			UserImpl u = getDelegate().findUserByLoginId(id);
			return u;
		}
		return null;
	}
	
	protected UserImpl _createUnknownUser(String dn) throws UserException {
		String cn = calculateCNfromDN(dn);
		UserImpl u = new UserImpl(dn,cn);
		return u;
	}
	protected UserImpl createUser(final JsonObject doc) throws JsonException {
		@SuppressWarnings("serial")
		UserImpl u = new UserImpl() {
			@Override
			public HttpBinaryData getContent(String type) throws UserException {
				// Not sure this is useful...
				if(StringUtil.equals(type, com.darwino.commons.security.acl.User.CONTENT_PAYLOAD)) {
					try {
						Session session = DarwinoJ2EEApplication.get().getLocalJsonDBServer().createSystemSession(null);
						try {
							CursorEntry e = session.getDatabase(DATABASE).getStore(STORE).openCursor().query("{pid:'${pid}'}").param("pid",doc.getString("pid")).findOne();
							JsonObject o = e!=null ? (JsonObject)e.getValue() : null;
							return new HttpBinaryData(new TextContent(o.toJson(true)));
						} finally {
							StreamUtil.close(session);
						}
					} catch(Exception ex) {
						throw new UserException(ex);
					}
				}
				if(StringUtil.equals(type, User.CONTENT_PHOTO)) {
					String url = (String)getAttribute(User.ATTR_PHOTOURL);
					if(StringUtil.isNotEmpty(url)) {
						return readPhoto(url);
					}
				}
				return null;
			}
			protected HttpBinaryData readPhoto(String url) throws UserException {
				try {
					URLConnection con = new URL(url).openConnection();
					con.connect();
					String ct = con.getContentType();
					int pos = ct.indexOf(';');
					if(pos>=0) {
						ct = ct.substring(0, pos);
					}
					InputStream is = con.getInputStream();
					if(is!=null) {
						return new HttpBinaryData(ct.trim(),is);
					}
				} catch (Exception e) {
					// Just don't display a photo...
					Platform.log(e);
				}
				return null;
			}
		};
		Map<String,Object> attrs = u._getAttributes(); 
		put(attrs,com.darwino.commons.security.acl.User.ATTR_DN, doc.getString("pid"));
		put(attrs,com.darwino.commons.security.acl.User.ATTR_CN, doc.getString("pdisplayname"));
		put(attrs,com.darwino.commons.security.acl.User.ATTR_EMAIL, doc.getString("pemail"));
		put(attrs,com.darwino.commons.security.acl.User.ATTR_PHOTOURL, doc.getString("ppictureurl"));
		put(attrs,"champion", StringUtil.equals(doc.getString("pchampion"),("1")));
		put(attrs,"pictype", doc.getString("ppicturetype"));
		return u;
	}
	protected void put(Map<String,Object> attrs, String key, Object v) {
		if(v!=null) {
			attrs.put(key, v);
		}
	}	


	//
	// Search methods are not used by this directory
	//
	
	@Override
	public List<Map<String,Object>> query(String query, String[] attributes, int skip, int limit, Map<String,Object> options) throws UserException {
		throw new NotImplementedException();
	}

	@Override
	public List<Map<String,Object>> typeAhead(final String query, String[] attributes, int skip, int limit, Map<String,Object> options) throws UserException {
		throw new NotImplementedException();
	}
}
