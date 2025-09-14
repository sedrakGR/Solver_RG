package org.ppit.test.brain;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ppit.core.brain.GA;
import org.ppit.core.brain.GACreator;
import org.ppit.core.brain.gaNode.ar1.GAAR1;
import org.ppit.core.brain.gaNode.listener.GAListenerCollecter;
import org.ppit.core.brain.gaNode.nucleus.GANucleus;
import org.ppit.core.brain.instance.ar1.AR1Instance;
import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.ConceptLibrary;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.GAException;
import org.ppit.util.exception.PPITException;

public class TestAR1Activation {
	
	TestHelper m_testHelper = new TestHelper();
	GA m_ga = new GA();
	GACreator m_gaCreator = new GACreator(m_ga);

	@Test
	public void testAR1Activation() {
		m_testHelper.resetAllResources();
		
		ConceptManager manager = ConceptManager.getInstance();
		ConceptLibrary library = manager.getLibraryForTest();

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
				"{\"cr1\":\"1\",\"name\":\"Figure\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\"," +
				"\"parent\":\"FigureType\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"cordX\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"cordY\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}" +
				"]}"
				);
		String pawnString = new String (
				"{\"cr1\":\"1\",\"name\":\"Pawn\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}" +
				"]}"
				);
		String firtsLineString = new String (
				"{\"cr1\":\"1\",\"name\":\"FirtLine\",\"parent\":\"\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"cordX\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}" +
				"]}"
				);
		
		PrimitiveConcept cordX = null;
		PrimitiveConcept cordY = null;
		PrimitiveConcept figureType = null;
		
		CompositeConcept firstLine = null;
		CompositeConcept figure = null;
		CompositeConcept pawn = null;
		
		GANucleus x_node = null;
		GANucleus y_node = null;
		GANucleus ft_node = null;
		
		GAAR1 firstLine_node = null;
		GAAR1 figure_node = null;
		GAAR1 pawn_node = null;
		
		GAListenerCollecter x_listener = new GAListenerCollecter();
		GAListenerCollecter y_listener = new GAListenerCollecter();
		GAListenerCollecter ft_listener = new GAListenerCollecter();
		
		GAListenerCollecter firstLine_listener = new GAListenerCollecter();
		GAListenerCollecter figure_listener = new GAListenerCollecter();
		GAListenerCollecter pawn_listener = new GAListenerCollecter();
		
		try {
			JSONObject tmpJSON = new JSONObject(cordXString);
			cordX = manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(cordYString);
			cordY = manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(figureTypeString);
			figureType = manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(figureString);
			figure = manager.createCompositeConcept(tmpJSON);
			
			tmpJSON = new JSONObject(pawnString);
			pawn = manager.createCompositeConcept(tmpJSON);
			
			tmpJSON = new JSONObject(firtsLineString);
			firstLine = manager.createCompositeConcept(tmpJSON);
			
			x_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(cordX);
			y_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(cordY);
			ft_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(figureType);
			
			figure_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(figure);
			pawn_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(pawn);
			firstLine_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(firstLine);
			
			x_listener.registerTo(x_node);
			y_listener.registerTo(y_node);
			ft_listener.registerTo(ft_node);
			firstLine_listener.registerTo(firstLine_node);
			figure_listener.registerTo(figure_node);
			pawn_listener.registerTo(pawn_node);
		} catch (PPITException e) {
			e.printStackTrace();
			System.out.println("TEST FAILED");
			fail("Could Not Create Primitive Concept");
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("Could not create json from the given string");
			return;
		}
		
		// Make sure that added concepts exist in library.
		assertEquals(library.getListOfTypes().contains(cordX.getType()), true);
		assertEquals(library.getListOfPrimitives().contains(cordX), true);
		
		assertEquals(library.getListOfComposites().contains(pawn), true);
		assertEquals(library.getListOfComposites().contains(firstLine), true);

		try {
			IdGroup idg_1 = new IdGroup(1);
			IdGroup idg_2 = new IdGroup(2);
			NucleusInstance x_instance1 = new NucleusInstance(1, cordX.getType(), idg_1, x_node);

			// Test that instance processing and listener firing is working.
			m_ga.processInstance(x_instance1);
			
			// Check that x_cord, and firstLine are activated, while the others are not.
			NucleusInstance x_result = (NucleusInstance)x_listener.getLastInstance();
			if(x_result == null ){
				fail("The x_cord abstract is not fired by x_instance1 processing!");
			} else {
				assertEquals(x_instance1.getValue(), x_result.getValue());
				assertSame(x_instance1, x_result);
			}
			
			AR1Instance fl_result = (AR1Instance)firstLine_listener.getLastInstance();
			if(fl_result == null ){
				fail("The first_line abstract is not fired by x_instance1 processing!");
			} else {
				assertEquals(idg_1.getIdGroup(), fl_result.getGroupId());
				NucleusInstance x_inst = fl_result.getElement("x");
				
				assertEquals(x_instance1.getValue(), x_inst.getValue());
				// The Nucleus Instance is not copied, 
				// hence, the returned and the actual instance shall be the same.
				assertSame(x_instance1, x_inst);
			}
			
			assertEquals(null, y_listener.getLastInstance());
			assertEquals(null, ft_listener.getLastInstance());
			assertEquals(null, pawn_listener.getLastInstance());
			
			// Process the x coordinate from the second Id Group.
			NucleusInstance x_instance2 = new NucleusInstance(2, cordX.getType(), idg_2, x_node);
			
			m_ga.processInstance(x_instance2);

			// Check that new x instance is fired.
			x_result = (NucleusInstance)x_listener.getLastInstance();
			assertEquals(x_instance2.getValue(), x_result.getValue());
			assertSame(x_result, x_instance2);

			// Ensure that FirstLine node is not activated by this instance.
			fl_result = (AR1Instance)firstLine_listener.getLastInstance();
			assertEquals(idg_1.getIdGroup(), fl_result.getGroupId());
			assertSame(x_instance1, fl_result.getElement("x"));

			// Activate FigureType instance.
			NucleusInstance ft_instance1 = new NucleusInstance(3, figureType.getType(), idg_1, ft_node);
			m_ga.processInstance(ft_instance1);
			
			// Start activating y instances.
			NucleusInstance y_instance1 = new NucleusInstance(4, cordY.getType(), idg_1, y_node);
			m_ga.processInstance(y_instance1);
			
			// Check that Figure is activated.
			AR1Instance figure_result = (AR1Instance)figure_listener.getLastInstance();
			if(figure_result == null ){
				fail("The Figure abstract is not fired by x,y,ft processing!");
			} else {
				assertEquals(idg_1.getIdGroup(), figure_result.getGroupId());
				NucleusInstance x_inst = figure_result.getElement("x");
				assertSame(x_instance1, x_inst);
				NucleusInstance y_inst = figure_result.getElement("y");
				assertSame(y_instance1, y_inst);
				NucleusInstance ft_inst = figure_result.getElement("ft");
				assertSame(ft_instance1, ft_inst);
			}
			assertEquals(1, figure_listener.getActiveInstanceCount());
			assertEquals(null, pawn_listener.getLastInstance());
			
			NucleusInstance y_instance2 = new NucleusInstance(3, cordY.getType(), idg_2, y_node);
			m_ga.processInstance(y_instance2);
			
			NucleusInstance ft_instance2 = new NucleusInstance(1, figureType.getType(), idg_2, ft_node);
			m_ga.processInstance(ft_instance2);
			
			// Check that Pawn is activated and there are two active instances of Figure.
			assertEquals(2, figure_listener.getActiveInstanceCount());
			assertEquals(2, ft_listener.getActiveInstanceCount());
			
			AR1Instance pawn_result = (AR1Instance)pawn_listener.getLastInstance();
			if(pawn_result == null ){
				fail("The Pawn abstract is not fired by x,y,ft processing!");
			} else {
				assertEquals(idg_2.getIdGroup(), pawn_result.getGroupId());
				
				NucleusInstance x_inst = pawn_result.getElement("x");
				assertSame(x_instance2, x_inst);

				NucleusInstance ft_inst = pawn_result.getElement("ft");
				assertSame(ft_instance2, ft_inst);
				
				NucleusInstance y_inst = pawn_result.getElement("y");
				assertEquals(y_instance2.getValue(), y_inst.getValue());
				assertSame(y_instance2, y_inst);
			}
		} catch (GAException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (PPITException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
	}
}
