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
import org.ppit.core.brain.gaNode.nucleus.GANucleus;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.brain.instance.ar1.AR1Instance;
import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.ConceptLibrary;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.plans.Goal;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.GAException;
import org.ppit.util.exception.PPITException;

public class TestVirtualActivation {
	
	TestHelper m_testHelper = new TestHelper();
	CognitionManager m_cognition = CognitionManager.getInstance();
	GA m_ga = null;
	GACreator m_gaCreator = null;

	@Test
	public void testAbstractActivation() {
		boolean enable_asserts = true;
		test(enable_asserts);
	}
	
	public void test(boolean enable_asserts) {
		//m_testHelper.resetAllResources();
		m_ga = m_cognition.getGA();
		m_gaCreator = m_cognition.getGACreator();
	
		
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
				"{ \"name\" : \"FigureType\" , \"is_key\" : \"0\" , \"parent\" : \"\" , \"nucleusConceptValueAttrs\" : " +
				"[ { \"name\" : \"v\" , \"oper\" : \"IN\" , \"value\" : { \"minValue\" : \"0\" , \"maxValue\" : \"6\"}}] , \"rules\" : [ ]}"
				);
		String figureColorString = new String(
				"{ \"name\" : \"FigureColor\" , \"is_key\" : \"1\" , \"parent\" : \"\" , \"nucleusConceptValueAttrs\" : " +
				"[ { \"name\" : \"v\" , \"oper\" : \"IN\" , \"value\" : { \"minValue\" : \"0\" , \"maxValue\" : \"2\"}}] , \"rules\" : [ ]}"
				);
		String fieldString = new String (
				"{\"cr1\":\"1\",\"name\":\"Field\",\"parent\":\"Field\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FigureType\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"cordX\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"FigureColor\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"cordY\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}" +
				"]}"
				);
		
		String figureString = new String (
				"{\"cr1\":\"1\",\"name\":\"Figure\",\"parent\":\"Field\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Field.ft\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Field.x\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"Field.fc\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Field.y\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}" +
				"]}"
				);
		
		String pawnString = new String (
				"{\"cr1\":\"1\",\"name\":\"Pawn\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"Figure.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}" +
				"]}"
				);

		String knightString = new String (
				"{\"cr1\":\"1\",\"name\":\"Knight\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"Figure.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"3\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}" +
				"]}"
				);
		
		String kingString = new String (
				"{\"cr1\":\"1\",\"name\":\"King\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"Figure.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"6\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}" +
				"]}"
				);
		
		String fieldUnderCheckString = new String (
			"{\"cr1\":\"0\",\"name\":\"FieldUnderCheck\",\"parent\":\"FieldUnderCheck\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
			"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"att\",\"parent\":\"Figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
			"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
			"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
			"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
			"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
			"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
			"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
			"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"Figure.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
			"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}]}," +
			"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"tar\",\"parent\":\"Field\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
			"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Field.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
			"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}," +
			"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Field.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
			"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
			"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Field.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
			"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
			"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"Field.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
			"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}]}]}"
		);

		String fieldUnderCheckOfPawnString = new String (
				"{\"cr1\":\"0\",\"name\":\"FieldUnderCheckOfPawn\",\"parent\":\"FieldUnderCheck\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"att\",\"parent\":\"FieldUnderCheck.att\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheck.att.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheck.att.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheck.att.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"FieldUnderCheck.att.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"tar\",\"parent\":\"FieldUnderCheck.tar\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheck.tar.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheck.tar.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheck.tar.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"FieldUnderCheck.tar.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}]}]}"
			);

		String fieldUnderCheckOfPawn1String = new String (
				"{\"cr1\":\"0\",\"name\":\"FieldUnderCheckOfPawn1\",\"parent\":\"FieldUnderCheckOfPawn\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"att\",\"parent\":\"FieldUnderCheckOfPawn.att\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheckOfPawn.att.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheckOfPawn.att.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheckOfPawn.att.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"FieldUnderCheckOfPawn.att.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"!=\",\"value\":\"*\"}]}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"tar\",\"parent\":\"FieldUnderCheckOfPawn.tar\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheckOfPawn.tar.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheckOfPawn.tar.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"att.y+1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheckOfPawn.tar.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"att.x+1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"FieldUnderCheckOfPawn.tar.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}]}]}"
			);

		String fieldUnderCheckOfPawn2String = new String (
				"{\"cr1\":\"0\",\"name\":\"FieldUnderCheckOfPawn2\",\"parent\":\"FieldUnderCheckOfPawn\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"att\",\"parent\":\"FieldUnderCheckOfPawn.att\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheckOfPawn.att.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheckOfPawn.att.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheckOfPawn.att.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"FieldUnderCheckOfPawn.att.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"tar\",\"parent\":\"FieldUnderCheckOfPawn.tar\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheckOfPawn.tar.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheckOfPawn.tar.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"att.y+1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheckOfPawn.tar.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"att.x-1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"FieldUnderCheckOfPawn.tar.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}]}]}"
			);
		
		String checkString = new String (
				"{\"cr1\":\"0\",\"name\":\"Check\",\"parent\":\"Check\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"fuc\",\"parent\":\"FieldUnderCheck\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"att\",\"parent\":\"FieldUnderCheck.att\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheck.att.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheck.att.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheck.att.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"FieldUnderCheck.att.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"!=\",\"value\":\"k.fc\"}]}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"tar\",\"parent\":\"FieldUnderCheck.tar\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheck.tar.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheck.tar.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"k.y\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheck.tar.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"k.x\"}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"FieldUnderCheck.tar.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}]}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"k\",\"parent\":\"King\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"King.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"6\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"King.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"King.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"King.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
				"{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}]}]}"	
		);
		
		String fieldUnderCheckOfKnightString = "{\"cr1\":\"0\",\"compositeConceptIndexAttrs\":[],\"name\":\"FieldUnderCheckOfKnight\",\"parent\":\"FieldUnderCheck\",\"compConceptAttrs\":[" +
				"{\"compositeConceptIndexAttrs\":[],\"name\":\"att\",\"parent\":\"FieldUnderCheck.att\",\"type\":\"c\",\"negated\":\"\",\"compConceptAttrs\":[" +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"v\",\"value\":\"3\"}],\"name\":\"ft\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheck.att.ft\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"v\",\"value\":\"?\"}],\"name\":\"y\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheck.att.y\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"v\",\"value\":\"?\"}],\"name\":\"x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheck.att.x\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"IN\",\"name\":\"v\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}],\"name\":\"fc\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheck.att.fc\",\"type\":\"p\",\"negated\":\"\"}]}," +
				"{\"compositeConceptIndexAttrs\":[],\"name\":\"tar\",\"parent\":\"FieldUnderCheck.tar\",\"type\":\"c\",\"negated\":\"\",\"compConceptAttrs\":[" +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"v\",\"value\":\"*\"}],\"name\":\"ft\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheck.tar.ft\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"v\",\"value\":\"?\"}],\"name\":\"y\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheck.tar.y\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"v\",\"value\":\"?\"}],\"name\":\"x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheck.tar.x\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"!=\",\"name\":\"v\",\"value\":\"att.fc\"}],\"name\":\"fc\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheck.tar.fc\",\"type\":\"p\",\"negated\":\"\"}]}]}";

		String fieldUnderCheckOfKnight1String = "{\"cr1\":\"0\",\"compositeConceptIndexAttrs\":[],\"name\":\"FieldUnderCheckOfKnight1\",\"parent\":\"FieldUnderCheckOfKnight\",\"compConceptAttrs\":[" +
				"{\"compositeConceptIndexAttrs\":[],\"name\":\"att\",\"parent\":\"FieldUnderCheckOfKnight.att\",\"type\":\"c\",\"negated\":\"\",\"compConceptAttrs\":[" +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"v\",\"value\":\"3\"}],\"name\":\"ft\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheckOfKnight.att.ft\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"v\",\"value\":\"*\"}],\"name\":\"y\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheckOfKnight.att.y\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"IN\",\"name\":\"v\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}],\"name\":\"fc\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheckOfKnight.att.fc\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"v\",\"value\":\"*\"}],\"name\":\"x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheckOfKnight.att.x\",\"type\":\"p\",\"negated\":\"\"}]}," +
				"{\"compositeConceptIndexAttrs\":[],\"name\":\"tar\",\"parent\":\"FieldUnderCheckOfKnight.tar\",\"type\":\"c\",\"negated\":\"\",\"compConceptAttrs\":[" +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"v\",\"value\":\"*\"}],\"name\":\"ft\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheckOfKnight.tar.ft\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"v\",\"value\":\"att.y+2\"}],\"name\":\"y\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheckOfKnight.tar.y\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"!=\",\"name\":\"v\",\"value\":\"att.fc\"}],\"name\":\"fc\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheckOfKnight.tar.fc\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"v\",\"value\":\"att.x+1\"}],\"name\":\"x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"FieldUnderCheckOfKnight.tar.x\",\"type\":\"p\",\"negated\":\"\"}]}]}";
		
		PrimitiveConcept cordX = null;
		PrimitiveConcept cordY = null;
		PrimitiveConcept figureType = null;
		PrimitiveConcept figureColor = null;
		
		CompositeConcept field = null;
		CompositeConcept figure = null;
		CompositeConcept pawn = null;
		CompositeConcept knight = null;
		CompositeConcept king = null;
		
		CompositeConcept fuc = null; // FieldUnderCheck
		CompositeConcept fuc_pn = null; // FieldUnderCheckOfPawn
		CompositeConcept fuc_pn1 = null; // FieldUnderCheckOfPawn1
		CompositeConcept fuc_pn2 = null; // FieldUnderCheckOfPawn2
		CompositeConcept check = null;
		CompositeConcept fuc_kn = null; // FieldUnderCheckOfKnight
		CompositeConcept fuc_kn1 = null; // FieldUnderCheckOfKnight1
		
		GANucleus x_node = null;
		GANucleus y_node = null;
		GANucleus ft_node = null;
		GANucleus fc_node = null;

		GAAR1 field_node = null;
		GAAR1 figure_node = null;
		GAAR1 pawn_node = null;
		GAAR1 knight_node = null;
		GAAR1 king_node = null;
		
		GAAbstract fuc_node = null;
		GAAbstract fucpn_node = null;
		GAAbstract fucpn1_node = null;
		GAAbstract fucpn2_node = null;
		GAAbstract check_node = null;
		GAAbstract fuckn_node = null;
		GAAbstract fuckn1_node = null;
		
		GAListenerCollecter field_listener = new GAListenerCollecter();
		GAListenerCollecter figure_listener = new GAListenerCollecter();
		GAListenerCollecter pawn_listener = new GAListenerCollecter();
		GAListenerCollecter knight_listener = new GAListenerCollecter();
		GAListenerCollecter king_listener = new GAListenerCollecter();
		
		GAListenerCollecter fuc_listener = new GAListenerCollecter();
		GAListenerCollecter fucpn_listener = new GAListenerCollecter();
		GAListenerCollecter fucpn1_listener = new GAListenerCollecter();
		GAListenerCollecter fucpn2_listener = new GAListenerCollecter();
		GAListenerCollecter check_listener = new GAListenerCollecter();
		GAListenerCollecter fuckn_listener = new GAListenerCollecter();
		GAListenerCollecter fuckn1_listener = new GAListenerCollecter();
		
		// Extra
		GAListenerCollecter fucpn1_att_listener = new GAListenerCollecter();
		GAListenerCollecter fucpn1_tar_listener = new GAListenerCollecter();
		
		
		try {
			// Creating nucleus abstracts.
			JSONObject tmpJSON = new JSONObject(cordXString);
			cordX = manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(cordYString);
			cordY = manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(figureTypeString);
			figureType = manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(figureColorString);
			figureColor = manager.createPrimitiveConcept(tmpJSON);
			
			// Creating AR1s.
			tmpJSON = new JSONObject(fieldString);
			field = manager.createCompositeConcept(tmpJSON);
			
			tmpJSON = new JSONObject(figureString);
			figure = manager.createCompositeConcept(tmpJSON);
			
			tmpJSON = new JSONObject(pawnString);
			pawn = manager.createCompositeConcept(tmpJSON);
			
			tmpJSON = new JSONObject(knightString);
			knight = manager.createCompositeConcept(tmpJSON);

			tmpJSON = new JSONObject(kingString);
			king = manager.createCompositeConcept(tmpJSON);

			// Creating Abstracts.
			tmpJSON = new JSONObject(fieldUnderCheckString);
			fuc = manager.createCompositeConcept(tmpJSON);

			tmpJSON = new JSONObject(fieldUnderCheckOfPawnString);
			fuc_pn = manager.createCompositeConcept(tmpJSON);

			tmpJSON = new JSONObject(fieldUnderCheckOfPawn1String);
			fuc_pn1 = manager.createCompositeConcept(tmpJSON);
			
			tmpJSON = new JSONObject(fieldUnderCheckOfPawn2String);
			fuc_pn2 = manager.createCompositeConcept(tmpJSON);
			
			tmpJSON = new JSONObject(checkString);
			check = manager.createCompositeConcept(tmpJSON);
			
			tmpJSON = new JSONObject(fieldUnderCheckOfKnightString);
			fuc_kn = manager.createCompositeConcept(tmpJSON);

			tmpJSON = new JSONObject(fieldUnderCheckOfKnight1String);
			fuc_kn1 = manager.createCompositeConcept(tmpJSON);

			if(enable_asserts) {
				// Make sure that added concepts exist in library.
				assertEquals(library.getListOfTypes().contains(cordX.getType()), true);
				assertEquals(library.getListOfPrimitives().contains(cordX), true);
				
				assertEquals(library.getListOfComposites().contains(pawn), true);
				assertEquals(library.getListOfComposites().contains(knight), true);
				
				// Check the "Virtual" characteristic.
				assertEquals(false, pawn.isAbstract());
				assertEquals(false, pawn.isUsage());
							
				assertEquals(true, fuc.getAttribute("att").isAbstract());
				assertEquals(false, fuc.getAttribute("att").isUsage());
				
				assertEquals(true, fuc.isAbstract());
				assertEquals(false, fuc.isUsage());
				
				assertEquals(true, fuc.getAttribute("tar").isAbstract());
				assertEquals(false, fuc.getAttribute("tar").isUsage());
				
				assertEquals(true, fuc_pn.isAbstract());
				assertEquals(false, fuc_pn.isUsage());
				
				assertEquals(true, fuc_pn.getAttribute("att").isAbstract());
				assertEquals(false, fuc_pn.getAttribute("att").isUsage());
				
				assertEquals(true, fuc_pn.getAttribute("tar").isAbstract());
				assertEquals(false, fuc_pn.getAttribute("tar").isUsage());
				
				assertEquals(false, fuc_pn1.getAttribute("att").isAbstract());
				assertEquals(false, fuc_pn1.getAttribute("att").isUsage());
				
				assertEquals(false, fuc_pn1.getAttribute("tar").isAbstract());
				assertEquals(false, fuc_pn1.getAttribute("tar").isUsage());
				
				assertEquals(false, fuc_pn1.isAbstract());
				assertEquals(false, fuc_pn1.isUsage());
						
				assertEquals(false, check.getAttribute("k").isAbstract());
				assertEquals(false, check.getAttribute("k").isUsage());
				
				assertEquals(true, check.getAttribute("fuc.att").isAbstract());
				assertEquals(false, check.getAttribute("fuc.att").isUsage());
				
				assertEquals(true, fuc_kn.isAbstract());
				assertEquals(false, fuc_kn.isUsage());
				
				assertEquals(true, fuc_kn.getAttribute("att").isAbstract());
				assertEquals(false, fuc_kn.getAttribute("att").isUsage());
				
				assertEquals(true, fuc_kn.getAttribute("tar").isAbstract());
				assertEquals(false, fuc_kn.getAttribute("tar").isUsage());
				
				assertEquals(false, fuc_kn1.getAttribute("att").isAbstract());
				assertEquals(false, fuc_kn1.getAttribute("att").isUsage());
				
				assertEquals(false, fuc_kn1.getAttribute("tar").isAbstract());
				assertEquals(false, fuc_kn1.getAttribute("tar").isUsage());
				
				assertEquals(false, fuc_kn1.isAbstract());
				assertEquals(false, fuc_kn1.isUsage());
				
				
				// Fixed 'to do' the following case is an example of Headache 
				//      which we could getting due to abstract attributes.
				// assertEquals(true, check.getAttribute("fuc.tar").isAbstract());
				// 
				// The abstract attributes are not added to the GA (neither for virtual nor for usage cases).
	
				assertEquals(false, check.getAttribute("fuc.tar").isUsage());
				
				assertEquals(true, check.getAttribute("fuc").isAbstract());
				assertEquals(true, check.getAttribute("fuc").isUsage());
				
				assertEquals(false, check.isAbstract());
				assertEquals(false, check.isUsage());
			}				
			// Inserting into the Graph.
			x_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(cordX);
			y_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(cordY);
			ft_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(figureType);
			fc_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(figureColor);
			
			field_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(field);
			figure_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(figure);
			pawn_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(pawn);
			knight_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(knight);
			king_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(king);
			
			fuc_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(fuc);
			fucpn_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(fuc_pn);
			fucpn1_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(fuc_pn1);
			fucpn2_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(fuc_pn2);
			check_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(check);
			fuckn_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(fuc_kn);
			fuckn1_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(fuc_kn1);
			 
			
			field_listener.registerTo(field_node);
			figure_listener.registerTo(figure_node);
			pawn_listener.registerTo(pawn_node);
			knight_listener.registerTo(knight_node);
			king_listener.registerTo(king_node);
			
			fuc_listener.registerTo(fuc_node);
			fucpn_listener.registerTo(fucpn_node);
			fucpn1_listener.registerTo(fucpn1_node);
			fucpn2_listener.registerTo(fucpn2_node);
			check_listener.registerTo(check_node);
			fuckn_listener.registerTo(fuckn_node);
			fuckn1_listener.registerTo(fuckn1_node);
			
			//Extra
			fucpn1_att_listener.registerTo(fuc_pn1.getAttribute("att").getGANode());
			fucpn1_tar_listener.registerTo(fuc_pn1.getAttribute("tar").getGANode());
			
		} catch (PPITException e) {
			e.printStackTrace();
			System.out.println("TEST FAILED");
			fail("Could Not Create Primitive Concept");
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("Could not create json from the given string");
			return;
		}

		if(!enable_asserts) {
			return;
		}
		try {
			IdGroup idg_21 = new IdGroup(21);
			IdGroup idg_22 = new IdGroup(22);
			IdGroup idg_23 = new IdGroup(23);
			
			IdGroup idg_31 = new IdGroup(31);
			IdGroup idg_32 = new IdGroup(32);
			IdGroup idg_33 = new IdGroup(33);
			IdGroup idg_34 = new IdGroup(34);
			
			IdGroup idg_55 = new IdGroup(55);
			
			NucleusInstance x_instance21 = new NucleusInstance(1, cordX.getType(), idg_21, x_node);
			NucleusInstance y_instance21 = new NucleusInstance(2, cordY.getType(), idg_21, y_node);
			NucleusInstance ft_instance21 = new NucleusInstance(1, figureType.getType(), idg_21, ft_node);
			NucleusInstance fc_instance21 = new NucleusInstance(1, figureColor.getType(), idg_21, fc_node);
			
			NucleusInstance x_instance22 = new NucleusInstance(2, cordX.getType(), idg_22, x_node);
			NucleusInstance y_instance22 = new NucleusInstance(2, cordY.getType(), idg_22, y_node);
			NucleusInstance ft_instance22 = new NucleusInstance(1, figureType.getType(), idg_22, ft_node);
			NucleusInstance fc_instance22 = new NucleusInstance(1, figureColor.getType(), idg_22, fc_node);

			NucleusInstance x_instance23 = new NucleusInstance(3, cordX.getType(), idg_23, x_node);			
			NucleusInstance y_instance23 = new NucleusInstance(2, cordY.getType(), idg_23, y_node);
			NucleusInstance ft_instance23 = new NucleusInstance(1, figureType.getType(), idg_23, ft_node);
			NucleusInstance fc_instance23 = new NucleusInstance(1, figureColor.getType(), idg_23, fc_node);
			
			NucleusInstance x_instance31 = new NucleusInstance(1, cordX.getType(), idg_31, x_node);
			NucleusInstance y_instance31 = new NucleusInstance(3, cordY.getType(), idg_31, y_node);
			NucleusInstance ft_instance31 = new NucleusInstance(0, figureType.getType(), idg_31, ft_node);
			NucleusInstance fc_instance31 = new NucleusInstance(0, figureColor.getType(), idg_31, fc_node);
			
			NucleusInstance x_instance32 = new NucleusInstance(2, cordX.getType(), idg_32, x_node);
			NucleusInstance y_instance32 = new NucleusInstance(3, cordY.getType(), idg_32, y_node);
			NucleusInstance ft_instance32 = new NucleusInstance(6, figureType.getType(), idg_32, ft_node);
			NucleusInstance fc_instance32 = new NucleusInstance(2, figureColor.getType(), idg_32, fc_node);
			
			NucleusInstance x_instance33 = new NucleusInstance(3, cordX.getType(), idg_33, x_node);
			NucleusInstance y_instance33 = new NucleusInstance(3, cordY.getType(), idg_33, y_node);
			NucleusInstance ft_instance33 = new NucleusInstance(0, figureType.getType(), idg_33, ft_node);			
			NucleusInstance fc_instance33 = new NucleusInstance(0, figureColor.getType(), idg_33, fc_node);

			NucleusInstance x_instance34 = new NucleusInstance(4, cordX.getType(), idg_34, x_node);
			NucleusInstance y_instance34 = new NucleusInstance(3, cordY.getType(), idg_34, y_node);
			NucleusInstance ft_instance34 = new NucleusInstance(3, figureType.getType(), idg_34, ft_node);
			NucleusInstance fc_instance34 = new NucleusInstance(1, figureColor.getType(), idg_34, fc_node);
			
			NucleusInstance x_instance55 = new NucleusInstance(5, cordX.getType(), idg_55, x_node);
			NucleusInstance y_instance55 = new NucleusInstance(5, cordY.getType(), idg_55, y_node);
			NucleusInstance ft_instance55 = new NucleusInstance(0, figureType.getType(), idg_55, ft_node);
			NucleusInstance fc_instance55 = new NucleusInstance(0, figureColor.getType(), idg_55, fc_node);
			
			// Activating the elements in the horizontal line "3" 
			// Fire x coordinates.
			m_ga.processInstance(x_instance31);
			m_ga.processInstance(x_instance32);			
			m_ga.processInstance(x_instance33);
			m_ga.processInstance(x_instance34);
			
			// Activate FigureType instances.
			m_ga.processInstance(ft_instance31);
			m_ga.processInstance(ft_instance32);
			m_ga.processInstance(ft_instance33);
			m_ga.processInstance(ft_instance34);
			
			// Start activating y instances.
			m_ga.processInstance(y_instance31);
			m_ga.processInstance(y_instance32);
			m_ga.processInstance(y_instance33);
			m_ga.processInstance(y_instance34);
			
			// Activating Color instances.
			m_ga.processInstance(fc_instance31);
			m_ga.processInstance(fc_instance32);
			m_ga.processInstance(fc_instance33);
			m_ga.processInstance(fc_instance34);
			
			assertEquals(4, field_listener.getActiveInstanceCount());
			assertEquals(2, figure_listener.getActiveInstanceCount());
			assertEquals(1, knight_listener.getActiveInstanceCount());
			assertEquals(0, pawn_listener.getActiveInstanceCount());
			
			//assertEquals(4, fucpn1_tar_listener.getActiveInstanceCount());
			assertEquals(0, fucpn1_att_listener.getActiveInstanceCount());
			assertEquals(0, fucpn1_listener.getActiveInstanceCount());
			assertEquals(0, fucpn2_listener.getActiveInstanceCount());
			assertEquals(0, fucpn_listener.getActiveInstanceCount());
			assertEquals(0, fuc_listener.getActiveInstanceCount());
			
			m_ga.processInstance(y_instance21);
			m_ga.processInstance(ft_instance21);
			m_ga.processInstance(fc_instance21);
			m_ga.processInstance(x_instance21);

			assertEquals(1, pawn_listener.getActiveInstanceCount());
			assertEquals(1, king_listener.getActiveInstanceCount());
			//assertEquals(5, fucpn1_tar_listener.getActiveInstanceCount());
			assertEquals(1, fucpn1_att_listener.getActiveInstanceCount());
			assertEquals(0, fucpn2_listener.getActiveInstanceCount());
			assertEquals(1, fucpn1_listener.getActiveInstanceCount());
			
			assertEquals(1, fucpn_listener.getActiveInstanceCount());
			assertEquals(1, fuc_listener.getActiveInstanceCount());
			assertSame(fucpn1_listener.getLastInstance(), fuc_listener.getLastInstance());
			
			AR1Instance pawnInst = (AR1Instance)pawn_listener.getLastInstance();
			AR1Instance kingInst = (AR1Instance)king_listener.getLastInstance();
			
			assertEquals(21, pawnInst.getGroupId());
			assertEquals(32, kingInst.getGroupId());
			
			AbstractInstance fuc_instance = (AbstractInstance)fuc_listener.getLastInstance();
			AR1Instance att = (AR1Instance)fuc_instance.getInstance("att");
			AR1Instance tar = (AR1Instance)fuc_instance.getInstance("tar");
			
			assertEquals(21, att.getGroupId());
			assertEquals(32, tar.getGroupId());
			
			assertSame(kingInst.getElement("x"), tar.getElement("x"));
			assertSame(kingInst.getElement("ft"), tar.getElement("ft"));

			// assertSame(kingInst, tar); - These are not the same object 
			// because they are activated by two different AR1 nodes: Figure and King.
			
			// Testing the USAGE of fuc abstract in Check abstract.
			assertEquals(1, check_listener.getActiveInstanceCount());
			
			m_ga.processInstance(y_instance22);
			m_ga.processInstance(ft_instance22);
			m_ga.processInstance(fc_instance22);
			m_ga.processInstance(x_instance22);
			
			assertEquals(2, pawn_listener.getActiveInstanceCount());
			assertEquals(1, fucpn2_listener.getActiveInstanceCount());
			assertEquals(2, fucpn1_listener.getActiveInstanceCount());
			assertEquals(3, fucpn_listener.getActiveInstanceCount());
			assertEquals(3, fuc_listener.getActiveInstanceCount());
			
			assertEquals(1, check_listener.getActiveInstanceCount());
			
			m_ga.processInstance(y_instance23);
			m_ga.processInstance(ft_instance23);
			m_ga.processInstance(fc_instance23);
			m_ga.processInstance(x_instance23);

			assertEquals(3, pawn_listener.getActiveInstanceCount());
			assertEquals(2, fucpn2_listener.getActiveInstanceCount());
			assertEquals(3, fucpn1_listener.getActiveInstanceCount());
			assertEquals(5, fucpn_listener.getActiveInstanceCount());
			assertEquals(5, fuc_listener.getActiveInstanceCount());

			assertEquals(2, check_listener.getActiveInstanceCount());
			// Start extra Usage testing.
			
			m_ga.processInstance(y_instance55);
			m_ga.processInstance(ft_instance55);
			m_ga.processInstance(fc_instance55);
			m_ga.processInstance(x_instance55);
			
			assertEquals(1, fuckn1_listener.getActiveInstanceCount());
			assertEquals(1, fuckn_listener.getActiveInstanceCount());
			assertEquals(2, check_listener.getActiveInstanceCount());
			
			
			
			
			//TODO: make a new test for Goals and plans,
			//currently it is just to use here
			
			
			try {
				String goalString = "{ \"name\" : \"hitpawn\", \"primary\" : \"true\", \"preCondition\" : \"FieldUnderCheckOfKnight\","
						+ " \"postCondition\" : \"Check\", \"depth\" : \"1\", \"evaluator\" : [] }";

				JSONObject goalJSON = new JSONObject(goalString);
				Goal g = ConceptManager.getInstance().createGoal(goalJSON);
				if (g == null) {
					System.out.println("goal is not created");
				} else {
					System.out.println("goal name: " + g.getName());
				}
			} catch (JSONException e) {
				e.printStackTrace();
				System.out
						.println("Could not create json from the given string");
				return;
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
