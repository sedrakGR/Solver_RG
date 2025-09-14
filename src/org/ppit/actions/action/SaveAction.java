package org.ppit.actions.action;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.util.exception.PPITException;
import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;


public class SaveAction extends ActionSupport {
	
	private String m_actionData = "";

	private ConceptManager m_conceptManager = ConceptManager.getInstance();
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveAction() {
        super();
    }
	
	public String execute() {
		try {
			JSONObject actionJSON = new JSONObject(m_actionData);							
			m_conceptManager.createAction(actionJSON);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (PPITException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public void setActionData(String actionData) {
		m_actionData = actionData;
	}
}
