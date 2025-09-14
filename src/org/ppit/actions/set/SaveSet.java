package org.ppit.actions.set;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.set.SetConcept;
import org.ppit.db.DBSetManager;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.ActionSupport;

public class SaveSet extends ActionSupport {

	private JSONObject m_setConc;
	private String m_setName = null;
	private ConceptManager m_manager = ConceptManager.getInstance();

	public SaveSet() {
		super();
	}

	public String execute() {
		try {
			m_setConc = new JSONObject(m_setName);
			m_manager.createSetConcept(m_setConc);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (PPITException e) {
			System.out.println("Error when saving a set concept" + e.getMessage());
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public void setSetName(String setName) {
		m_setName = setName;
	}
}
