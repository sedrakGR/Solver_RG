package org.ppit.test.concept.generalization;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.rules.IRule;
import org.ppit.core.concept.rules.RuleE;
import org.ppit.core.concept.rules.RuleG;
import org.ppit.core.concept.rules.RuleGE;
import org.ppit.core.concept.rules.RuleIn;
import org.ppit.core.concept.rules.RuleL;
import org.ppit.core.concept.rules.RuleLE;
import org.ppit.core.concept.rules.RuleNE;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.PPITException;


public class TestRelationExtraction {
	TestHelper m_testHelper = new TestHelper();

	@Test
	public void testRuleGeneralization() {
		
		m_testHelper.resetAllResources();
		ConceptManager c_manager = ConceptManager.getInstance();
		//SituationManager s_manager = SituationManager.getInstance();
		//CognitionManager m_cognition = CognitionManager.getInstance();
		
		 
		String xName = ("X");
		String yName = ("Y");
		String fType = ("FT");
		String fColor = ("FC");
	
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
				"{\"" +
						"cr1\":\"0\"," +
						"\"name\":\"NeighborPawn\"," +
						"\"parent\":\"NeighborPawn\"," +
						"\"compositeConceptIndexAttrs\":[]," +
						"\"compConceptAttrs\":[" +
													"{\"name\":\"p1\"," +
													"\"parent\":\"Pawn\"," +
													"\"type\":\"c\"," +
													"\"negated\":\"\"," +
													"\"compositeConceptIndexAttrs\":[]," +
													"\"compConceptAttrs\":[" +
																		"{\"name\":\"ft\"," +
																		"\"parent\":\"Pawn.ft\"," +
																		"\"type\":\"p\"," +
																		"\"negated\":\"\"," +
																		"\"nucleusConceptIndexAttrs\":[]," +
																		"\"nucleusConceptValueAttrs\":[" +
																										"{\"name\":\"v\"," +
																										"\"oper\":\"=\"," +
																										"\"value\":\"1\"" +
																										"}" +
																									"]" +
																		"}," +
																		"{\"name\":\"y\"," +
																		"\"parent\":\"Pawn.y\"," +
																		"\"type\":\"p\"," +
																		"\"negated\":\"\"," +
																		"\"nucleusConceptIndexAttrs\":[]," +
																		"\"nucleusConceptValueAttrs\":[" +
																										"{\"name\":\"v\"," +
																										"\"oper\":\"IN\"," +
																										"\"value\":{\"minValue\":\"2\"," +
																													"\"maxValue\":\"7\"" +
																													"}" +
																										"}" +
																									   "]" +
																		"}," +
																		"{\"name\":\"x\"," +
																		"\"parent\":\"Pawn.x\"," +
																		"\"type\":\"p\"," +
																		"\"negated\":\"\"," +
																		"\"nucleusConceptIndexAttrs\":[]," +
																		"\"nucleusConceptValueAttrs\":[" +
																										"{\"name\":\"v\"," +
																										 "\"oper\":\"IN\"," +
																										  "\"value\":{\"minValue\":\"1\"," +
																														"\"maxValue\":\"8\"" +
																													  "}" +
																										"}" +
																									   "]}," +
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
			CompositeConcept figure = c_manager.createCompositeConcept(tmpJSON);
			figure.getJSON();
			
			tmpJSON = new JSONObject(dummyString);
			CompositeConcept dummy = c_manager.createCompositeConcept(tmpJSON);
			dummy.getJSON();
			
			tmpJSON = new JSONObject(pawnString);
			CompositeConcept pawn = c_manager.createCompositeConcept(tmpJSON);
			pawn.getJSON();
			
			tmpJSON = new JSONObject(kingString);
			CompositeConcept king = c_manager.createCompositeConcept(tmpJSON);
			king.getJSON();
			
			tmpJSON = new JSONObject(knightString);
			CompositeConcept knight = c_manager.createCompositeConcept(tmpJSON);
			knight.getJSON();
			
			tmpJSON = new JSONObject(neigPawnString);
			c_manager.createCompositeConcept(tmpJSON);
			
			IRule rule0 = new RuleIn("0, p.i");
			TestHelper m_testHelper = new TestHelper();
			NucleusInstance ni = m_testHelper.getNucleusInstance();
			ni.setValue(10);
			rule0.setDependency("p.i", ni );
			
			/* testing our conjunction function for rules */
			IRule ruleIn1 = new RuleIn("0, 10");
			IRule ruleIn2 = new RuleIn("5, 15");
			IRule ruleIn3 = new RuleIn("5, 10");
			IRule ruleIn4 = new RuleIn("3, 8");
			
			IRule ruleE1 = new RuleE("1, 5");
			IRule ruleE2 = new RuleE("5");
			IRule ruleE3 = new RuleE("3, 7");
			IRule ruleE4 = new RuleE("7");

			IRule ruleNE1 = new RuleNE("1, 5");
			
			IRule ruleGE1 = new RuleGE("4");
			IRule ruleGE2 = new RuleGE("0");
			
			IRule ruleG1 = new RuleG("1");
			IRule ruleG2 = new RuleG("3");
			IRule ruleG3 = new RuleG("4");
			
			IRule ruleL1 = new RuleL("5");
			IRule ruleL2 = new RuleL("8");
			
			IRule ruleLE1 = new RuleLE("15");
			
			ArrayList<IRule> rulegroup = new ArrayList<IRule>();
			rulegroup.add(new RuleE("0"));
			rulegroup.add(new RuleIn("2, 4"));
			rulegroup.add(new RuleGE("6"));
			
			assertEquals("Result", ruleIn3.getJSON(), ruleIn1.conjunction(ruleIn2).get(0).getJSON());
			
			assertEquals("Result", ruleE2.getJSON(), ruleIn4.conjunction(ruleE1).get(0).getJSON());
			
			assertEquals("Result", ruleE4.getJSON(), ruleE3.conjunction(ruleGE1).get(0).getJSON());
			
			assertEquals("Result", rulegroup.get(0).getJSON(), ruleNE1.conjunction(ruleGE2).get(0).getJSON());
			assertEquals("Result", rulegroup.get(1).getJSON(), ruleNE1.conjunction(ruleGE2).get(1).getJSON());
			assertEquals("Result", rulegroup.get(2).getJSON(), ruleNE1.conjunction(ruleGE2).get(2).getJSON());
			
			assertEquals("Result", ruleGE1.getJSON(), ruleG1.conjunction(ruleG2).get(0).getJSON());
			
			assertEquals("Result", ruleIn2.getJSON(), ruleLE1.conjunction(ruleG3).get(0).getJSON());
			
			assertEquals("Result", ruleL1.getJSON(), ruleL1.conjunction(ruleL2).get(0).getJSON());
			
			
			//here you can see result of any two rules conjunction
			/*for(IRule i: rule1.conjunction(rule2)) {
				System.out.print(i.getOperator() + "\t");
				System.out.println(i.getValues());
			}*/
			
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
