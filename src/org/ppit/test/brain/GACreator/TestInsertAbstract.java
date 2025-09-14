package org.ppit.test.brain.GACreator;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ppit.core.brain.GA;
import org.ppit.core.brain.GACreator;
import org.ppit.core.brain.GAEdge;
import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.gaNode.abstractNode.GAAbstract;
import org.ppit.core.brain.gaNode.action.GAAction;
import org.ppit.core.brain.gaNode.ar1.GAAR1;
import org.ppit.core.brain.gaNode.nucleus.GANucleus;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.action.Action;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.PPITException;

/**
 * 
 * @author SedrakG
 * This is to test insertAbstract method of GACreator
 */
public class TestInsertAbstract {
	@Test
	public void testCompositeConcept() {
		//resets all resources
		TestHelper helper = new TestHelper();
		ConceptManager manager = ConceptManager.getInstance();
		//ConceptLibrary library = manager.getLibraryForTest();
		helper.resetAllResources();
		
		
		//NOTE: We define king under check of knight, which has no defence attribute (for complete description of checkMate we need to add defenceKing attribute too.)
		
		//create concepts
		String cordXString = new String(
				"{ \"nucleusConceptValueAttrs\" : [{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}] , " +
				"\"is_key\" : \"false\", \"name\" : \"cordX\" , \"parent\" : \"\"}"
				);
		
		String cordYString = new String(
				"{ \"nucleusConceptValueAttrs\" : [{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}] , " +
				"\"is_key\" : \"false\", \"name\" : \"cordY\" , \"parent\" : \"\"}"
				);
		
		String figureTypeString = new String(
				"{ \"nucleusConceptValueAttrs\" : [{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}] , " +
				"\"is_key\" : \"false\", \"name\" : \"figureType\" , \"parent\" : \"\"}"
				);
		
		String figureColorString = new String(
				"{ \"nucleusConceptValueAttrs\" : [{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}] , " +
				"\"is_key\" : \"true\", \"name\" : \"figureColor\" , \"parent\" : \"\"}"
				);
		
		String figureString = new String(
				"{\"cr1\":\"1\",\"name\":\"figure\",\"parent\":\"figure\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"cordX\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"cordY\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figureColor\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figureType\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}]}"
				);
		
		String pawnString = new String(
				"{\"cr1\":\"1\",\"name\":\"pawn\",\"parent\":\"figure\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"figure.x\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"figure.y\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"2\",\"maxValue\":\"7\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figure.c\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figure.ft\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}]}"
				);
		
		String knightString = new String(
				"{\"cr1\":\"1\",\"name\":\"knight\",\"parent\":\"figure\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"figure.x\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"figure.y\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figure.c\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figure.ft\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}]}"
				);
		
		String moveKnightString = new String(
				"{\"name\":\"moveKnight1\",\"precondition\":{\"cr1\":\"0\",\"name\":\"precond\",\"parent\":\"\"," +
				"\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"cr1\":\"1\", \"negated\":\"0\",\"name\":\"att\",\"parent\":\"knight\"," +
				"\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"knight.ft\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"knight.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}" +
				",{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"knight.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"knight.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}," +
				"{\"cr1\":\"1\", \"negated\":\"0\",\"name\":\"vic\",\"parent\":\"figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figure.ft\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"att.x.v-1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"figure.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"att.y.v-2\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figure.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}]}]}," +
				"\"expressions\":[{\"right\":\"vic.ft.v\",\"oper\":\"=\",\"left\":\"att.ft.v\"}," +
				"{\"right\":\"vic.c.v\",\"oper\":\"=\",\"left\":\"att.c.v\"}," +
				"{\"right\":\"att.ft.v\",\"oper\":\"=\",\"left\":\"0\"}," +
				"{\"right\":\"att.c.v\",\"oper\":\"=\",\"left\":\"0\"}]}"
				);
		
		String filedUnderCheckString = new String(
				"{\"cr1\":\"0\",\"name\":\"FieldUnderCheck\",\"parent\":\"FieldUnderCheck\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\", \"negated\":\"0\",\"name\":\"vic\",\"parent\":\"figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"figure.x\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"figure.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figure.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}]}," +
				"{\"cr1\":\"1\", \"negated\":\"0\",\"name\":\"att\",\"parent\":\"figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figure.ft\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"figure.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figure.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}]}," +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"figureColor\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}]}"
				);
		
		String fieldUnderCheckOfKnightString = new String(
				"{\"cr1\":\"0\",\"name\":\"FieldUnderCheckOfKnight\",\"parent\":\"FieldUnderCheck\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"vic\",\"parent\":\"FieldUnderCheck.vic\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheck.vic.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheck.vic.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheck.vic.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FieldUnderCheck.vic.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}]}," +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"FieldUnderCheck.v_c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"att\",\"parent\":\"FieldUnderCheck.att\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheck.att.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheck.att.x\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FieldUnderCheck.att.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheck.att.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}]}]}"
				);
		
		String fieldUnderCheckOfKnight1String = new String(
				"{\"cr1\":\"0\",\"name\":\"FieldUnderCheckOfKnight1\",\"parent\":\"FieldUnderCheckOfKnight\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"vic\",\"parent\":\"FieldUnderCheckOfKnight.vic\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheckOfKnight.vic.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"att.x.v+1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheckOfKnight.vic.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"att.y.v+2\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheckOfKnight.vic.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"*\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FieldUnderCheckOfKnight.vic.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"!=\",\"value\":\"vic.c\"}]}]}," +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"FieldUnderCheckOfKnight.v_c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"!=\",\"value\":\"vic.c\"}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"att\",\"parent\":\"FieldUnderCheckOfKnight.att\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheckOfKnight.att.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheckOfKnight.att.x\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"7\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FieldUnderCheckOfKnight.att.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheckOfKnight.att.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"6\"}}]}]}]}"
				);
		
		String fldIsBusyOrUndChkString = new String(
				"{\"cr1\":\"0\",\"name\":\"FldIsBusyOrUndChk\",\"parent\":\"FldIsBusyOrUndChk\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"fd\",\"parent\":\"figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"figure.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"figure.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figure.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}]}," +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"figureColor\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}]}"
				);
		
		String fldIsBusyString = new String(
				"{\"cr1\":\"0\",\"name\":\"FieldIsBusy\",\"parent\":\"FldIsBusyOrUndChk\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"FldIsBusyOrUndChk.v_c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"fd\",\"parent\":\"FldIsBusyOrUndChk.fd\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FldIsBusyOrUndChk.fd.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"v_c.v\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FldIsBusyOrUndChk.fd.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FldIsBusyOrUndChk.fd.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FldIsBusyOrUndChk.fd.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}]}"
				);
		
		String fldUndChkString = new String(
				"{\"cr1\":\"0\",\"name\":\"FldUndChk\",\"parent\":\"FldIsBusyOrUndChk\"," +
				"\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"FldIsBusyOrUndChk.v_c\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"fd\",\"parent\":\"FldIsBusyOrUndChk.fd\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FldIsBusyOrUndChk.fd.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"v_c.v\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FldIsBusyOrUndChk.fd.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FldIsBusyOrUndChk.fd.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FldIsBusyOrUndChk.fd.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}]}," +
				"{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"ef\",\"parent\":\"FieldUnderCheck\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"vic\",\"parent\":\"FieldUnderCheck.vic\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheck.vic.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"fd.x.v\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheck.vic.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"fd.y.v\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheck.vic.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FieldUnderCheck.vic.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"FieldUnderCheck.v_c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"v_c.v\"}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"att\",\"parent\":\"FieldUnderCheck.att\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheck.att.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheck.att.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FieldUnderCheck.att.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheck.att.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}]}]}]}]}"
				);
		
		String kingCantEscapeString = new String(
				"{\"cr1\":\"0\",\"name\":\"KingCantEscape\",\"parent\":\"KingCantEscape\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"k\",\"parent\":\"figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figure.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"6\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"figure.x\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"figure.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figure.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}]}]}"
				);
		
		String kingCantEscape1String = new String(
				"{\"cr1\":\"0\",\"name\":\"KingCantEscape1\",\"parent\":\"KingCantEscape\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"k\",\"parent\":\"KingCantEscape.k\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"KingCantEscape.k.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"KingCantEscape.k.x\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"KingCantEscape.k.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"KingCantEscape.k.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"6\"}]}]},{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"f1\",\"parent\":\"FldIsBusyOrUndChk\",\"type\":\"c\"," +
				"\"compositeConceptIndexAttrs\":[],\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"FldIsBusyOrUndChk.v_c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"fd\",\"parent\":\"FldIsBusyOrUndChk.fd\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FldIsBusyOrUndChk.fd.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FldIsBusyOrUndChk.fd.ft\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FldIsBusyOrUndChk.fd.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FldIsBusyOrUndChk.fd.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}]}]}," +
				"{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"f2\",\"parent\":\"FldIsBusyOrUndChk\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"FldIsBusyOrUndChk.v_c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"fd\",\"parent\":\"FldIsBusyOrUndChk.fd\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FldIsBusyOrUndChk.fd.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FldIsBusyOrUndChk.fd.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FldIsBusyOrUndChk.fd.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FldIsBusyOrUndChk.fd.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}]}]}," +
				"{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"f3\",\"parent\":\"FldIsBusyOrUndChk\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"FldIsBusyOrUndChk.v_c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"fd\",\"parent\":\"FldIsBusyOrUndChk.fd\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FldIsBusyOrUndChk.fd.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FldIsBusyOrUndChk.fd.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"6\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FldIsBusyOrUndChk.fd.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FldIsBusyOrUndChk.fd.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}]}]}]}"
				);
		
		// NOTE: this is not used but do not remove this please
		// This seems not needed
		// TODO: not working abstract
		String defenceKingFromAttackString = new String(
				"{\"cr1\":\"0\",\"name\":\"DefenceKingFromAttack\",\"parent\":\"DefenceKingFromAttack\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"vic\",\"parent\":\"figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figureType\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"6\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"cordX\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"cordY\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figureColor\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"att\",\"parent\":\"figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figureType\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"cordX\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"cordY\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figureColor\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}]}," +
				"{\"negated\":\"0\",\"name\":\"a_c\",\"parent\":\"figureColor\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}]}"
				);
		
		String checkMateString = new String(
				"{\"cr1\":\"0\",\"name\":\"checkMate\",\"parent\":\"checkMate\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"uc\",\"parent\":\"FieldUnderCheck\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"vic\",\"parent\":\"FieldUnderCheck.vic\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheck.vic.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"es.k.x.v\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheck.vic.y\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"es.k.y.v\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheck.vic.ft\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"6\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FieldUnderCheck.vic.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}]}," +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"FieldUnderCheck.v_c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"v_c.v\"}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"att\",\"parent\":\"FieldUnderCheck.att\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"FieldUnderCheck.att.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"FieldUnderCheck.att.ft\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"FieldUnderCheck.att.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"0\",\"maxValue\":\"2\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"FieldUnderCheck.att.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"?\"}]}]}]}," +
				"{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"es\",\"parent\":\"KingCantEscape\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"k\",\"parent\":\"KingCantEscape.k\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"KingCantEscape.k.y\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"KingCantEscape.k.x\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"8\"}}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"KingCantEscape.k.c\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"v_c.v\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"KingCantEscape.k.ft\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"6\"}]}]}]}," +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"figureColor\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"IN\",\"value\":{\"minValue\":\"1\",\"maxValue\":\"2\"}}]}]}"
				);
		
		// TODO: not working abstract
		// This seems not needed
		String checkMateWhiteKing1String = new String(
				"{\"cr1\":\"0\",\"name\":\"checkMateWhiteKing1\",\"parent\":\"checkMate\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"uc\",\"parent\":\"FieldUnderCheck\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"vic\",\"parent\":\"figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"cordX\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"es.k.x.v\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"cordY\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"es.k.y.v\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figureType\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"6\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figureColor\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}]}," +
				"{\"negated\":\"0\",\"name\":\"v_c\",\"parent\":\"figureColor\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"v_c.v\"}]}," +
				"{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"att\",\"parent\":\"figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"cordX\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"3\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figureType\",\"type\":\"p\"," +
				"\"nucleusConceptIndexAttrs\":[],\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figureColor\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}," +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"cordY\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"2\"}]}]}]}," +
				"{\"cr1\":\"0\",\"negated\":\"0\",\"name\":\"es\",\"parent\":\"KingCantEscape\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[{\"cr1\":\"1\",\"negated\":\"0\",\"name\":\"k\",\"parent\":\"figure\",\"type\":\"c\",\"compositeConceptIndexAttrs\":[]," +
				"\"compConceptAttrs\":[" +
				"{\"negated\":\"0\",\"name\":\"y\",\"parent\":\"cordY\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"x\",\"parent\":\"cordX\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}," +
				"{\"negated\":\"0\",\"name\":\"c\",\"parent\":\"figureColor\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"v_c.v\"}]}," +
				"{\"negated\":\"0\",\"name\":\"ft\",\"parent\":\"figureType\",\"type\":\"p\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"6\"}]}]}]}," +
				"{\"name\":\"v_c\",\"parent\":\"figureColor\",\"type\":\"n\",\"nucleusConceptIndexAttrs\":[]," +
				"\"nucleusConceptValueAttrs\":[{\"name\":\"v\",\"oper\":\"=\",\"value\":\"1\"}]}]}"
				);
		
		PrimitiveConcept cordX, cordY, figureType, figureColor;
		CompositeConcept figure, pawn, knight, fieldUnderCheck, fieldUnderCheckOfKnight, fieldUnderCheckOfKnight1, fldIsBusyOrUndChk,
						fldIsBusy, fldUndChk, kingCantEscape, kingCantEscape1, defenceKingFromAttack, checkMate, checkMateWhiteKing1;
		Action moveKnight;
		JSONObject conceptJSON;
		try {
			
			conceptJSON = new JSONObject(cordXString);
			cordX = manager.createPrimitiveConcept(conceptJSON);
			
			conceptJSON = new JSONObject(cordYString);
			cordY = manager.createPrimitiveConcept(conceptJSON);
			
			conceptJSON = new JSONObject(figureTypeString);
			figureType = manager.createPrimitiveConcept(conceptJSON);
			
			conceptJSON = new JSONObject(figureColorString);
			figureColor = manager.createPrimitiveConcept(conceptJSON);
			
			conceptJSON = new JSONObject(figureString);
			figure = manager.createCompositeConcept(conceptJSON);
			
			conceptJSON = new JSONObject(pawnString);
			pawn = manager.createCompositeConcept(conceptJSON);
			
			conceptJSON = new JSONObject(knightString);
			knight = manager.createCompositeConcept(conceptJSON);
			
			conceptJSON = new JSONObject(moveKnightString);
			moveKnight = manager.createAction(conceptJSON);
			
			conceptJSON = new JSONObject(filedUnderCheckString);
			fieldUnderCheck = manager.createCompositeConcept(conceptJSON);
			
			conceptJSON = new JSONObject(fieldUnderCheckOfKnightString);
			fieldUnderCheckOfKnight = manager.createCompositeConcept(conceptJSON);
			
			conceptJSON = new JSONObject(fieldUnderCheckOfKnight1String);
			fieldUnderCheckOfKnight1 = manager.createCompositeConcept(conceptJSON);
			
			conceptJSON = new JSONObject(fldIsBusyOrUndChkString);
			fldIsBusyOrUndChk = manager.createCompositeConcept(conceptJSON);

			conceptJSON = new JSONObject(fldIsBusyString);
			fldIsBusy = manager.createCompositeConcept(conceptJSON);
			
			conceptJSON = new JSONObject(fldUndChkString);
			fldUndChk = manager.createCompositeConcept(conceptJSON);
			
			conceptJSON = new JSONObject(kingCantEscapeString);
			kingCantEscape = manager.createCompositeConcept(conceptJSON);
			
			conceptJSON = new JSONObject(kingCantEscape1String);
			kingCantEscape1 = manager.createCompositeConcept(conceptJSON);
			
			conceptJSON = new JSONObject(defenceKingFromAttackString);
			//defenceKingFromAttack = manager.createCompositeConcept(conceptJSON);
			
			conceptJSON = new JSONObject(checkMateString);
			checkMate = manager.createCompositeConcept(conceptJSON);
			
			conceptJSON = new JSONObject(checkMateWhiteKing1String);
			//checkMateWhiteKing1 = manager.createCompositeConcept(conceptJSON);
			
			System.out.println("Abstract creation in TestInsertAbstract is successfully finished");
			
		} catch (PPITException e) {
			e.printStackTrace();
			System.out.println("test failed while creating a concept in TestInsertAbstract class with PPITException");
			return;
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("Could not create json from the given string");
			return;
		}
		
		GANucleus xNode = null, yNode = null, figureColorNode = null, figureTypeNode = null;
		GAAR1 figureNode = null, pawnNode = null, knightNode = null;
		GAAction moveKnightNode = null;
		GAAbstract fieldUnderCheckNode = null, fieldUnderCheckOfKnightNode = null, fieldUnderCheckOfKnight1Node = null, 
			fldIsBusyOrUndChkNode = null, fldIsBusyNode = null, fldUndChkNode = null, kingCantEscapeNode = null, kingCantEscape1Node = null, checkMateNode = null;
		
		GA ga = new GA();
		GACreator gaCreator = new GACreator(ga);
		try {
			// nodes of nucleus types
			xNode = (GANucleus)gaCreator.insertPrimaryAbstract(cordX);
			yNode = (GANucleus)gaCreator.insertPrimaryAbstract(cordY);
			figureColorNode = (GANucleus)gaCreator.insertPrimaryAbstract(figureColor);
			figureTypeNode = (GANucleus)gaCreator.insertPrimaryAbstract(figureType);
			
			// nodes of AR1s
			figureNode = (GAAR1)gaCreator.insertPrimaryAbstract(figure);
			pawnNode = (GAAR1)gaCreator.insertPrimaryAbstract(pawn);
			knightNode = (GAAR1)gaCreator.insertPrimaryAbstract(knight);
			
			//node of moveKnight Action
			moveKnightNode = (GAAction)gaCreator.insertPrimaryAbstract(moveKnight);
			
			// nodes of abstracts
			fieldUnderCheckNode = (GAAbstract)gaCreator.insertPrimaryAbstract(fieldUnderCheck);
			fieldUnderCheckOfKnightNode = (GAAbstract)gaCreator.insertPrimaryAbstract(fieldUnderCheckOfKnight);
			fieldUnderCheckOfKnight1Node = (GAAbstract)gaCreator.insertPrimaryAbstract(fieldUnderCheckOfKnight1);
			
			fldIsBusyOrUndChkNode = (GAAbstract)gaCreator.insertPrimaryAbstract(fldIsBusyOrUndChk);
			fldIsBusyNode = (GAAbstract)gaCreator.insertPrimaryAbstract(fldIsBusy);
			fldUndChkNode = (GAAbstract)gaCreator.insertPrimaryAbstract(fldUndChk);
			
			kingCantEscapeNode = (GAAbstract)gaCreator.insertPrimaryAbstract(kingCantEscape);
			kingCantEscape1Node = (GAAbstract)gaCreator.insertPrimaryAbstract(kingCantEscape1);
			
			checkMateNode = (GAAbstract)gaCreator.insertPrimaryAbstract(checkMate);
			
			System.out.println("Node insertion part in TestInsertAbstract is successfully finished");
			
			
		} catch (PPITException e) {
			fail("failed to create some GA Node");
			e.printStackTrace();
		}
		
		try {
			
			//figure
			Iterator<GAEdge> iter = figureNode.getElements().iterator(), iter1;
			GAEdge edge, newEdge, attrEdge;
			assertTrue((edge = iter.next()).getSlave().equals(xNode) || edge.getSlave().equals(yNode) || edge.getSlave().equals(figureTypeNode) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(yNode) || edge.getSlave().equals(figureTypeNode) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(yNode) || edge.getSlave().equals(figureTypeNode) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(yNode) || edge.getSlave().equals(figureTypeNode) || edge.getSlave().equals(figureColorNode));
			assertFalse(iter.hasNext());
			System.out.println("Figure test passed.");
			
			//Pawn
			assertEquals(figureNode, pawnNode.getParents().iterator().next().getMaster());
			iter = pawnNode.getElements().iterator();
			assertTrue((edge = iter.next()).getSlave().equals(xNode) || edge.getSlave().equals(pawn.getAttribute("y").getGANode()) || edge.getSlave().equals(pawn.getAttribute("ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(pawn.getAttribute("y").getGANode()) || edge.getSlave().equals(pawn.getAttribute("ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(pawn.getAttribute("y").getGANode()) || edge.getSlave().equals(pawn.getAttribute("ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(pawn.getAttribute("y").getGANode()) || edge.getSlave().equals(pawn.getAttribute("ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(yNode, pawn.getAttribute("y").getGANode());
			assertNotSame(figureTypeNode, pawn.getAttribute("ft").getGANode());
			assertFalse(iter.hasNext());
			System.out.println("Pawn test passed.");
			
			//Knight
			assertEquals(figureNode, knightNode.getParents().iterator().next().getMaster());
			iter = knightNode.getElements().iterator();
			assertTrue((edge = iter.next()).getSlave().equals(xNode) || edge.getSlave().equals(yNode) || edge.getSlave().equals(knight.getAttribute("ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(yNode) || edge.getSlave().equals(knight.getAttribute("ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(yNode) || edge.getSlave().equals(knight.getAttribute("ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(yNode) || edge.getSlave().equals(knight.getAttribute("ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(figureTypeNode, knight.getAttribute("ft").getGANode());
			assertFalse(iter.hasNext());
			System.out.println("Knight test passed.");
			
			//moveKnight1
			iter = moveKnightNode.getPerformers().iterator();
			edge = iter.next();
			GAAbstract precond = (GAAbstract)edge.getMaster();
			iter = precond.getElements().iterator();
			assertTrue((edge = iter.next()).getSlave().equals(((CompositeConcept)precond.getAbstract()).getAttribute("vic").getGANode()) || edge.getSlave().equals(((CompositeConcept)precond.getAbstract()).getAttribute("att").getGANode()));
			if (edge.getSlave().equals(((CompositeConcept)precond.getAbstract()).getAttribute("att").getGANode())) {
				assertSame(knightNode, edge.getSlave());
			} else {
				assertSame(figureNode, edge.getSlave());
			}
			assertNotSame(((CompositeConcept)edge.getSlave().getAbstract()), ((CompositeConcept)(newEdge = iter.next()).getSlave().getAbstract()));
			// att and vic attributes of precondition has knight and figure parents
			assertNotSame(((CompositeConcept)edge.getSlave().getAbstract()).getParent(), ((CompositeConcept)newEdge.getSlave().getAbstract()).getParent());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(((CompositeConcept)precond.getAbstract()).getAttribute("vic").getGANode()) || edge.getSlave().equals(((CompositeConcept)precond.getAbstract()).getAttribute("att").getGANode()));
			if (edge.getSlave().equals(((CompositeConcept)precond.getAbstract()).getAttribute("att").getGANode())) {
				assertSame(knightNode, edge.getSlave());
			} else {
				assertSame(figureNode, edge.getSlave());
			}
			assertFalse(iter.hasNext());
			System.out.println("MoveKnight test passed.");
			
			//FieldUnderCheck
			iter = fieldUnderCheckNode.getElements().iterator();
			assertFalse(iter.hasNext());
			iter = fieldUnderCheckNode.getChildren().iterator();
			assertTrue((edge = iter.next()).getSlave().equals(fieldUnderCheckOfKnightNode));
			assertFalse(iter.hasNext());
			System.out.println("FieldUnderCheck test passed.");
			
			//FieldUnderCheckOfKnight
			iter = fieldUnderCheckOfKnightNode.getElements().iterator();
			assertFalse(iter.hasNext());
			iter = fieldUnderCheckOfKnightNode.getChildren().iterator();
			assertTrue((edge = iter.next()).getSlave().equals(fieldUnderCheckOfKnight1Node));
			assertFalse(iter.hasNext());
			System.out.println("FieldUnderCheckOfKnight test passed.");
			
			//FieldUnderCheckOfKnight1
			GAAR1 ar1Node = null;
			for (Iterator<GAEdge> iterator1 = figureColorNode.getComposers().iterator(); iterator1.hasNext();) {
				GANode temp = iterator1.next().getMaster();
				assertTrue(temp instanceof GAAR1);
				Iterator<GAEdge> iterator2 = temp.getComposers().iterator();
				if (iterator2.hasNext() && temp.getComposers().iterator().next().getMaster().equals(fieldUnderCheckOfKnight1Node)) {
					if (temp.getElements().size() == 1) {
							ar1Node = (GAAR1)temp;
							break;
					}
				}
			}
			assertNotNull(ar1Node);
			assertEquals(ar1Node.getElements().iterator().next().getSlave(), manager.getConcept("FieldUnderCheckOfKnight1.v_c").getGANode());
			iter = fieldUnderCheckOfKnight1Node.getElements().iterator();
			assertEquals(3, fieldUnderCheckOfKnight1Node.getElements().size());
			assertTrue(iter.hasNext());
			GANode k1VicNode = manager.getConcept("FieldUnderCheckOfKnight1.vic").getGANode();
			GANode k1AttNode = manager.getConcept("FieldUnderCheckOfKnight1.att").getGANode();

			edge = iter.next();
			assertEquals(fieldUnderCheckOfKnight1Node, edge.getMaster());
			GANode k1Element = edge.getSlave();
			
			if (k1Element.equals(ar1Node)) {System.out.println("they are equal723");}
			if (k1Element instanceof GAAR1) {
				System.out.println("this is ar1 and it's ok 697");
			} else {
				System.out.println("seems something is wrong 699");
			}
			System.out.println("the abstract is: " + k1Element.getAbstract().getName());
			assertTrue(k1Element.equals(k1VicNode)/*figure*/ || k1Element.equals(k1AttNode) || k1Element.equals(ar1Node));
			if ( k1Element.equals(k1AttNode)) {
				GANode attr = k1Element;
				iter1 = attr.getElements().iterator();
				attrEdge = iter1.next();
				assertTrue(attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.x").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.y").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheck.att.c").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight.att.ft").getGANode()));
				assertNotSame(((PrimitiveConcept)attrEdge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter1.next()).getSlave().getAbstract()).getType());
				attrEdge = newEdge;
				assertTrue(attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.x").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.y").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheck.att.c").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight.att.ft").getGANode()));
				assertNotSame(((PrimitiveConcept)attrEdge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter1.next()).getSlave().getAbstract()).getType());
				attrEdge = newEdge;
				assertTrue(attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.x").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.y").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheck.att.c").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight.att.ft").getGANode()));
				assertNotSame(((PrimitiveConcept)attrEdge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter1.next()).getSlave().getAbstract()).getType());
				attrEdge = newEdge;
				assertTrue(attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.x").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.y").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheck.att.c").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight.att.ft").getGANode()));
				assertNotSame(figureTypeNode, knight.getAttribute("ft").getGANode());
				assertFalse(iter1.hasNext());
			} else if (k1Element.equals(k1VicNode)) {
				assertSame(figureNode, k1Element);
			}
			assertTrue(iter.hasNext());
			edge = iter.next();
			
			assertEquals(fieldUnderCheckOfKnight1Node, edge.getMaster());
			k1Element = edge.getSlave();
			
			if (k1Element.equals(ar1Node)) {System.out.println("they are equal745");}
			if (k1Element instanceof GAAR1) {
				System.out.println("this is ar1 and it's ok 729");
			} else {
				System.out.println("seems something is wrong 731");
			}
			System.out.println("the abstract is: " + k1Element.getAbstract().getName());
			assertTrue(k1Element.equals(k1VicNode) || k1Element.equals(k1AttNode) || k1Element.equals(ar1Node));
			
			if (k1Element.equals(k1AttNode)) {
				GANode attr = edge.getSlave();
				iter1 = attr.getElements().iterator();
				assertTrue((attrEdge = iter1.next()).getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.x").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.y").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheck.att.c").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight.att.ft").getGANode()));
				assertNotSame(((PrimitiveConcept)attrEdge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter1.next()).getSlave().getAbstract()).getType());
				attrEdge = newEdge;
				assertTrue(attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.x").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.y").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheck.att.c").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight.att.ft").getGANode()));
				assertNotSame(((PrimitiveConcept)attrEdge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter1.next()).getSlave().getAbstract()).getType());
				attrEdge = newEdge;
				assertTrue(attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.x").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.y").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheck.att.c").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight.att.ft").getGANode()));
				assertNotSame(((PrimitiveConcept)attrEdge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter1.next()).getSlave().getAbstract()).getType());
				attrEdge = newEdge;
				assertTrue(attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.x").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.y").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheck.att.c").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight.att.ft").getGANode()));
				assertNotSame(figureTypeNode, knight.getAttribute("ft").getGANode());
				assertFalse(iter1.hasNext());
			} else if (edge.getSlave().equals(fieldUnderCheckOfKnight1.getAttribute("vic").getGANode())) {
				assertSame(figureNode, k1Element);
			}
			
			assertTrue(iter.hasNext());
			edge = iter.next();
			
			assertEquals(fieldUnderCheckOfKnight1Node, edge.getMaster());
			k1Element = edge.getSlave();
			
			if (k1Element.equals(ar1Node)) {System.out.println("they are equal766");}
			if (k1Element instanceof GAAR1) {
				System.out.println("this is ar1 and it's ok 757");
			} else {
				System.out.println("seems something is wrong 759");
			}
			System.out.println("the abstract is: " + k1Element.getAbstract().getName());
			assertTrue(k1Element.equals(k1VicNode) || k1Element.equals(k1AttNode) || k1Element.equals(ar1Node));
			if (k1Element.equals(k1AttNode)) {
				GANode attr = edge.getSlave();
				iter1 = attr.getElements().iterator();
				assertTrue((attrEdge = iter1.next()).getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.x").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.y").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheck.att.c").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight.att.ft").getGANode()));
				assertNotSame(((PrimitiveConcept)attrEdge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter1.next()).getSlave().getAbstract()).getType());
				attrEdge = newEdge;
				assertTrue(attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.x").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.y").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheck.att.c").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight.att.ft").getGANode()));
				assertNotSame(((PrimitiveConcept)attrEdge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter1.next()).getSlave().getAbstract()).getType());
				attrEdge = newEdge;
				assertTrue(attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.x").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.y").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheck.att.c").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight.att.ft").getGANode()));
				assertNotSame(((PrimitiveConcept)attrEdge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter1.next()).getSlave().getAbstract()).getType());
				attrEdge = newEdge;
				assertTrue(attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.x").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight1.att.y").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheck.att.c").getGANode()) || attrEdge.getSlave().equals(manager.getConcept("FieldUnderCheckOfKnight.att.ft").getGANode()));
				assertNotSame(figureTypeNode, knight.getAttribute("ft").getGANode());
				assertFalse(iter1.hasNext());
			} else if (edge.getSlave().equals(fieldUnderCheckOfKnight1.getAttribute("vic").getGANode())) {
				assertSame(figureNode, k1Element);
			}
			assertFalse(iter.hasNext());
			System.out.println("FieldUnderCheckOfKnight1 test passed.");
			
			//FldIsBusyOrUndChk
			
			iter = fldIsBusyOrUndChkNode.getChildren().iterator();
			assertTrue((edge = iter.next()).getSlave().equals(manager.getConcept("FieldIsBusy").getGANode()) || edge.getSlave().equals(manager.getConcept("FldUndChk").getGANode()));
			assertNotSame((CompositeConcept)edge.getSlave().getAbstract(), (CompositeConcept)(newEdge = iter.next()).getSlave().getAbstract());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(manager.getConcept("FieldIsBusy").getGANode()) || edge.getSlave().equals(manager.getConcept("FldUndChk").getGANode()));
			assertFalse(iter.hasNext());
			iter = fldIsBusyOrUndChkNode.getElements().iterator();
			assertFalse(iter.hasNext());
			System.out.println("FieldIsBuzyOrUnderCheck test passed.");
			
			//FieldIsBusy
			iter = fldIsBusyNode.getElements().iterator();
			ar1Node = null;
			for (Iterator<GAEdge> iterator1 = figureColorNode.getComposers().iterator(); iterator1.hasNext();) {
				GANode temp = iterator1.next().getMaster();
				assertTrue(temp instanceof GAAR1);
				Iterator<GAEdge> iterator2 = temp.getComposers().iterator();
				if (iterator2.hasNext() && temp.getComposers().iterator().next().getMaster().equals(fldIsBusyNode)
						) {
					if (temp.getElements().size() == 1) {
							ar1Node = (GAAR1)temp;
							break;
					}
					
				}
			}
			assertNotNull(ar1Node);
			System.out.println("name is : " + ar1Node.getAbstract().getName());
			assertEquals(ar1Node.getElements().iterator().next().getSlave(), manager.getConcept("FieldIsBusy.v_c").getGANode());
			assertEquals(ar1Node.getElements().iterator().next().getSlave(), manager.getConcept("figureColor").getGANode());
			
			edge = iter.next();
			if (edge.getSlave().equals(ar1Node)) {System.out.println("they are equal814");}
			GANode fb_fd = manager.getConcept("FieldIsBusy.fd").getGANode();
			assertNotNull(fb_fd);
			if (edge.getSlave().equals(fb_fd)) {
				System.out.println("fldIsBusyNode contains fb_fd");
			} else if (edge.getSlave().equals(ar1Node)) {
				System.out.println("fldIsBusyNode contains " + ar1Node.getAbstract().getName());
			} else {
				System.out.println("none of atts containt cause it's: " + edge.getSlave().getAbstract().getName());
				if (edge.getSlave() instanceof GAAR1) {
					System.out.println("this seems ok");
				} else {
					System.out.println("this seems not ok");
				}
			}
			assertTrue(edge.getSlave().equals(fb_fd) || edge.getSlave().equals(ar1Node));
			edge = iter.next();
			if (edge.getSlave().equals(ar1Node)) {System.out.println("they are equal817");}
			assertTrue(edge.getSlave().equals(fb_fd) || edge.getSlave().equals(ar1Node));
			assertFalse(iter.hasNext());
			iter = manager.getConcept("FieldIsBusy.fd").getGANode().getElements().iterator();
			edge = iter.next();
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(yNode) || edge.getSlave().equals(manager.getConcept("FieldIsBusy.fd.ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(yNode) || edge.getSlave().equals(manager.getConcept("FieldIsBusy.fd.ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(yNode) || edge.getSlave().equals(manager.getConcept("FieldIsBusy.fd.ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(xNode) || edge.getSlave().equals(yNode) || edge.getSlave().equals(manager.getConcept("FieldIsBusy.fd.ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertFalse(iter.hasNext());
			System.out.println("FieldIsBuzy test passed.");
			
			//FldUndChk
			iter = fldUndChkNode.getElements().iterator();
			ar1Node = null;
			for (Iterator<GAEdge> iterator1 = figureColorNode.getComposers().iterator(); iterator1.hasNext();) {
				GANode temp = iterator1.next().getMaster();
				assertTrue(temp instanceof GAAR1);
				Iterator<GAEdge> iterator2 = temp.getComposers().iterator();
				if (iterator2.hasNext() && temp.getComposers().iterator().next().getMaster().equals(fldUndChkNode)) {
					if (temp.getElements().size() == 1) {
						ar1Node = (GAAR1)temp;
						break;
					}
				}
			}
			assertNotNull(ar1Node);
			assertEquals(ar1Node.getElements().iterator().next().getSlave(), manager.getConcept("FldUndChk.v_c").getGANode());
			edge = iter.next();
			if (edge.getSlave().equals(ar1Node)) {System.out.println("they are equal849");}
			assertTrue(edge.getSlave().equals(figureNode) || edge.getSlave().equals(manager.getConcept("FldUndChk.ef").getGANode()) || edge.getSlave().equals(ar1Node));
			edge = iter.next();
			if (edge.getSlave().equals(ar1Node)) {System.out.println("they are equal852");}
			System.out.println("875: the name of the abstract: " + edge.getSlave().getAbstract().getName());
			assertTrue(edge.getSlave().equals(figureNode) || edge.getSlave().equals(manager.getConcept("FldUndChk.ef").getGANode()) || edge.getSlave().equals(ar1Node));
			edge = iter.next();
			assertTrue(edge.getSlave().equals(figureNode) || edge.getSlave().equals(manager.getConcept("FldUndChk.ef").getGANode()) || edge.getSlave().equals(ar1Node));
			assertFalse(iter.hasNext());
			iter = manager.getConcept("FldUndChk.ef").getGANode().getElements().iterator();
			assertFalse(iter.hasNext());
			iter = manager.getConcept("FldUndChk.ef").getGANode().getChildren().iterator();
			assertTrue(iter.next().getSlave().equals(fieldUnderCheckNode));
			assertFalse(iter.hasNext());
			System.out.println("FldUndCheck test passed.");
			
			//KingCantEscape
			iter = kingCantEscapeNode.getElements().iterator();
			assertFalse(iter.hasNext());
			iter = kingCantEscapeNode.getChildren().iterator();
			assertTrue((edge = iter.next()).getSlave().equals(manager.getConcept("KingCantEscape1").getGANode()));
			assertFalse(iter.hasNext());
			System.out.println("KingCantEscape test passed.");
			
			//KingCantEscape1
			iter = kingCantEscape1Node.getElements().iterator();
			assertTrue((edge = iter.next()).getSlave().equals(manager.getConcept("KingCantEscape1.k").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.f1").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.f2").getGANode()) ||  edge.getSlave().equals(manager.getConcept("KingCantEscape1.f3").getGANode()));
			assertNotSame((CompositeConcept)edge.getSlave().getAbstract(), (CompositeConcept)(newEdge = iter.next()).getSlave().getAbstract());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(manager.getConcept("KingCantEscape1.k").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.f1").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.f2").getGANode()) ||  edge.getSlave().equals(manager.getConcept("KingCantEscape1.f3").getGANode()));
			assertNotSame((CompositeConcept)edge.getSlave().getAbstract(), (CompositeConcept)(newEdge = iter.next()).getSlave().getAbstract());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(manager.getConcept("KingCantEscape1.k").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.f1").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.f2").getGANode()) ||  edge.getSlave().equals(manager.getConcept("KingCantEscape1.f3").getGANode()));
			assertNotSame((CompositeConcept)edge.getSlave().getAbstract(), (CompositeConcept)(newEdge = iter.next()).getSlave().getAbstract());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(manager.getConcept("KingCantEscape1.k").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.f1").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.f2").getGANode()) ||  edge.getSlave().equals(manager.getConcept("KingCantEscape1.f3").getGANode()));
			assertFalse(iter.hasNext());
			iter = manager.getConcept("KingCantEscape1.f1").getGANode().getChildren().iterator();
			assertTrue(iter.next().getSlave().equals(fldIsBusyOrUndChkNode));
			assertFalse(iter.hasNext());
			iter = manager.getConcept("KingCantEscape1.f2").getGANode().getChildren().iterator();
			assertTrue(iter.next().getSlave().equals(fldIsBusyOrUndChkNode));
			assertFalse(iter.hasNext());
			iter = manager.getConcept("KingCantEscape1.f3").getGANode().getChildren().iterator();
			assertTrue(iter.next().getSlave().equals(fldIsBusyOrUndChkNode));
			assertFalse(iter.hasNext());
			iter = manager.getConcept("KingCantEscape1.k").getGANode().getChildren().iterator();
			assertFalse(iter.hasNext());
			iter = manager.getConcept("KingCantEscape1.k").getGANode().getElements().iterator();
			assertTrue((edge = iter.next()).getSlave().equals(manager.getConcept("KingCantEscape1.k.x").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.k.y").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.k.ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(manager.getConcept("KingCantEscape1.k.x").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.k.y").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.k.ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(manager.getConcept("KingCantEscape1.k.x").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.k.y").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.k.ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertNotSame(((PrimitiveConcept)edge.getSlave().getAbstract()).getType(), ((PrimitiveConcept)(newEdge = iter.next()).getSlave().getAbstract()).getType());
			edge = newEdge;
			assertTrue(edge.getSlave().equals(manager.getConcept("KingCantEscape1.k.x").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.k.y").getGANode()) || edge.getSlave().equals(manager.getConcept("KingCantEscape1.k.ft").getGANode()) || edge.getSlave().equals(figureColorNode));
			assertFalse(iter.hasNext());
			System.out.println("KingCantEscape1 test passed.");
			
			//CheckMate
			iter = checkMateNode.getElements().iterator();
			ar1Node = null;
			for (Iterator<GAEdge> iterator1 = manager.getConcept("checkMate.v_c").getGANode().getComposers().iterator(); iterator1.hasNext();) {
				GANode temp = iterator1.next().getMaster();
				assertTrue(temp instanceof GAAR1);
				Iterator<GAEdge> iterator2 = temp.getComposers().iterator();
				if (iterator2.hasNext() && temp.getComposers().iterator().next().getMaster().equals(checkMateNode)) {
					if (temp.getElements().size() == 1) {
						ar1Node = (GAAR1)temp;
						break;
					}
				}
			}
			assertNotNull(ar1Node);
			assertTrue((edge = iter.next()).getSlave().equals(manager.getConcept("checkMate.uc").getGANode()) || edge.getSlave().equals(manager.getConcept("checkMate.es").getGANode()) || edge.getSlave().equals(ar1Node));
			assertTrue((edge = iter.next()).getSlave().equals(manager.getConcept("checkMate.uc").getGANode()) || edge.getSlave().equals(manager.getConcept("checkMate.es").getGANode()) || edge.getSlave().equals(ar1Node));
			assertTrue((edge = iter.next()).getSlave().equals(manager.getConcept("checkMate.uc").getGANode()) || edge.getSlave().equals(manager.getConcept("checkMate.es").getGANode()) || edge.getSlave().equals(ar1Node));
			iter = manager.getConcept("checkMate.uc").getGANode().getChildren().iterator();
			assertTrue((edge = iter.next()).getSlave().equals(manager.getConcept("FieldUnderCheck").getGANode()));
			iter = manager.getConcept("checkMate.es").getGANode().getChildren().iterator();
			assertTrue((edge = iter.next()).getSlave().equals(manager.getConcept("KingCantEscape").getGANode()));
			iter = manager.getConcept("checkMate.v_c").getGANode().getParents().iterator();
			assertTrue((edge = iter.next()).getMaster().equals(figureColorNode));
			System.out.println("CheckMate test passed.");
			
		} catch (PPITException e) {
			fail("failed while checking one of conncetions of the GA");
			e.printStackTrace();
		}
		
		System.out.println("Test is passed sucessfully");
			
	}
	
	private void IterateOverBeConnections(GANode node)
	{
		//System.out.println("slaves of GA Node - " + ((Concept)node.getAbstract()).getName() + ", i.e. children of x");
		for (GAEdge edge : node.getChildren()) {
			System.out.println(((Concept)node.getAbstract()).getName() + " is the parent of " + ((Concept)(edge.getSlave().getAbstract())).getName());
			IterateOverBeConnections(edge.getSlave());
		}
	}
	
	private void IterateOverHaveConnections(GANode node)
	{
		for (GAEdge edge : node.getElements()) {
			System.out.println(((Concept)node.getAbstract()).getName() + " has " + ((Concept)(edge.getSlave().getAbstract())).getName());
			IterateOverHaveConnections(edge.getSlave());
		}
	}
	
	private void IterateOverDoConnections(GANode node)
	{
		for (GAEdge edge : node.getPerformers()) {
			System.out.println(((Action)node.getAbstract()).getName() + " is performed by " + ((Concept)(edge.getMaster().getAbstract())).getName());
			IterateOverHaveConnections(edge.getMaster());
		}
	}

}
