package org.ppit.test.concept.generalization;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.learn.*;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.PPITException;

public class TestMappingManager {
	TestHelper m_testHelper = new TestHelper();

	@Test
	public void test() {
		ConceptManager c_manager = ConceptManager.getInstance();
		String a_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"is_key\":\"0\",\"name\":\"a\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"\"}");
		
		String b_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"is_key\":\"0\",\"name\":\"b\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"\"}");
		
		String c_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"is_key\":\"0\",\"name\":\"c\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"\"}");
		
		String d_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"is_key\":\"0\",\"name\":\"d\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"\"}");

		String e_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"is_key\":\"0\",\"name\":\"e\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"\"}");

		String f_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"is_key\":\"0\",\"name\":\"f\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"\"}");

		String g_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"is_key\":\"0\",\"name\":\"g\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"\"}");

		String h_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"is_key\":\"0\",\"name\":\"h\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"\"}");

		String i_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"is_key\":\"0\",\"name\":\"i\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"\"}");

		String j_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"is_key\":\"0\",\"name\":\"j\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"\"}");

		String k_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"is_key\":\"0\",\"name\":\"k\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"\"}");
		
		String a1_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"name\":\"a1\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\"}");
		
		String a2_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"name\":\"a2\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\"}");
		
		String a11_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +"" +
				"\"name\":\"a11\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a1\"}");
		
		String a12_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +"" +
				"\"name\":\"a12\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a1\"}");
		
		String a13_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +"" +
				"\"name\":\"a13\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a1\"}");
		
		String a21_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +"" +
				"\"name\":\"a21\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a2\"}");
		
		String a111_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +"" +
				"\"name\":\"a111\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a11\"}");
		
		String a112_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +"" +
				"\"name\":\"a112\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a11\"}");
		
		String a211_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +"" +
				"\"name\":\"a211\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a21\"}");
		
		String a1121_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +"" +
				"\"name\":\"a1121\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a112\"}");
		
		String b1_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"name\":\"b1\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"b\"}");
		
		String c1_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"name\":\"c1\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"c\"}");
		
		String c2_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}]," +
				"\"name\":\"c2\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"c\"}");
		
		/*String x0_string = new String(
				"{\"cr1\":\"0\",\"compositeConceptIndexAttrs\":[],\"name\":\"x\",\"parent\":\"x\",\"compConceptAttrs\":[" +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a1\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a2\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a11\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a1\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"b1\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"b\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"b2\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"b\",\"type\":\"p\",\"negated\":\"\"}]}");
		
		String y0_string = new String(
				"{\"cr1\":\"0\",\"compositeConceptIndexAttrs\":[],\"name\":\"y\",\"parent\":\"y\",\"compConceptAttrs\":[" +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a3\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a4\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a13\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a1\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"b3\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"b\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"b4\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"b\",\"type\":\"p\",\"negated\":\"\"}]}");*/

		String x_string = new String(
				"{\"cr1\":\"0\",\"compositeConceptIndexAttrs\":[],\"name\":\"x\",\"parent\":\"x\",\"compConceptAttrs\":[" +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a1121x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a1121\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a12x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a12\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"a12x.v\"}],\"name\":\"a2x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"a211x.v\"}],\"name\":\"a3x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"b1x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"b1\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"c2x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"c2\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a211x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a21\",\"type\":\"p\",\"negated\":\"\"}]}");
		
		String y_string = new String(
				"{\"cr1\":\"0\",\"compositeConceptIndexAttrs\":[],\"name\":\"y\",\"parent\":\"y\",\"compConceptAttrs\":[" +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"c1x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"c1\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"a12x.v\"}],\"name\":\"a4x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"a211x.v\"}],\"name\":\"a5x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a111x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a111\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a1x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a1\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a12x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a13\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a211x\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a211\",\"type\":\"p\",\"negated\":\"\"}]}");
		
		String gen1_manual = new String(
				"{'name':'gen_manual1','parent':'','cr1':'0','type':'c','compositeConceptIndexAttrs':[],'compConceptAttrs':["+
				"{'negated':'0','name':'na21','parent':'a21','type':'p','is_key':'0','nucleusConceptIndexAttrs':[],'nucleusConceptValueAttrs':[{'name':'value','value':'0','oper':'='}]}," +
				"{'negated':'0','name':'nc','parent':'c','type':'p','is_key':'0','nucleusConceptIndexAttrs':[],'nucleusConceptValueAttrs':[{'name':'value','value':'0','oper':'='}]}," +
				"{'negated':'0','name':'na','parent':'a','type':'p','is_key':'0','nucleusConceptIndexAttrs':[],'nucleusConceptValueAttrs':[{'name':'value','value':'0','oper':'='}]}," +
				"{'negated':'0','name':'na11','parent':'a11','type':'p','is_key':'0','nucleusConceptIndexAttrs':[],'nucleusConceptValueAttrs':[{'name':'value','value':'0','oper':'='}]}," +
				"{'negated':'0','name':'na1','parent':'a1','type':'p','is_key':'0','nucleusConceptIndexAttrs':[],'nucleusConceptValueAttrs':[{'name':'value','value':'0','oper':'='}]}]}");
		
		String gen2_manual = new String(
				"{'name':'gen_manual2','parent':'','cr1':'0','type':'c','compositeConceptIndexAttrs':[],'compConceptAttrs':["+
				"{'negated':'0','name':'na21','parent':'a21','type':'p','is_key':'0','nucleusConceptIndexAttrs':[],'nucleusConceptValueAttrs':[{'name':'value','value':'0','oper':'='}]}," +
				"{'negated':'0','name':'nc','parent':'c','type':'p','is_key':'0','nucleusConceptIndexAttrs':[],'nucleusConceptValueAttrs':[{'name':'value','value':'0','oper':'='}]}," +
				"{'negated':'0','name':'na','parent':'a','type':'p','is_key':'0','nucleusConceptIndexAttrs':[],'nucleusConceptValueAttrs':[{'name':'value','value':'0','oper':'='}]}," +
				"{'negated':'0','name':'na11','parent':'a11','type':'p','is_key':'0','nucleusConceptIndexAttrs':[],'nucleusConceptValueAttrs':[{'name':'value','value':'0','oper':'='}]}," +
				"{'negated':'0','name':'na1','parent':'a1','type':'p','is_key':'0','nucleusConceptIndexAttrs':[],'nucleusConceptValueAttrs':[{'name':'value','value':'0','oper':'='}]}]}");
		
		String primX_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"primA\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\"}");
		
		String primY_string = new String(
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"primB\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\"}");

		String AR1X_string = new String(
				"{\"cr1\":\"1\",\"compositeConceptIndexAttrs\":[],\"name\":\"x_ar1\",\"parent\":\"x_ar1\",\"compConceptAttrs\":[" +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a1\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"b1\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"b\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"c1\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"c\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"d1\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"d\",\"type\":\"p\",\"negated\":\"\"}]}");
		
		String AR1Y_string = new String(
				"{\"cr1\":\"1\",\"compositeConceptIndexAttrs\":[],\"name\":\"y_ar1\",\"parent\":\"y_ar1\",\"compConceptAttrs\":[" +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"a2\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"a\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"b2\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"b\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"e2\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"e\",\"type\":\"p\",\"negated\":\"\"}," +
				"{\"nucleusConceptValueAttrs\":[{\"oper\":\"=\",\"name\":\"value\",\"value\":\"0\"}],\"name\":\"d2\",\"nucleusConceptIndexAttrs\":[],\"parent\":\"d\",\"type\":\"p\",\"negated\":\"\"}]}");
		
		JSONObject tmpJSON;
		try {
			tmpJSON = new JSONObject(a_string);
			PrimitiveConcept a_concept = c_manager.createPrimitiveConcept(tmpJSON);
			a_concept.getJSON();
			
			tmpJSON = new JSONObject(b_string);
			PrimitiveConcept b_concept = c_manager.createPrimitiveConcept(tmpJSON);
			b_concept.getJSON();
			
			tmpJSON = new JSONObject(c_string);
			PrimitiveConcept c_concept = c_manager.createPrimitiveConcept(tmpJSON);
			c_concept.getJSON();

			tmpJSON = new JSONObject(d_string);
			PrimitiveConcept d_concept = c_manager.createPrimitiveConcept(tmpJSON);
			d_concept.getJSON();

			tmpJSON = new JSONObject(e_string);
			PrimitiveConcept e_concept = c_manager.createPrimitiveConcept(tmpJSON);
			e_concept.getJSON();

			tmpJSON = new JSONObject(f_string);
			PrimitiveConcept f_concept = c_manager.createPrimitiveConcept(tmpJSON);
			f_concept.getJSON();

			tmpJSON = new JSONObject(g_string);
			PrimitiveConcept g_concept = c_manager.createPrimitiveConcept(tmpJSON);
			g_concept.getJSON();

			tmpJSON = new JSONObject(h_string);
			PrimitiveConcept h_concept = c_manager.createPrimitiveConcept(tmpJSON);
			h_concept.getJSON();

			tmpJSON = new JSONObject(i_string);
			PrimitiveConcept i_concept = c_manager.createPrimitiveConcept(tmpJSON);
			i_concept.getJSON();

			tmpJSON = new JSONObject(j_string);
			PrimitiveConcept j_concept = c_manager.createPrimitiveConcept(tmpJSON);
			j_concept.getJSON();

			tmpJSON = new JSONObject(k_string);
			PrimitiveConcept k_concept = c_manager.createPrimitiveConcept(tmpJSON);
			k_concept.getJSON();
			
			tmpJSON = new JSONObject(a1_string);
			PrimitiveConcept a1_concept = c_manager.createPrimitiveConcept(tmpJSON);
			a1_concept.getJSON();
			
			tmpJSON = new JSONObject(a2_string);
			PrimitiveConcept a2_concept = c_manager.createPrimitiveConcept(tmpJSON);
			a2_concept.getJSON();
			
			tmpJSON = new JSONObject(a11_string);
			PrimitiveConcept a11_concept = c_manager.createPrimitiveConcept(tmpJSON);
			a11_concept.getJSON();
			
			tmpJSON = new JSONObject(a12_string);
			PrimitiveConcept a12_concept = c_manager.createPrimitiveConcept(tmpJSON);
			a12_concept.getJSON();
			
			tmpJSON = new JSONObject(a13_string);
			PrimitiveConcept a13_concept = c_manager.createPrimitiveConcept(tmpJSON);
			a13_concept.getJSON();
			
			tmpJSON = new JSONObject(a21_string);
			PrimitiveConcept a21_concept = c_manager.createPrimitiveConcept(tmpJSON);
			a21_concept.getJSON();
			
			tmpJSON = new JSONObject(a111_string);
			PrimitiveConcept a111_concept = c_manager.createPrimitiveConcept(tmpJSON);
			a111_concept.getJSON();
			
			tmpJSON = new JSONObject(a112_string);
			PrimitiveConcept a112_concept = c_manager.createPrimitiveConcept(tmpJSON);
			a112_concept.getJSON();
			
			tmpJSON = new JSONObject(a211_string);
			PrimitiveConcept a211_concept = c_manager.createPrimitiveConcept(tmpJSON);
			a211_concept.getJSON();
			
			tmpJSON = new JSONObject(a1121_string);
			PrimitiveConcept a1121_concept = c_manager.createPrimitiveConcept(tmpJSON);
			a1121_concept.getJSON();
			
			tmpJSON = new JSONObject(b1_string);
			PrimitiveConcept b1_concept = c_manager.createPrimitiveConcept(tmpJSON);
			b1_concept.getJSON();
			
			tmpJSON = new JSONObject(c1_string);
			PrimitiveConcept c1_concept = c_manager.createPrimitiveConcept(tmpJSON);
			c1_concept.getJSON();
			
			tmpJSON = new JSONObject(c2_string);
			PrimitiveConcept c2_concept = c_manager.createPrimitiveConcept(tmpJSON);
			c2_concept.getJSON();
			
			tmpJSON = new JSONObject(x_string);
			CompositeConcept x_concept = c_manager.createCompositeConcept(tmpJSON);
			x_concept.getJSON();
			
			tmpJSON = new JSONObject(y_string);
			CompositeConcept y_concept = c_manager.createCompositeConcept(tmpJSON);
			y_concept.getJSON();
			
			tmpJSON = new JSONObject(primX_string);
			PrimitiveConcept primX_concept = c_manager.createPrimitiveConcept(tmpJSON);
			primX_concept.getJSON();
			
			tmpJSON = new JSONObject(primY_string);
			PrimitiveConcept primY_concept = c_manager.createPrimitiveConcept(tmpJSON);
			primY_concept.getJSON();
			
			tmpJSON = new JSONObject(AR1X_string);
			CompositeConcept AR1X_concept = c_manager.createCompositeConcept(tmpJSON);
			AR1X_concept.getJSON();
			
			tmpJSON = new JSONObject(AR1Y_string);
			CompositeConcept AR1Y_concept = c_manager.createCompositeConcept(tmpJSON);
			AR1Y_concept.getJSON();
			
			tmpJSON = new JSONObject(gen1_manual);
			CompositeConcept gen1_manual_concept = c_manager.createCompositeConcept(tmpJSON);
			gen1_manual_concept.getJSON();
			
			tmpJSON = new JSONObject(gen2_manual);
			CompositeConcept gen2_manual_concept = c_manager.createCompositeConcept(tmpJSON);
			gen2_manual_concept.getJSON();
			
			/*MappingManager manager = new MappingManager();
			manager.initializeMappingManager(x_concept, y_concept);
			
			manager.show();
			manager.extract();
			manager.print_result();
			
			GeneralizePrimitives primitive = new GeneralizePrimitives();
			
			primitive.initialize(primX_concept, primY_concept);
			primitive.generalize();
			primitive.print_result();
			System.out.println(primitive.generateConcepts().getJSON());*/
			
			/*GeneralizeAR1s ar1_composite = new GeneralizeAR1s();
			
			ar1_composite.initialize(AR1X_concept, AR1Y_concept);
			ar1_composite.generalize();
			ar1_composite.print_result();
			System.out.println(ar1_composite.generateConcepts().getJSON());*/
			
			GeneralizeComposites compositeGenerator = new GeneralizeComposites();
			
			compositeGenerator.initialize(x_concept, y_concept);
			compositeGenerator.generalize();
			for(Concept c: compositeGenerator.generateConcepts())
				System.out.println(c.getJSON());
			compositeGenerator.generateDependencies(compositeGenerator.m_peList.get(0));
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (PPITException e) {
			e.printStackTrace();
		}
	}

}
