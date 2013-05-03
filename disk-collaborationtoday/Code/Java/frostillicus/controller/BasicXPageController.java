package frostillicus.controller;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;

public class BasicXPageController implements XPageController {
	private static final long serialVersionUID = 1L;

	public BasicXPageController() { }

	public void beforePageLoad() throws Exception { }
	public void afterPageLoad() throws Exception { }

	public void beforeRenderResponse(PhaseEvent event) throws Exception { }
	public void afterRenderResponse(PhaseEvent event) throws Exception { }

	public void afterRestoreView(PhaseEvent event) throws Exception { }

	protected static Object resolveVariable(String varName) {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().getVariableResolver().resolveVariable(context, varName);
	}

	@SuppressWarnings("unchecked")
	protected static Map<String, Object> getViewScope() {
		return (Map<String, Object>)resolveVariable("viewScope");
	}
}
