package org.ppit.test.concept.composite;

import static org.junit.Assert.*;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptLibrary;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.IncorrectDependency;
import org.ppit.util.exception.PPITException;

public class TestCompositeConcept {
	

	@Test
	public void testCompositeConcept() {
		TestHelper helper = new TestHelper();
		ConceptManager manager = ConceptManager.getInstance();
		ConceptLibrary library = manager.getLibraryForTest();
		helper.resetAllResources();
		String cordXString = new String(
				"{ \"name\" : \"cordX\" , \"is_key\" : \"0\" , \"parent\" : \"\" , \"nucleusConceptValueAttrs\" : " +
				"[ { \"name\" : \"v\" , \"oper\" : \"IN\" , \"value\" : { \"minValue\" : \"1\" , \"maxValue\" : \"8\"}}] , \"rules\" : [ ]}"
				);
		String cordYString = new String (
				"{ \"name\" : \"cordY\" , \"is_key\" : \"0\" , \"parent\" : \"\" , \"nucleusConceptValueAttrs\" : " +
				"[ { \"name\" : \"v\" , \"oper\" : \"IN\" , \"value\" : { \"minValue\" : \"1\" , \"maxValue\" : \"8\"}}] , \"rules\" : [ ]}"
				);
		String figureTypeString = new String(
				"{ \"name\" : \"FigureType\" , \"is_key\" : \"1\" , \"parent\" : \"\" , \"nucleusConceptValueAttrs\" : " +
				"[ { \"name\" : \"v\" , \"oper\" : \"IN\" , \"value\" : { \"minValue\" : \"0\" , \"maxValue\" : \"6\"}}] , \"rules\" : [ ]}"
				);
		String figureString = new String (
				"{\"cr1\":\"0\",\"name\":\"Figure\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\"," +
				"\"parent\":\"FigureType\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"cordX\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}"
				);
		
		String pawnString = new String (
				"{\"cr1\":\"0\",\"name\":\"Pawn\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"x\"," +
				"\"parent\":\"Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}]}"
				);
		
