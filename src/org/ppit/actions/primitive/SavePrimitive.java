package org.ppit.actions.primitive;


import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.ActionSupport;

public class SavePrimitive extends ActionSupport {
	
	private JSONObject m_primConc;
	private String m_primitiveList;
	private String m_prim;

	private ConceptManager m_manager = ConceptManager.getInstance();    
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	public String execute() {
		try {
			m_primConc = new JSONObject(m_prim);
			try {
				m_manager.createPrimitiveConcept(m_primConc);
			} catch (PPITException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			m_primitiveList = m_manager.getPrimitiveNames();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	/**
	 * we would wish to change the name to getPrimitiveList! 
	 * @return
	 */
	public String getNucleusList() {
		return m_primitiveList;
	}

	public void setPrim(String prim) {
		m_prim = prim;
	}
}
