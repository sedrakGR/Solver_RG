package org.ppit.actions.goal;
import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;
import com.opensymphony.xwork2.ActionSupport;

public class SaveGoal extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private JSONObject m_goalJSON;
	private String m_goal = null;
	private ConceptManager m_manager = ConceptManager.getInstance();
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveGoal() {
        super();
    }
	
	public String execute() {
		try {
			m_goalJSON = new JSONObject(m_goal); 
			if (m_manager.createGoal(m_goalJSON) != null) {
				return SUCCESS;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (PPITException e) {
			System.out.println("Error when saving the the goal" + e.getMessage());
			e.printStackTrace();
		}
		return ERROR;
	}

	public void setGoal(String goal) {		
		m_goal = goal;
	}
	
}

