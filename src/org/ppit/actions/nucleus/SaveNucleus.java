package org.ppit.actions.nucleus;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.ActionSupport;


public class SaveNucleus extends ActionSupport {
	
	private JSONObject m_nucleusConc;
	private String m_nucleusList = null;
	private String m_nucleusName = null;

	private ConceptManager m_manager = ConceptManager.getInstance();
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveNucleus() {
        super();
    }
	
	public String execute() {
		try {
			m_nucleusConc = new JSONObject(m_nucleusName);
			m_manager.createPrimitiveConcept(m_nucleusConc);
			m_nucleusList = m_manager.getNucleusNames();
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (PPITException e) {
			System.out.println("Error when saving a nucleus concept" + e.getMessage());
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public String getNucleusList() {
		return m_nucleusList;
	}

	public void setNucleusName(String nucleusName) {
		m_nucleusName = nucleusName;
	}
}

