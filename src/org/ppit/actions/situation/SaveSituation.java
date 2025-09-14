package org.ppit.actions.situation;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.percept.SituationManager;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.ActionSupport;

public class SaveSituation extends ActionSupport {

	private JSONObject m_situation = null;
	private String m_situationString = null;
	private SituationManager m_manager = SituationManager.getInstance();
    
    public SaveSituation() {
        super();
    }
	
	public String execute() {
		try {
			m_situation = new JSONObject(m_situationString);							
			m_manager.createSituation(m_situation);			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (PPITException e) {
			System.out.println("Error when saving the composite concept" + e.getMessage());
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public void setSituationString(String situationString) {
		m_situationString = situationString;
	}
}

