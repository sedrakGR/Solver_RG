package org.ppit.test.concept.primitive;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ppit.core.brain.GA;
import org.ppit.core.brain.GACreator;
import org.ppit.core.brain.gaNode.listener.GAListenerCollecter;
import org.ppit.core.brain.gaNode.nucleus.GANucleus;
import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.ConceptLibrary;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.*;

public class TestPrimitiveConcept {
	
	TestHelper m_testHelper = new TestHelper();
	
	/**
	 * The Graph of Abstracts are used to test the filtering of Nucleus instances.
	 */
	GA m_ga = new GA();
	GACreator m_gaCreator = new GACreator(m_ga);
	
	@Test
	public void testPrimitiveConcept() {
		m_testHelper.resetAllResources();
		
		ConceptManager manager = ConceptManager.getInstance();
		ConceptLibrary library = manager.getLibraryForTest();

		String concString = new String(
				"{ \"nucleusConceptValueAttrs\" : [ { \"oper\" : \">\" , \"name\" : \"a\" , \"value\" : \"111\"}] , " +
				"\"is_key\" : \"false\" , \"level\" : \"0\" , \"name\" : \"p1\" , \"parent\" : \"\" , \"rules\" : [ ]}"
				);
		
		PrimitiveConcept p1 = null;
		GAListenerCollecter listener = new GAListenerCollecter();
		GANucleus p1_node = null;
		try {
			JSONObject conceptJSON = new JSONObject(concString);
			p1 = manager.createPrimitiveConcept(conceptJSON);
			
			p1_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(p1);
			listener.registerTo(p1_node);
			
		} catch (PPITException e) {
			e.printStackTrace();
			System.out.println("TEST FAILED");
			fail("Could Not Create Primitive Concept");
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("Could not create json from the given string");
			return;
		}
		
		// make sure it is added in both primitive types and primitive concept list.
		assertEquals(library.getListOfTypes().contains(p1.getType()), true);
		assertEquals(library.getListOfPrimitives().contains(p1), true);
		
		assertNotNull(p1_node);
		try {
			IdGroup idg = new IdGroup(0);
			NucleusInstance conceptInstance = new NucleusInstance(0, p1.getType(), idg, p1_node);
			
			// Make sure filtering value works as expected
			conceptInstance.setValue(100);
			assertEquals(p1_node.filterInstance(conceptInstance), false);
			
			conceptInstance.setValue(111);
			assertEquals(p1_node.filterInstance(conceptInstance), false);
			
			// Test that instance processing and listener firing is working.
			p1_node.processInstance(conceptInstance);
			assertEquals(listener.getLastInstance(), null);			
			
			conceptInstance.setValue(112);
			assertEquals(p1_node.filterInstance(conceptInstance), true);
			
			p1_node.processInstance(conceptInstance);
			NucleusInstance result = (NucleusInstance)listener.getLastInstance();
			if(result == null ){
				fail("The instance is not fired by p1 abstract!");
			} else {
				assertEquals(conceptInstance.getValue(), result.getValue());
			}
			// Testing instance firing through the GA.
			IdGroup idg1 = new IdGroup(10);
			NucleusInstance conceptInstance2 = new NucleusInstance(113, p1.getType(), idg1, p1_node);
			
			m_ga.processInstance(conceptInstance2);
			
			result = (NucleusInstance)listener.getLastInstance();
			assertEquals(conceptInstance2.getValue(), result.getValue());
			
		} catch (GAException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (PPITException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 

		concString = new String(
				"{ \"nucleusConceptValueAttrs\" : [ { \"oper\" : \"<\" , \"name\" : \"a\" , \"value\" : \"115\"}] , " +
				"\"is_key\" : \"false\" , \"name\" : \"p2\" , \"parent\" : \"p1\" }"
				);
		
		PrimitiveConcept p2 = null;
		GANucleus p2_node = null;

		PrimitiveConcept p21 = null;
		GANucleus p21_node = null;

		try {			
			// create the second concept, its parent is first one
			JSONObject conceptJSON = new JSONObject(concString);
			p2 = manager.createPrimitiveConcept(conceptJSON);
			p2.evaluateDependencies();
			
			// Here we know that p1 is already added, so we safely add p2.
			p2_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(p2);
			
			// Registering listener to the next node.
			listener.registerTo(p2_node);
			
			// create a duplicate of p2
			String duplicateJson = p2.getDuplicate("p21");
			System.out.println(duplicateJson);
			
			conceptJSON = new JSONObject(duplicateJson);
			p21 = manager.createPrimitiveConcept(conceptJSON);
			
			p21_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(p21);
			listener.registerTo(p21_node);
		} catch (PPITException e) {
			e.printStackTrace();
			System.out.println("TEST FAILED");
			fail("Could Not Create Primitive Concept");
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("Could not create json from the given string");
			return;
		}
		
		// make sure it is added in both primitive types and primitive concept list.
		assertEquals(library.getListOfTypes().contains(p2.getType()), true);
		assertEquals(library.getListOfPrimitives().contains(p2), true);
		
		// make sure concept has no dependencies
		try {
			assertEquals(p2.getExternalDependencies().isEmpty(), true);
		} catch (IncorrectDependency e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		}
		
		try {
			IdGroup idg = new IdGroup(1);
			NucleusInstance conceptInstance = new NucleusInstance(0, p2.getType(), idg, p2_node);
			
			// Make sure filtering value works as expected (per a single node)
			conceptInstance.setValue(116);
			assertEquals(p2_node.filterInstance(conceptInstance), false);
			
			conceptInstance.setValue(111);
			p2_node.processInstance(conceptInstance);
			NucleusInstance result = (NucleusInstance)listener.getLastInstance();
			assertEquals(conceptInstance.getValue(), result.getValue());
			
			// Check filtering through the Graph of Abstracts.
			IdGroup idg1 = new IdGroup(11);
			NucleusInstance conceptInstance2 = new NucleusInstance(100, p2.getType(), idg1, p2_node);
			m_ga.processInstance(conceptInstance2);
			
			// The listener shall contain the last fired instance.
			result = (NucleusInstance)listener.getLastInstance();
			assertEquals(111, result.getValue());
			
			
			// The instance will be filtered out by the second condition.
			IdGroup idg2 = new IdGroup(21);
			NucleusInstance conceptInstance3 = new NucleusInstance(116, p2.getType(), idg2, p2_node);
			m_ga.processInstance(conceptInstance3);
			
			// The listener shall contain the last fired instance.
			result = (NucleusInstance)listener.getLastInstance();
			assertEquals(111, result.getValue());
			
			// The instance will be fired.
			IdGroup idg3 = new IdGroup(31);
			NucleusInstance conceptInstance4 = new NucleusInstance(114, p2.getType(), idg3, p2_node);
			m_ga.processInstance(conceptInstance4);
			
			// The listener shall contain the last fired instance.
			result = (NucleusInstance)listener.getLastInstance();
			assertEquals(conceptInstance4.getValue(), result.getValue());
			
		} catch (GAException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (PPITException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
	
			/*
		
		concString = new String(
				"{ \"nucleusConceptValueAttrs\" : [ { \"oper\" : \"<\" , \"name\" : \"a\" , \"value\" : \"200 - a.p2\"}] , " +
				"\"is_key\" : \"false\" , \"name\" : \"p3\" , \"parent\" : \"\" }"
				);
		PrimitiveConcept p3 = null;
		try {
			// create the third concept, its parent is first one
			JSONObject conceptJSON = new JSONObject(concString);
			p3 = manager.createPrimitiveConcept(conceptJSON);
		} catch (PPITException e) {
			e.printStackTrace();
			System.out.println("TEST FAILED");
			fail("Could Not Create Primitive Concept");
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("Could not create json from the given string");
			return;
		}
		
		// make sure it is added in both primitive types and primitive concept list.
		assertEquals(library.getListOfTypes().contains(p3.getType()), true);
		assertEquals(library.getListOfPrimitives().contains(p3), true);
		
		// make sure p3 is dependent from p2 concept
		try {
			assertEquals(p3.getDependencies().isEmpty(), false);
			assertEquals(p3.getDependencies().get(0), "a.p2");
		
			// resolve the dependency
			assertEquals(p3.setDependency("a.p2", p2), true);
			assertEquals(p3.getDependencies().isEmpty(), false);

			// make sure set value works as expected
			assertEquals(p3.setValue(100), false);
			assertEquals(p3.setValue(111), false);
			
			assertEquals(p3.setValue(80), true);
		} catch (UnsolvedDependency e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IncorrectDependency e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		}
		*/ 
	}
}
