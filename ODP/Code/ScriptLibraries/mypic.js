/*
 * © Copyright 2012 ZetaOne Solutions Group, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
dojo.provide("com.ZetaOne.mypic");

( function() {
	dojo.declare(
		"com.ZetaOne.mypic",
		null,
		{
			showProfileCard : function(userId, ownerControl, serviceUrl) {
				var dialog = new dijit.TooltipDialog( {
					id : 'profileCardPopUp',
					style : {
						width : '387px'
					},
					href : serviceUrl + 'profileCard.xsp?id=' + userId,
					onClose : function() {
						this.destroy();
					},
					onBlur : function() {
						this.destroy();
					}
				});
				dojo.query('.dijitTooltipContainer', dialog.domNode).style({
							padding : 0
						});
				dojo.query('.dijitTooltipContents', dialog.domNode).style({
							padding : 0
						});
				dijit.popup.open({
					around : dojo.byId(ownerControl),
					popup : dialog
				});
				return false;
			},

			doLogin : function(user, pass, errorPanel, successURL) {
				dojo.xhrPost({
					url : '/names.nsf?login&redirectto=' + window.location.href,
					handleAs : 'text',
					preventCache : true,
					content : {
						"UserName" : user,
						"Password" : pass
					},
					load : function(response, ioArgs) {
						if (response
								.indexOf('action="/names.nsf?Login"') == -1) {
							document.cookie = "BackupSesID=; expires=Thu, 01-Jan-70 00:00:01 GMT";
							window.location.href = successURL;
						} else {
							dojo
									.xhrGet( {
										url : '/names.nsf?logout',
										handleAs : "text",
										preventCache : true,
										load : function(
												response,
												ioArgs) {
											if (document.cookie
													.indexOf('BackupSesId=') != -1) {
												document.cookie = "DomAuthSessId="
														+ document.cookie
																.split('BackupSesID=')[1]
																.substr(
																		0,
																		32);
											}
											document.cookie = "BackupSesId=; expires=Thu, 01-Jan-70 00:00:01 GMT";
											dojo.html.set(errorPanel, "<span>You have entered an invalid<br>username or password, or do not<br>have sufficient credentials.</span>");
										},
										error : function(
												response,
												ioArgs) {
											dojo.html.set(errorPanel, "<span>You have entered an invalid<br>username or password, or do not<br>have sufficient credentials.</span>");
										}
									})
						}
					},
					error : function(response, ioArgs) {
						dojo.html.set(errorPanel,"<span>You have entered an invalid<br>username or password, or do not<br>have sufficient credentials.<span>");
					}
				})
			}
		});
})();

XSP.addOnLoad( function() {
	mypic = new com.ZetaOne.mypic();
})