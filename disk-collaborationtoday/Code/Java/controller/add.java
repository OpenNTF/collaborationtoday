package controller;

import java.util.*;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.servlet.http.HttpServletResponse;
import org.openntf.news.http.core.Parser;
import frostillicus.controller.BasicXPageController;

public class add extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	@Override
	public void beforeRenderResponse(PhaseEvent event) throws Exception {
		super.beforeRenderResponse(event);

		ExternalContext exCon = FacesContext.getCurrentInstance().getExternalContext();

		HttpServletResponse response = (HttpServletResponse)exCon.getResponse();
		response.setHeader("Cache-Control", "no-cache");
	}

	public void parseURL() {
		Map<String, Object> viewScope = getViewScope();
		String resultUNID = Parser.getOutput((String)viewScope.get("addingURL"));
		viewScope.put("parsedDocUNID", resultUNID);
	}
}
