package org.ppit.test.situation;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ppit.core.brain.gaNode.listener.GAListenerCollecter;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.percept.Situation;
import org.ppit.core.percept.SituationManager;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.PPITException;


public class TestSituationCreation {

	TestHelper m_testHelper = new TestHelper();

	@Test
	public void testSituationCreation() {
		
		m_testHelper.resetAllResources();
		ConceptManager c_manager = ConceptManager.getInstance();
		SituationManager s_manager = SituationManager.getInstance();
		CognitionManager m_cognition = CognitionManager.getInstance();
		
		/**
		 * P = Pawn 1, K = King 6, Q = Queen 5, R = Rook 4, B = Bishop 2, N = knight 3, D = Dummy 0
		 * W = White 1, B = Black 2, D = Dummy 0
		 *  
		 * 8  DD DD DD DD DD DD DD DD
		 * 7  DD DD DD DD DD DD DD DD
		 * 6  DD DD DD DD DD DD DD DD
		 * 5  DD DD DD DD DD DD DD DD
		 * 4  DD DD DD WP DD DD DD DD
		 * 3  DD WN WP DD DD DD DD DD
		 * 2  WP WP DD DD DD DD DD DD
		 * 1  DD DD DD WK DD DD DD DD
		 * 
		 *    1  2  3  4  5  6  7  8
		 *    
		 * The Matrix bellow are reversed a little (to match the index to the position in the above picture.
		 */
		
		int[][] fc = {
				{0, 0, 0, 1, 0, 0, 0, 0}, 
				{1, 1, 0, 0, 0, 0, 0, 0},
				{0, 1, 1, 0, 0, 0, 0, 0},
				{0, 0, 0, 1, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0}
		};
		int[][] ft = {
				{0, 0, 0, 6, 0, 0, 0, 0}, 
				{1, 1, 0, 0, 0, 0, 0, 0},
				{0, 3, 1, 0, 0, 0, 0, 0},
				{0, 0, 0, 1, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0}
		};
		
		 
		String xName = ("X");
		String yName = ("Y");
		String fType = ("FT");
		String fColor = ("FC");

		int groupId = 1;
		String situationString = new String ("{\"name\":\"sit1\",\"elements\":[");
		for(int y = 0; y < 8; ++y) {
			for (int x = 0; x < 8; ++x, ++groupId) {
				// I officially declare that Java is a sun of a beach!
				// This shit, threats 1 in a "x + 1" expression in a string not as a value :(
				// So, "()" shall be used! Fuck it!
				situationString += 
				"{\"groupid\":\"" + groupId + "\",\"instances\":[" +
				"{\"type\":\"" + xName + "\",\"value\":\"" + (x + 1) + "\"}," +
				"{\"type\":\"" + yName + "\",\"value\":\"" + (y + 1) + "\"}," +
				"{\"type\":\"" + fType + "\",\"value\":\"" + ft[y][x] + "\"}," +
				"{\"type\":\"" + fColor + "\",\"value\":\"" + fc[y][x] + "\"}" +
				"]}";
				
				if(!(x == 7 && y == 7)) {
					situationString += ",";
				}
			}
		}
		situationString += "]}";
		
		String cordXString = new String(
				"{ \"name\" : \"" + xName + "\" , \"is_key\" : \"0\" , \"parent\" : \"\" , \"nucleusConceptValueAttrs\" : " +
				"[ { \"name\" : \"v\" , \"oper\" : \"IN\" , \"value\" : { \"minValue\" : \"1\" , \"maxValue\" : \"8\"}}] , \"rules\" : [ ]}"
				);
		String cordYString = new String (
				"{ \"name\" : \"" + yName + "\" , \"is_key\" : \"0\" , \"parent\" : \"\" , \"nucleusConceptValueAttrs\" : " +
				"[ { \"name\" : \"v\" , \"oper\" : \"IN\" , \"value\" : { \"minValue\" : \"1\" , \"maxValue\" : \"8\"}}] , \"rules\" : [ ]}"
				);
		String figureTypeString = new String(
				"{ \"name\" : \"" + fType + "\" , \"is_key\" : \"0\" , \"parent\" : \"\" , \"nucleusConceptValueAttrs\" : " +
				"[ { \"name\" : \"v\" , \"oper\" : \"IN\" , \"value\" : { \"minValue\" : \"0\" , \"maxValue\" : \"6\"}}] , \"rules\" : [ ]}"
				);
		String figureColorString = new String(
				"{ \"name\" : \"" + fColor + "\" , \"is_key\" : \"1\" , \"parent\" : \"\" , \"nucleusConceptValueAttrs\" : " +
				"[ { \"name\" : \"v\" , \"oper\" : \"IN\" , \"value\" : { \"minValue\" : \"0\" , \"maxValue\" : \"2\"}}] , \"rules\" : [ ]}"
				);
		
		String figureString = new String(
				"{\"cr1\":\"1\",\"name\":\"Figure\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"name\":\"x\",\"parent\":\"X\",\"type\":\"n\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"name\":\"y\",\"parent\":\"Y\",\"type\":\"n\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"name\":\"ft\",\"parent\":\"FT\",\"type\":\"n\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"name\":\"fc\",\"parent\":\"FC\",\"type\":\"n\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}]}"
				);
		
		String dummyString = new String (
				"{\"cr1\":\"1\",\"name\":\"Dummy\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"0\"}]}," +
				"{\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"name\":\"fc\",\"parent\":\"Figure.fc\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"0\"}]}," +
				"{\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}"
				);

		String pawnString = new String (
				"{\"cr1\":\"1\",\"name\":\"Pawn\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}," +
				"{\"name\":\"fc\",\"parent\":\"Figure.fc\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}," +
				"{\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}"
				);

		String kingString = new String (
				"{\"cr1\":\"1\",\"name\":\"King\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"6\"}]}," +
				"{\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"name\":\"fc\",\"parent\":\"Figure.fc\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}," +
				"{\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}"
				);
		
		String knightString = new String (
				"{\"cr1\":\"1\",\"name\":\"Knight\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"3\"}]}," +
				"{\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"name\":\"fc\",\"parent\":\"Figure.fc\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}," +
				"{\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}"
				);
		
		String neigPawnString = new String (
				"{\"cr1\":\"0\",\"name\":\"NeighborPawn\",\"parent\":\"NeighborPawn\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"name\":\"p1\",\"parent\":\"Pawn\",\"type\":\"c\",\"negated\":\"\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"name\":\"ft\",\"parent\":\"Pawn.ft\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"name\":\"y\",\"parent\":\"Pawn.y\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}," +
				"{\"name\":\"x\",\"parent\":\"Pawn.x\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"name\":\"fc\",\"parent\":\"Pawn.fc\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}]}," +
				"{\"name\":\"p2\",\"parent\":\"Pawn\",\"type\":\"c\",\"negated\":\"\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"name\":\"ft\",\"parent\":\"Pawn.ft\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"name\":\"y\",\"parent\":\"Pawn.y\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"p1.y\"}]}," +
				"{\"name\":\"x\",\"parent\":\"Pawn.x\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"p1.x+1\"}]}," +
				"{\"name\":\"fc\",\"parent\":\"Pawn.fc\",\"type\":\"p\",\"negated\":\"\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}]}]}"
				);
				
		GAListenerCollecter xListener = null; 
		GAListenerCollecter yListener = null;
		GAListenerCollecter ftListener = null;
		GAListenerCollecter fcListener = null;
		
		GAListenerCollecter figureListener;
		GAListenerCollecter pawnListener;
		GAListenerCollecter kingListener;
		GAListenerCollecter dummyListener;
		GAListenerCollecter knightListener;
		GAListenerCollecter neigPawnListener;
		
				
		Situation stu1 = null;
		try {
			JSONObject tmpJSON = new JSONObject(cordXString);
			c_manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(cordYString);
			c_manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(figureTypeString);
			c_manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(figureColorString);
			c_manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(figureString);
			c_manager.createCompositeConcept(tmpJSON);

			tmpJSON = new JSONObject(dummyString);
			c_manager.createCompositeConcept(tmpJSON);

			tmpJSON = new JSONObject(pawnString);
			c_manager.createCompositeConcept(tmpJSON);
			
			tmpJSON = new JSONObject(kingString);
			c_manager.createCompositeConcept(tmpJSON);
			
			tmpJSON = new JSONObject(knightString);
			c_manager.createCompositeConcept(tmpJSON);

			tmpJSON = new JSONObject(neigPawnString);
			c_manager.createCompositeConcept(tmpJSON);

			
			// Assign listeners.
			xListener = m_cognition.getWM().getActivatedInstances(xName);
			yListener = m_cognition.getWM().getActivatedInstances(yName);
			ftListener = m_cognition.getWM().getActivatedInstances(fType);
			fcListener = m_cognition.getWM().getActivatedInstances(fColor);
			
			figureListener = m_cognition.getWM().getActivatedInstances("Figure");
			pawnListener = m_cognition.getWM().getActivatedInstances("Pawn");
			kingListener = m_cognition.getWM().getActivatedInstances("King");
			knightListener = m_cognition.getWM().getActivatedInstances("Knight");
			dummyListener = m_cognition.getWM().getActivatedInstances("Dummy");
			neigPawnListener = m_cognition.getWM().getActivatedInstances("NeighborPawn");
			
			tmpJSON = new JSONObject(situationString);
			stu1 = s_manager.createSituation(tmpJSON);
			
			//reset all and reload
			s_manager.getLibraryForTest().reset();
			s_manager.initialLoadSituations();
			
			tmpJSON = new JSONObject(situationString);
			stu1 = s_manager.createSituation(tmpJSON);
			
			assertEquals(64, stu1.getElements().size());
			
			assertEquals(0, figureListener.getActiveInstanceCount());
			assertEquals(0, pawnListener.getActiveInstanceCount());
			assertEquals(0, kingListener.getActiveInstanceCount());
			assertEquals(0, knightListener.getActiveInstanceCount());
			assertEquals(0, dummyListener.getActiveInstanceCount());
			assertEquals(0, neigPawnListener.getActiveInstanceCount());
			
			m_cognition.processSituation(stu1);
			
			assertEquals(64, xListener.getActiveInstanceCount());
			assertEquals(64, yListener.getActiveInstanceCount());
			assertEquals(64, ftListener.getActiveInstanceCount());
			assertEquals(64, fcListener.getActiveInstanceCount());
			
			assertEquals(64, figureListener.getActiveInstanceCount());
			assertEquals(4, pawnListener.getActiveInstanceCount());
			assertEquals(1, kingListener.getActiveInstanceCount());
			assertEquals(1, knightListener.getActiveInstanceCount());
			assertEquals(58, dummyListener.getActiveInstanceCount());
			
			assertEquals(1, neigPawnListener.getActiveInstanceCount());
		} catch (JSONException e) {
			e.printStackTrace();
			fail(e.getMessage());
			return;
		} catch (PPITException e) {
			e.printStackTrace();
			fail(e.getMessage());
			return;
		}
	}
}
