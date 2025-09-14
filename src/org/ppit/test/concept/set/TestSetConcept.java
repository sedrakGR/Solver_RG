package org.ppit.test.concept.set;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptLibrary;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.set.SetConcept;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.IncorrectDependency;
import org.ppit.util.exception.PPITException;

public class TestSetConcept {
	
	@Test
	public void testSetConcept() {
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
				"{\"cr1\":\"0\",\"name\":\"Figure\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\"," +
				"\"parent\":\"FigureType\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"cordX\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}"
				);
		
		String lineString = new String (
				"{\"name\":\"Line\",\"parent\":\"\",\"attribute\":{\"cr1\":\"0\",\"name\":\"element\",\"parent\":\"\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"Figure\",\"parent\":\"Figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"0\"}]}]}]},\"setManAttr\":" +
				"[{\"name\":\"count\",\"oper\":\"IN\",\"value\":{\"minValue\":\"5\",\"maxValue\":\"5\"}}],\"setAddAttr\":[]}");
		
		String emptyLineString = new String (
				"{\"cr1\":\"0\",\"name\":\"emptyLine\",\"parent\":\"emptyLine\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"line\",\"parent\":\"Line\",\"attribute\":{\"cr1\":\"0\",\"name\":\"element\",\"parent\":\"Line.element\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"Figure\",\"parent\":\"Line.element.Figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Line.element.Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"emptyLine.addX\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Line.element.Figure.ft\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"0\"}]}]}]},\"setManAttr\":" +
				"[{\"name\":\"count\",\"oper\":\"IN\",\"value\":{\"minValue\":\"emptyLine.addY\",\"maxValue\":\"5\"}}],\"setAddAttr\":[]}," +
				"{\"negated\":\"0\",\"name\":\"addX\",\"parent\":\"cordX\"," +
				"\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}," +
				"{\"negated\":\"0\",\"name\":\"addY\",\"parent\":\"cordY\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}]}"
				);
		PrimitiveConcept addX = null, addY = null;
		CompositeConcept emptyLine = null;
		SetConcept line = null, l = null;
		try {
			// cordX
			JSONObject conceptJSON = new JSONObject(cordXString);
			manager.createPrimitiveConcept(conceptJSON);
			
			// cordY
			conceptJSON = new JSONObject(cordYString);
			manager.createPrimitiveConcept(conceptJSON);
			
			// figureType
			conceptJSON = new JSONObject(figureTypeString);
			manager.createPrimitiveConcept(conceptJSON);
			
			// Figure
			conceptJSON = new JSONObject(figureString);
			manager.createCompositeConcept(conceptJSON);
			
			// Line
			conceptJSON = new JSONObject(lineString);
			line = manager.createSetConcept(conceptJSON);
			
			// EmptyLine
			conceptJSON = new JSONObject(emptyLineString);
			emptyLine = manager.createCompositeConcept(conceptJSON);
			
		} catch (PPITException e) {
			e.printStackTrace();
			fail("cannot create a concept");
			return;
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		
		assertTrue(library.getListOfSets().contains(line));
		assertTrue(library.getListOfComposites().contains(emptyLine));
		try {
			assertTrue(line.getExternalDependencies().isEmpty());
		} catch (IncorrectDependency e) {
			e.printStackTrace();
			fail("failed to get dependencies of the set concept line");
		}
		try {
			for (Concept concept : emptyLine.getAttributes()) {
				if (concept instanceof SetConcept)
				{
					l = (SetConcept)concept;
					System.out.println("I'm here, yeah!!!!!");
					assertFalse(l.getExternalDependencies().isEmpty());
				}
				else
				{
					if (concept.getName().equals("emptyLine.addX"))
					{
						addX = (PrimitiveConcept)concept;
					}
					else
					{
						addY = (PrimitiveConcept)concept;
					}
				}
			}
			// set dependencies, but first we have to initiate the list of dependencies
			emptyLine.evaluateDependencies();
			
			// reset the library
			emptyLine = null;
			addX = null;
			addY = null;
			line = null;
			l = null;
			library.reset();
			assertFalse(library.getListOfSetNames().contains("Line"));
			
			// reload the library from DB
			manager.initialLoadAll();
			assertTrue(library.getListOfSetNames().contains("Line"));
			assertTrue(library.getListOfSetNames().contains("emptyLine.line"));
			assertTrue(library.getListOfCompositeNames().contains("emptyLine"));
			
			emptyLine = (CompositeConcept)manager.getConcept("emptyLine");
			emptyLine.getExternalDependencies();
		} catch (IncorrectDependency e) {
			e.printStackTrace();
			fail("failed to get dependencies of the composite concept emptyLine");
		}
	}
}