		String neibhourFigureString = new String (
				"{\"cr1\":\"0\",\"name\":\"NP\",\"parent\":\"NP\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"p1\",\"parent\":\"Figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\"," +
				"\"maxValue\":\"8\"}}]}]}," +
				"{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"p2\",\"parent\":\"Figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"p1.x + 1\"}]}]}]}"
				);
		PrimitiveConcept cordX = null, cordY = null, figureType = null;
		CompositeConcept figure = null, pawn = null, neibhourFigure = null, duplicatedNF = null;
		try {
			// cordX
			JSONObject conceptJSON = new JSONObject(cordXString);
			cordX = manager.createPrimitiveConcept(conceptJSON);
			
			// cordY
			conceptJSON = new JSONObject(cordYString);
			cordY = manager.createPrimitiveConcept(conceptJSON);
			
			// figureType
			conceptJSON = new JSONObject(figureTypeString);
			figureType = manager.createPrimitiveConcept(conceptJSON);
			
			// Figure
			conceptJSON = new JSONObject(figureString);
			figure = manager.createCompositeConcept(conceptJSON);
			
			// pawn
			conceptJSON = new JSONObject(pawnString);
			pawn = manager.createCompositeConcept(conceptJSON);
			
			// NP
			conceptJSON = new JSONObject(neibhourFigureString);
			neibhourFigure = manager.createCompositeConcept(conceptJSON);
			
			String dup_nfString = neibhourFigure.getDuplicate("nf_d");
			
			System.out.println(neibhourFigureString);
			System.out.println(dup_nfString);
			
			conceptJSON = new JSONObject(dup_nfString);
			duplicatedNF = manager.createCompositeConcept(conceptJSON);
		} catch (PPITException e) {
			e.printStackTrace();
			return;
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		
		// check the Figure concept is contained in the list and check there are no external dependencies
		assertEquals(true, library.getListOfComposites().contains(figure));
		try {
			assertEquals(true, figure.getExternalDependencies().isEmpty());
		} catch (IncorrectDependency e1) {
			e1.printStackTrace();
			return;
		}
				
		// check the neibhourPawn concept is contained in the list and check there are no external dependencies
		assertEquals(library.getListOfComposites().contains(neibhourFigure), true);
		try {
			assertEquals(true, neibhourFigure.getExternalDependencies().isEmpty());
			assertEquals(true, duplicatedNF.getExternalDependencies().isEmpty());
		} catch (IncorrectDependency e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		}

		try {
			// update the pawn string and the concept
			pawnString = new String(
					"{\"cr1\":\"0\",\"name\":\"Pawn\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
					"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
					"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"6\"}]},{\"negated\":\"0\",\"name\":\"y\"," +
					"\"parent\":\"cordY\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
					"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}"
					);
			JSONObject conceptJSON = new JSONObject(pawnString);
			pawn = manager.createCompositeConcept(conceptJSON);
		} catch (PPITException e) {
			e.printStackTrace();
			return;
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		
		// check the neibhourPawn concept is contained in the list and check there are no external dependencies
		// TODO due to Library re-initialization (modification of Pawn abstract leads to that)
		//      The old concept gets dropped and a new one is created. So this check won't hold!
		// 		It shall be fixed and added later, after improving Update handling in GA.
		// assertEquals(library.getListOfComposites().contains(neibhourFigure), true);
		CompositeConcept nbFigure1 = library.getCompositeConcept(neibhourFigure.getName());
		assertNotNull(nbFigure1);
		try {
			assertEquals(true, nbFigure1.getExternalDependencies().isEmpty());
		} catch (IncorrectDependency e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		}
		
		// make sure one of them is free and the second one - not
		int numOfDependentAttributes = 0;
		try {
			for (Concept concept : nbFigure1.getAttributes()) {
				CompositeConcept attribute = (CompositeConcept)concept;
				if (!attribute.getExternalDependencies().isEmpty()) {				
					++numOfDependentAttributes;
					for (Concept primitiveAttribute : attribute.getAttributes()) {
						if (!primitiveAttribute.getExternalDependencies().isEmpty()) {
							break;
						}			
					}
				}
			}
			assertEquals(1, numOfDependentAttributes);
		} catch (IncorrectDependency e) {
			e.printStackTrace();
			return;
		}

		manager.initialLoadAll();
		
		Set<String> compositeNames = library.getListOfCompositeNames();
		
		assertEquals(true, compositeNames.contains("Figure"));
		assertEquals(true, compositeNames.contains("Pawn"));
		assertEquals(true, compositeNames.contains("NP"));
		assertEquals(true, compositeNames.contains("NP.p1"));
		assertEquals(true, compositeNames.contains("NP.p2"));
		assertEquals(false, compositeNames.contains("p2"));
		
		pawn = (CompositeConcept)manager.getConcept("Pawn");
		assertNotNull(pawn);
		
		//make sure pawn contains figure type attribute anymore
		boolean contionsFigureType = false;
		for (Concept attribute : pawn.getAttributes()) {
			if (((PrimitiveConcept)attribute).getType().getName().equals("FigureType"))
			{
				contionsFigureType = true;
			}
		}
		assertTrue(contionsFigureType);
				
		try {
			// update the pawn string and the concept
			pawnString = new String(
					"{\"cr1\":\"0\",\"name\":\"Pawn\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
					"[{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"cordY\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
					"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}"
					);
			JSONObject conceptJSON = new JSONObject(pawnString);
			pawn = manager.createCompositeConcept(conceptJSON);
		} catch (PPITException e) {
			e.printStackTrace();
			return;
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		
		//make sure pawn does not contain figure type attribute anymore
		contionsFigureType = false;
		for (Concept attribute : pawn.getAttributes()) {
			if (((PrimitiveConcept)attribute).getType().getName().equals("FigureType"))
			{
				contionsFigureType = true;
			}
		}
		
		assertFalse(contionsFigureType);
		
	}
}
