package org.ppit.actions.plan;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;
import com.opensymphony.xwork2.ActionSupport;

public class SavePlan extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private JSONObject m_planJSON;
	private String m_planString = null;
	private ConceptManager m_manager = ConceptManager.getInstance();
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SavePlan() {
        super();
    }
	
	public String execute() {
		try {
			m_planJSON = new JSONObject(m_planString); 
			m_manager.createPlan(m_planJSON);		
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (PPITException e) {
			System.out.println("Error when saving the the plan" + e.getMessage());
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public void setPlan(String plan) {
		m_planString = plan;
	}
}

