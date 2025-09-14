package org.ppit.test;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.ConceptLibrary;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.db.DBHandler;
import org.ppit.util.exception.BrokenCR1;

public class TestHelper {
	public ConceptManager m_conceptManager = ConceptManager.getInstance();
	public ConceptLibrary m_library = m_conceptManager.getLibraryForTest();
	public CognitionManager m_cogManager = CognitionManager.getInstance(); 
	public DBHandler m_dbHandler = DBHandler.getInstance();
	
	private ArrayList<String> m_primitiveStrings = new ArrayList<String>();
	// index of the current primitive concept string in the list
	private int m_primitiveIndex = 0;
	private int m_groupId = 0;
	
	
	private ArrayList<String> m_compositeStrings = new ArrayList<String>();
	private ArrayList<String> m_setStrings = new ArrayList<String>();
	private ArrayList<String> m_actionStrings = new ArrayList<String>();
	
	public TestHelper() {
		initiatePrimitives();
		initiateComposites();
		initiateSets();
		initiateActions();
	}
	
	private void initiatePrimitives() {
		m_primitiveStrings.add(new String(
				"{ \"nucleusConceptValueAttrs\" : [ { \"oper\" : \">\" , \"name\" : \"a\" , \"value\" : \"0\"}] , " +
				"\"is_key\" : \"false\" , \"level\" : \"0\" , \"name\" : \"p1\" , \"parent\" : \"\" , \"rules\" : [ ]}"
				));
		m_primitiveStrings.add(new String(
				"{ \"nucleusConceptValueAttrs\" : [ { \"oper\" : \"<\" , \"name\" : \"a\" , \"value\" : \"100\"}] , " +
				"\"is_key\" : \"false\" , \"name\" : \"p2\" , \"parent\" : \"p1\" }"
				));
	}
	
	private void initiateComposites() {
		//TODO:
	}
	
	private void initiateSets() {
		//TODO:
	}
	
	private void initiateActions() {
		//TODO:
	}
	
	public NucleusInstance getNucleusInstance() throws BrokenCR1 {
		IdGroup idg = new IdGroup(m_groupId++);
		// The PrimitiveType and GANode attributes are passed nulls.
		return new NucleusInstance(0, null, idg, null);
	}
	
	public PrimitiveConcept getNextPrimitiveConcept() {
		JSONObject conceptJSON = null;
		try {
			conceptJSON = new JSONObject(m_primitiveStrings.get(m_primitiveIndex++));
		} catch (JSONException e) {
			//TODO:
		}
		if (m_primitiveIndex == m_primitiveStrings.size()) {
			--m_primitiveIndex;
		}
		PrimitiveConcept concept = null;
		try {
			if (conceptJSON != null)
				concept = m_conceptManager.createPrimitiveConcept(conceptJSON);
			else
				concept = getNextPrimitiveConcept();
		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return concept;
	}
	
	public void resetAllResources() {
		m_dbHandler.resetDB();
		m_library.reset();
		m_cogManager.reset();
	}
}
