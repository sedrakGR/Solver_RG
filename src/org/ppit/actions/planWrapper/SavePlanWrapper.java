package org.ppit.actions.planWrapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;
import com.opensymphony.xwork2.ActionSupport;

public class SavePlanWrapper extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private JSONObject m_planWrapperJSON;
	private String m_planWrapperString = null;
	private ConceptManager m_manager = ConceptManager.getInstance();
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SavePlanWrapper() {
        super();
    }
	
	public String execute() {
		try {
			m_planWrapperJSON = new JSONObject(m_planWrapperString); 
			m_manager.createPlanWrapper(m_planWrapperJSON);		
		} catch (JSONException e) {
			e.printStackTrace();
			return ERROR;
		} catch (PPITException e) {
			System.out.println("Error when saving the the plan" + e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public void setPlanWrapper(String planWrapper) {
		m_planWrapperString = planWrapper;
	}
}

