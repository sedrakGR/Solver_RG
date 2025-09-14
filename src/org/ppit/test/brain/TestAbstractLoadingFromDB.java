package org.ppit.test.brain;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ppit.core.brain.*;
import org.ppit.core.brain.gaNode.abstractNode.GAAbstract;
import org.ppit.core.brain.gaNode.ar1.GAAR1;
import org.ppit.core.brain.gaNode.nucleus.GANucleus;
import org.ppit.core.brain.gaNode.set.GASet;
import org.ppit.core.concept.*;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.set.SetConcept;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.*;

public class TestAbstractLoadingFromDB {
	TestHelper m_testHelper = new TestHelper();
	GA m_ga = new GA();
	GACreator m_gaCreator = new GACreator(m_ga);

	@Test
	public void testAbstractLoading() {
		m_testHelper.resetAllResources();
		
		// Registering the WM Listener.
		
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
		String figureColorString = new String(
				"{ \"name\" : \"FigureColor\" , \"is_key\" : \"1\" , \"parent\" : \"\" , \"nucleusConceptValueAttrs\" : " +
				"[ { \"name\" : \"v\" , \"oper\" : \"IN\" , \"value\" : { \"minValue\" : \"0\" , \"maxValue\" : \"2\"}}] , \"rules\" : [ ]}"
				);
		
		
		String figureString = new String (
				"{\"cr1\":\"1\",\"name\":\"Figure\",\"parent\":\"Figure\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":" +
				"[{\"negated\":\"0\",\"name\":\"ft\"," +
				"\"parent\":\"FigureType\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"cordX\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"FigureColor\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":" +
				"[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}," +
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
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"p1\",\"parent\":\"Pawn\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Pawn.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
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
			"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"tar\",\"parent\":\"Figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
			"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"Figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
			"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}," +
			"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"Figure.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
			"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
			"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"Figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
			"{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
			"{\"negated\":\"0\",\"name\":\"fc\",\"parent\":\"Figure.fc\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[" +
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
		
		
		
		
		PrimitiveConcept cordX = null;
		PrimitiveConcept cordY = null;
		PrimitiveConcept figureType = null;
		PrimitiveConcept figureColor = null;
		
		
		CompositeConcept figure = null;
		CompositeConcept pawn = null;
		
		CompositeConcept twoPawns = null;
		CompositeConcept nbPawn = null;
		CompositeConcept nb3Pawn = null;
		
		SetConcept line = null;
		CompositeConcept lineAbove = null;
		CompositeConcept lineBetween = null;
		
		CompositeConcept king = null;
		
		CompositeConcept fuc = null; // FieldUnderCheck
		CompositeConcept fuc_pn = null; // FieldUnderCheckOfPawn
		CompositeConcept fuc_pn1 = null; // FieldUnderCheckOfPawn1
		CompositeConcept fuc_pn2 = null; // FieldUnderCheckOfPawn2
		CompositeConcept check = null;
		
		GANucleus x_node = null;
		GANucleus y_node = null;
		GANucleus ft_node = null;
		GANucleus fc_node = null;
		
		GAAR1 figure_node = null;
		GAAR1 pawn_node = null;
		
		GAAbstract twoPawns_node = null;
		GAAbstract nbPawn_node = null;
		GAAbstract nb3Pawn_node = null;
		
		GASet line_node = null;
		GAAbstract lineAbove_node = null;
		GAAbstract lineBetween_node = null;
		
		GAAR1 king_node = null;
		
		GAAbstract fuc_node = null;
		GAAbstract fucpn_node = null;
		GAAbstract fucpn1_node = null;
		GAAbstract fucpn2_node = null;
		GAAbstract check_node = null;
		
		try {
			JSONObject tmpJSON = new JSONObject(cordXString);
			cordX = manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(cordYString);
			cordY = manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(figureTypeString);
			figureType = manager.createPrimitiveConcept(tmpJSON);
			
			tmpJSON = new JSONObject(figureColorString);
			figureColor = manager.createPrimitiveConcept(tmpJSON);
			
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
			
			tmpJSON = new JSONObject(lineString);
			line = manager.createSetConcept(tmpJSON);
			
			tmpJSON = new JSONObject(lineAbove_String);
			lineAbove = manager.createCompositeConcept(tmpJSON);

			tmpJSON = new JSONObject(lineBetween_String);
			lineBetween = manager.createCompositeConcept(tmpJSON);
			
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

			
			x_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(cordX);
			y_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(cordY);
			ft_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(figureType);
			fc_node = (GANucleus)m_gaCreator.insertPrimaryAbstract(figureColor);
			
			figure_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(figure);
			pawn_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(pawn);
			
			twoPawns_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(twoPawns);
			nbPawn_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(nbPawn);
			nb3Pawn_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(nb3Pawn);
			
			line_node = (GASet)m_gaCreator.insertPrimaryAbstract(line);
			lineAbove_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(lineAbove);
			lineBetween_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(lineBetween);
			
			king_node = (GAAR1)m_gaCreator.insertPrimaryAbstract(king);
			
			fuc_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(fuc);
			fucpn_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(fuc_pn);
			fucpn1_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(fuc_pn1);
			fucpn2_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(fuc_pn2);
			check_node = (GAAbstract)m_gaCreator.insertPrimaryAbstract(check);
			
			
			library.reset();
			manager.initialLoadAll();
			assertTrue( library.getConcept("cordX").getGANode() != null);
			assertTrue( library.getConcept("cordY").getGANode() != null);
			assertTrue( library.getConcept("FigureType").getGANode() != null);
			assertTrue( library.getConcept("FigureColor").getGANode() != null);
			
			assertTrue( library.getConcept("Figure").getGANode() != null);
			assertTrue( library.getConcept("Pawn").getGANode() != null);
			assertTrue( library.getConcept("King").getGANode() != null);
			assertTrue( library.getConcept("TwoPawns").getGANode() != null);
			assertTrue( library.getConcept("NBPawn").getGANode() != null);
			assertTrue( library.getConcept("ThreePawn").getGANode() != null);
			assertTrue( library.getConcept("Line").getGANode() != null);
			assertTrue( library.getConcept("LineAbove").getGANode() != null);
			assertTrue( library.getConcept("LineBetween").getGANode() != null);
			assertTrue( library.getConcept("FieldUnderCheck").getGANode() != null);
			assertTrue( library.getConcept("FieldUnderCheckOfPawn").getGANode() != null);
			assertTrue( library.getConcept("FieldUnderCheckOfPawn1").getGANode() != null);
			assertTrue( library.getConcept("FieldUnderCheckOfPawn2").getGANode() != null);
			assertTrue( library.getConcept("Check").getGANode() != null);
			
			
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
	}
}