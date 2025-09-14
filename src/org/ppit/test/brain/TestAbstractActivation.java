package org.ppit.test.brain;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ppit.core.brain.GA;
import org.ppit.core.brain.GACreator;
import org.ppit.core.brain.gaNode.abstractNode.GAAbstract;
import org.ppit.core.brain.gaNode.ar1.GAAR1;
import org.ppit.core.brain.gaNode.listener.GAListenerCollecter;
import org.ppit.core.brain.gaNode.listener.WorkingMemoryListener;
import org.ppit.core.brain.gaNode.nucleus.GANucleus;
import org.ppit.core.brain.instance.IdGroupContainer;
import org.ppit.core.brain.instance.InstanceBase;
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

public class TestAbstractActivation {
	
	TestHelper m_testHelper = new TestHelper();
	GA m_ga = new GA();
	GACreator m_gaCreator = new GACreator(m_ga);
	WorkingMemoryListener m_wm = new WorkingMemoryListener();

	@Test
	public void testAbstractActivation() {
		m_testHelper.resetAllResources();
		
		// Registering the WM Listener.
		m_gaCreator.registerWMListener(m_wm);
		
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
		
		String twoPawnsString = new String (
				"{\"cr1\":\"0\",\"name\":\"TwoPawns\",\"parent\":\"TwoPawns\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"p1\",\"parent\":\"Pawn\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Pawn.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Pawn.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Pawn.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"p2\",\"parent\":\"Pawn\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Pawn.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Pawn.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Pawn.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}]}"	
		);
		
		String nbPawnString = new String (
				"{\"cr1\":\"0\",\"name\":\"NBPawn\",\"parent\":\"NBPawn\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"p1\",\"parent\":\"Pawn\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Pawn.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Pawn.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Pawn.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"p2.x+1\"}]}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"p2\",\"parent\":\"Pawn\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Pawn.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Pawn.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Pawn.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}]}"	
		);
		
		String nb3PawnString = new String (
				"{\"cr1\":\"0\",\"name\":\"ThreePawn\",\"parent\":\"ThreePawn\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"nb\",\"parent\":\"NBPawn\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"p2\",\"parent\":\"NBPawn.p2\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"NBPawn.p2.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"NBPawn.p2.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"NBPawn.p2.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"p1\",\"parent\":\"NBPawn.p1\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"NBPawn.p1.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"NBPawn.p1.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"NBPawn.p1.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"p2.x+1\"}]}]}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"p3\",\"parent\":\"Pawn\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Pawn.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Pawn.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"nb.p2.y\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Pawn.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"nb.p1.x+1\"}]}]}]}"
		);
		
		PrimitiveConcept cordX = null;
		PrimitiveConcept cordY = null;
		PrimitiveConcept figureType = null;
		
		CompositeConcept figure = null;
		CompositeConcept pawn = null;
		
		CompositeConcept twoPawns = null;
		CompositeConcept nbPawn = null;
		CompositeConcept nb3Pawn = null;
		
		GANucleus x_node = null;
		GANucleus y_node = null;
		GANucleus ft_node = null;
		
		GAAR1 figure_node = null;
		GAAR1 pawn_node = null;
		
		GAAbstract twoPawns_node = null;
		GAAbstract nbPawn_node = null;
		GAAbstract nb3Pawn_node = null;
		
		GAListenerCollecter x_listener = new GAListenerCollecter();
		GAListenerCollecter y_listener = new GAListenerCollecter();
		GAListenerCollecter ft_listener = new GAListenerCollecter();
		
		GAListenerCollecter figure_listener = new GAListenerCollecter();
		GAListenerCollecter pawn_listener = new GAListenerCollecter();
		
		GAListenerCollecter twoPawns_listener = new GAListenerCollecter();
		GAListenerCollecter nbPawn_listener = new GAListenerCollecter();
		GAListenerCollecter nb3Pawn_listener = new GAListenerCollecter();
		
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
			
			tmpJSON = new JSONObject(twoPawnsString);
			twoPawns = manager.createCompositeConcept(tmpJSON);

			tmpJSON = new JSONObject(nbPawnString);
			nbPawn = manager.createCompositeConcept(tmpJSON);
			
			tmpJSON = new JSONObject(nb3PawnString);
			nb3Pawn = manager.createCompositeConcept(tmpJSON);
			
			x_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(cordX);
			y_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(cordY);
			ft_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(figureType);
			
			figure_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(figure);
			pawn_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(pawn);
			
			twoPawns_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(twoPawns);
			nbPawn_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(nbPawn);
			nb3Pawn_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(nb3Pawn);
			
			x_listener.registerTo(x_node);
			y_listener.registerTo(y_node);
			ft_listener.registerTo(ft_node);
			figure_listener.registerTo(figure_node);
			pawn_listener.registerTo(pawn_node);
			twoPawns_listener.registerTo(twoPawns_node);
			nbPawn_listener.registerTo(nbPawn_node);
			nb3Pawn_listener.registerTo(nb3Pawn_node);
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
		assertEquals(library.getListOfComposites().contains(nbPawn), true);
		
		try {
			IdGroup idg_1 = new IdGroup(1);
			IdGroup idg_2 = new IdGroup(2);
			IdGroup idg_3 = new IdGroup(3);
			IdGroup idg_4 = new IdGroup(4);

			NucleusInstance x_instance1 = new NucleusInstance(1, cordX.getType(), idg_1, x_node);
			NucleusInstance x_instance2 = new NucleusInstance(2, cordX.getType(), idg_2, x_node);
			NucleusInstance x_instance3 = new NucleusInstance(3, cordX.getType(), idg_3, x_node);
			NucleusInstance x_instance4 = new NucleusInstance(4, cordX.getType(), idg_4, x_node);
			
			NucleusInstance y_instance1 = new NucleusInstance(3, cordY.getType(), idg_1, y_node);
			NucleusInstance y_instance2 = new NucleusInstance(3, cordY.getType(), idg_2, y_node);
			NucleusInstance y_instance3 = new NucleusInstance(3, cordY.getType(), idg_3, y_node);
			NucleusInstance y_instance4 = new NucleusInstance(3, cordY.getType(), idg_4, y_node);

			NucleusInstance ft_instance1 = new NucleusInstance(3, figureType.getType(), idg_1, ft_node);
			NucleusInstance ft_instance2 = new NucleusInstance(1, figureType.getType(), idg_2, ft_node);
			NucleusInstance ft_instance3 = new NucleusInstance(1, figureType.getType(), idg_3, ft_node);
			NucleusInstance ft_instance4 = new NucleusInstance(1, figureType.getType(), idg_4, ft_node);
			
			// Fire x coordinates.
			m_ga.processInstance(x_instance1);
			m_ga.processInstance(x_instance2);			
			
			// Activate FigureType instance.
			m_ga.processInstance(ft_instance1);
			
			// Start activating y instances.
			m_ga.processInstance(y_instance1);
			
			assertEquals(1, figure_listener.getActiveInstanceCount());
			assertEquals(null, pawn_listener.getLastInstance());
			assertEquals(null, twoPawns_listener.getLastInstance());
			
			m_ga.processInstance(y_instance2);
			m_ga.processInstance(ft_instance2);
			
			// Check that Pawn is activated and there are two active instances of Figure.
			assertEquals(2, figure_listener.getActiveInstanceCount());
			assertEquals(2, ft_listener.getActiveInstanceCount());
			assertEquals(1, pawn_listener.getActiveInstanceCount());
			assertEquals(0, nbPawn_listener.getActiveInstanceCount());
			assertEquals(0, nb3Pawn_listener.getActiveInstanceCount());
			
			IdGroupContainer idc = pawn_listener.getLastInstance().getIdGroupsInInstance();
			assertTrue(idc.getIdGroups().contains(idg_2));
			assertEquals(1, idc.getIdGroups().size());
			
			
			// TODO In the current implementation there is no checking for non-similarity
			//      hence, the same pawn can fire here both p1 and p2 attributes
			//      and activate this concept
			assertEquals(1, twoPawns_listener.getActiveInstanceCount());
			
			// Fire the second Pawn.
			m_ga.processInstance(y_instance3);
			m_ga.processInstance(ft_instance3);
			m_ga.processInstance(x_instance3);

			assertEquals(3, figure_listener.getActiveInstanceCount());
			assertEquals(2, pawn_listener.getActiveInstanceCount());
			assertEquals(1, nbPawn_listener.getActiveInstanceCount());
			
			// TODO In the current implementation there is no checking for non-similarity
			//      hence, the same pawn can fire here both p1 and p2 attributes
			//      and activate this concept
			assertEquals(4, twoPawns_listener.getActiveInstanceCount());
			assertEquals(0, nb3Pawn_listener.getActiveInstanceCount());
			
			InstanceBase nb_result = nbPawn_listener.getLastInstance();
			AR1Instance p1_result = (AR1Instance)nb_result.getInstance("p1");
			AR1Instance p2_result = (AR1Instance)nb_result.getInstance("p2");
			
			AR1Instance second_pawn = (AR1Instance)pawn_listener.getLastInstance(); 
			
			if(p1_result.getGroupId() == second_pawn.getGroupId()) {
				// In the current implementation
				// P1 and Pawn are not identical (because in P1 dependencies are specified)
				// TODO optimize it correctly to handle this behavior.
				assertNotSame(p1_result, second_pawn);
			} else {
				// On the contrary, the P2 node is the same as Pawn node, hence, the instance shall be the same.
				assertEquals(p2_result.getGroupId(), second_pawn.getGroupId());
				assertSame(p2_result, second_pawn);
			}
			
			// Fire the third Pawn.
			m_ga.processInstance(y_instance4);
			m_ga.processInstance(ft_instance4);
			m_ga.processInstance(x_instance4);

			assertEquals(3, pawn_listener.getActiveInstanceCount());
			assertEquals(2, nbPawn_listener.getActiveInstanceCount());
			assertEquals(1, nb3Pawn_listener.getActiveInstanceCount());
			
			AR1Instance third_pawn = (AR1Instance)pawn_listener.getLastInstance();
			AR1Instance nb_pawn_3rd = (AR1Instance)(nb3Pawn_listener.getLastInstance().getInstance("p3"));
			
			// For the same reason as P1 pawn. P3 is not the same as Pawn GANode.
			assertNotSame(third_pawn, nb_pawn_3rd);
			
			// WM Listener testing.
			assertEquals(3, m_wm.getActivatedInstances("Pawn").getActiveInstanceCount());
			assertEquals(2, m_wm.getActivatedInstances("NBPawn").getActiveInstanceCount());
			assertEquals(1, m_wm.getActivatedInstances("ThreePawn").getActiveInstanceCount());
			
			boolean exceptionTrown = false;
			try {
				m_wm.getActivatedInstances("NBPawn.p1");
			} catch (PPITException e) {
				exceptionTrown = true;
			}
			assertTrue(exceptionTrown);
			
			IdGroupContainer idc_n3 = nb3Pawn_listener.getLastInstance().getIdGroupsInInstance();
			assertTrue(idc_n3.getIdGroups().contains(idg_2));
			assertTrue(idc_n3.getIdGroups().contains(idg_3));
			assertTrue(idc_n3.getIdGroups().contains(idg_4));
			assertEquals(3, idc_n3.getIdGroups().size());
			System.out.println(idc_n3.getOnlyIdsJSON(1));
		} catch (GAException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (PPITException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
	}
}
