package org.ppit.actions.composite;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.ActionSupport;


public class SaveComposite extends ActionSupport {
	
	private JSONObject m_compositeConc;
	private String m_compositeName = null;
	private ConceptManager m_manager = ConceptManager.getInstance();
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveComposite() {
        super();
    }
	
	public String execute() {
		try {
			m_compositeConc = new JSONObject(m_compositeName); 
			m_manager.createCompositeConcept(m_compositeConc);		
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (PPITException e) {
			System.out.println("Error when saving the composite concept" + e.getMessage());
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public void setCompositeName(String compositeName) {
		m_compositeName = compositeName;
	}
}

