package org.ppit.test.brain;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ppit.util.exception.*;
import org.ppit.core.brain.gaNode.listener.GAListenerCollecter;
import org.ppit.core.brain.gaNode.nucleus.GANucleus;
import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.*;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.set.SetConcept;
import org.ppit.test.TestHelper;


public class TestSetActivation {
	
	TestHelper m_testHelper = new TestHelper();
//	GA m_ga = new GA();
//	GACreator m_gaCreator = new GACreator(m_ga);

	@Test
	public void testAbstractActivation() {
		m_testHelper.resetAllResources();
		
		ConceptManager manager = ConceptManager.getInstance();
		ConceptLibrary library = manager.getLibraryForTest();
		CognitionManager m_cognition = CognitionManager.getInstance();

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
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}" +
				"]}"
				);

		String lineString = new String (
				"{\"name\":\"Line\",\"parent\":\"\",\"attribute\":" +
				"{\"cr1\":\"0\",\"name\":\"element\",\"parent\":\"\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"fig\",\"parent\":\"Figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}]}]},\"setManAttr\":" +
				"[{\"name\":\"count\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"5\"}}],\"setAddAttr\":[]}");
		
		String lineAbove_String = new String (
				"{\"cr1\":\"0\",\"name\":\"LineAbove\",\"parent\":\"LineAbove\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ln\",\"parent\":\"Line\",\"attribute\":" +
				"{\"cr1\":\"0\",\"name\":\"element\",\"parent\":\"Line.element\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"fig\",\"parent\":\"Line.element.fig\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Line.element.fig.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Line.element.fig.y\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"pos.y+1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Line.element.fig.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}]}," +
				"\"setManAttr\":[{\"name\":\"count\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"5\"}}]," +
				"\"setAddAttr\":[]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"pos\",\"parent\":\"Figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}]}"
			); 

		String lineBetween_String = new String (
				"{\"cr1\":\"0\",\"name\":\"LineBetween\",\"parent\":\"LineBetween\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ln\",\"parent\":\"Line\",\"attribute\":" +
				"{\"cr1\":\"0\",\"name\":\"element\",\"parent\":\"Line.element\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"fig\",\"parent\":\"Line.element.fig\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Line.element.fig.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Line.element.fig.y\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"e1.y\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Line.element.fig.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"e1.x+1\",\"maxValue\":\"e2.x-1\"}}]}]}]}," +
				"\"setManAttr\":[{\"name\":\"count\",\"oper\":\"IN\",\"value\":{\"minValue\":\"e2.x-e1.x-1\",\"maxValue\":\"8\"}}]," +
				"\"setAddAttr\":[]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"e1\",\"parent\":\"Figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"0\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"e2\",\"parent\":\"Figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"0\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\">\",\"value\":\"e1.x\"}]}]}" +
				"]}"
			); 

		PrimitiveConcept cordX = null;
		PrimitiveConcept cordY = null;
		PrimitiveConcept figureType = null;
		
		SetConcept line = null;
		
		GANucleus x_node = null;
		GANucleus y_node = null;
		GANucleus ft_node = null;		
		
		GAListenerCollecter figure_listener;
		GAListenerCollecter line_listener;
		GAListenerCollecter lineAbove_listener;
		GAListenerCollecter lineBetween_listener;
		
		try {
			JSONObject tmpJSON = new JSONObject(cordXString);
			cordX = manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(cordYString);
			cordY = manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(figureTypeString);
			figureType = manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(figureString);
			manager.createCompositeConcept(tmpJSON);
			
			tmpJSON = new JSONObject(lineString);
			line = manager.createSetConcept(tmpJSON);
			
			tmpJSON = new JSONObject(lineAbove_String);
			CompositeConcept lineAbove = manager.createCompositeConcept(tmpJSON);

			tmpJSON = new JSONObject(lineBetween_String);
			manager.createCompositeConcept(tmpJSON);
			
			String dupLineAbove_String = lineAbove.getDuplicate("LineAbove_1");
			System.out.println(dupLineAbove_String);
			
			tmpJSON = new JSONObject(dupLineAbove_String);
			manager.createCompositeConcept(tmpJSON);

			figure_listener = m_cognition.getWM().getActivatedInstances("Figure");
			line_listener = m_cognition.getWM().getActivatedInstances("Line");
			lineAbove_listener = m_cognition.getWM().getActivatedInstances("LineAbove_1");
			lineBetween_listener = m_cognition.getWM().getActivatedInstances("LineBetween");
			
		} catch (PPITException e) {
			e.printStackTrace();
			System.out.println("TEST FAILED");
			fail("Could Not Create Primitive Concept");
			return;
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("Could not create json from the given string");
			return;
		}
		
		// Make sure that added concepts exist in library.
		assertEquals(library.getListOfSets().contains(line), true);
		
		try {
			IdGroup idg_13 = new IdGroup(13);
			
			IdGroup idg_21 = new IdGroup(21);
			IdGroup idg_22 = new IdGroup(22);
			IdGroup idg_23 = new IdGroup(23);
			IdGroup idg_24 = new IdGroup(24);
			IdGroup idg_25 = new IdGroup(25);
			IdGroup idg_26 = new IdGroup(26);

			NucleusInstance x_instance13 = new NucleusInstance(3, cordX.getType(), idg_13, x_node);
			NucleusInstance y_instance13 = new NucleusInstance(1, cordY.getType(), idg_13, y_node);
			NucleusInstance ft_instance13 = new NucleusInstance(0, figureType.getType(), idg_13, ft_node);

			NucleusInstance x_instance21 = new NucleusInstance(1, cordX.getType(), idg_21, x_node);
			NucleusInstance y_instance21 = new NucleusInstance(2, cordY.getType(), idg_21, y_node);
			NucleusInstance ft_instance21 = new NucleusInstance(0, figureType.getType(), idg_21, ft_node);
			
			NucleusInstance x_instance22 = new NucleusInstance(2, cordX.getType(), idg_22, x_node);
			NucleusInstance y_instance22 = new NucleusInstance(2, cordY.getType(), idg_22, y_node);
			NucleusInstance ft_instance22 = new NucleusInstance(1, figureType.getType(), idg_22, ft_node);
			
			NucleusInstance x_instance23 = new NucleusInstance(3, cordX.getType(), idg_23, x_node);
			NucleusInstance y_instance23 = new NucleusInstance(2, cordY.getType(), idg_23, y_node);
			NucleusInstance ft_instance23 = new NucleusInstance(1, figureType.getType(), idg_23, ft_node);
			
			NucleusInstance x_instance24 = new NucleusInstance(4, cordX.getType(), idg_24, x_node);
			NucleusInstance y_instance24 = new NucleusInstance(2, cordY.getType(), idg_24, y_node);
			NucleusInstance ft_instance24 = new NucleusInstance(1, figureType.getType(), idg_24, ft_node);			

			NucleusInstance x_instance25 = new NucleusInstance(5, cordX.getType(), idg_25, x_node);
			NucleusInstance y_instance25 = new NucleusInstance(2, cordY.getType(), idg_25, y_node);
			NucleusInstance ft_instance25 = new NucleusInstance(0, figureType.getType(), idg_25, ft_node);			

			NucleusInstance x_instance26 = new NucleusInstance(6, cordX.getType(), idg_26, x_node);
			NucleusInstance y_instance26 = new NucleusInstance(2, cordY.getType(), idg_26, y_node);
			NucleusInstance ft_instance26 = new NucleusInstance(1, figureType.getType(), idg_26, ft_node);			

			// Fire x coordinates.
			m_cognition.processNInstance(x_instance21);
			m_cognition.processNInstance(x_instance22);			
			
			// Activate FigureType instance.
			m_cognition.processNInstance(ft_instance21);
			
			// Start activating y instances.
			m_cognition.processNInstance(y_instance21);
			
			assertEquals(1, figure_listener.getActiveInstanceCount());
			assertEquals(0, line_listener.getActiveInstanceCount());
			
			m_cognition.processNInstance(y_instance22);
			m_cognition.processNInstance(ft_instance22);
			
			assertEquals(2, figure_listener.getActiveInstanceCount());
			assertEquals(1, line_listener.getActiveInstanceCount());
			
			// Fire the first Pos (in AboveLine).
			m_cognition.processNInstance(x_instance13);
			m_cognition.processNInstance(y_instance13);
			m_cognition.processNInstance(ft_instance13);
			
			assertEquals(3, figure_listener.getActiveInstanceCount());
			assertEquals(4, line_listener.getActiveInstanceCount());
			assertEquals(0, lineAbove_listener.getActiveInstanceCount());
			
			// Fire the second Pawn.
			m_cognition.processNInstance(y_instance23);
			m_cognition.processNInstance(ft_instance23);
			m_cognition.processNInstance(x_instance23);

			assertEquals(4, figure_listener.getActiveInstanceCount());
			assertEquals(11, line_listener.getActiveInstanceCount());
			assertEquals(1, lineAbove_listener.getActiveInstanceCount());
			
			// Fire the second Pawn.
			m_cognition.processNInstance(y_instance26);
			m_cognition.processNInstance(ft_instance26);
			m_cognition.processNInstance(x_instance26);

			assertEquals(5, figure_listener.getActiveInstanceCount());
			assertEquals(26, line_listener.getActiveInstanceCount());
			assertEquals(4, lineAbove_listener.getActiveInstanceCount());
			assertEquals(0, lineBetween_listener.getActiveInstanceCount());

			// Fire the second Empty field.
			m_cognition.processNInstance(y_instance25);
			m_cognition.processNInstance(ft_instance25);
			m_cognition.processNInstance(x_instance25);
			
			assertEquals(6, figure_listener.getActiveInstanceCount());
			assertEquals(0, lineBetween_listener.getActiveInstanceCount());
			
			m_cognition.processNInstance(y_instance24);
			m_cognition.processNInstance(ft_instance24);
			m_cognition.processNInstance(x_instance24);
			
			assertEquals(7, figure_listener.getActiveInstanceCount());
			assertEquals(1, lineBetween_listener.getActiveInstanceCount());
			
		} catch (GAException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (PPITException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
	}
}


