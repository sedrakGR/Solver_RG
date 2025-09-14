package org.ppit.test.concept.expression;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.expression.Expression;
import org.ppit.core.concept.primitive.PrimitiveType;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.InvalidExpression;
import org.ppit.util.exception.PPITException;
import org.ppit.util.exception.UnsolvedDependency;

public class ExpressionTest {	
	@Test
	public void testCreateExpression() {
		try {
			new Expression("(5+4-3)/2 + p1.x/4");
		} catch (InvalidExpression e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testEvaluate() {
		try {
			Expression expression = new Expression("(5 + 4 - 3 )/2");
			assertEquals("Result", 3, expression.evaluate());
		} catch (InvalidExpression e) {
			fail(e.getMessage());
			e.printStackTrace();
		} catch (UnsolvedDependency e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testSetDependency() {
		try {
			TestHelper helper = new TestHelper();
			helper.resetAllResources();
			
			IdGroup idg1 = new IdGroup(1);
			// We don't pass PrimitiveType and GANode attributes, 
			// we believe they won't be used in the test - we were mistaken :)
			PrimitiveType pt = new PrimitiveType("x");
			NucleusInstance p1 = new NucleusInstance(4, pt, idg1, null);
			
			IdGroup idg2 = new IdGroup(1);
			NucleusInstance p2 = new NucleusInstance(1, pt, idg2, null);
			
			Expression expression = new Expression("(5+4 - p2.x)/2 + p1.x/4 + p2.x");
			ArrayList<String> dep = new ArrayList<String>() {{add("p1.x"); add("p2.x");}};
			assertEquals("Result", dep, expression.getDependencies());
			assertTrue(expression.setDependency("p1.x", p1));
			assertTrue(expression.setDependency("p2.x", p2));
			
			assertEquals("Result", 6, expression.evaluate());
			
			// Testing if cloning works.
			Expression clone = expression.clone();
			// Check that in the clone nothing is broken.
			assertEquals("Result", 6, expression.evaluate());
			assertEquals("Result", 6, clone.evaluate());
			assertEquals("Result", dep, clone.getDependencies());
			
			// modify the clone.
			IdGroup idg3 = new IdGroup(3);
			NucleusInstance p1_1 = new NucleusInstance(8, pt, idg3, null);
			assertTrue(clone.setDependency("p1.x", p1_1));

			assertEquals("Result", 6, expression.evaluate());
			assertEquals("Result", 7, clone.evaluate());
			
			IdGroup idg4 = new IdGroup(4);
			NucleusInstance p2_1 = new NucleusInstance(5, pt, idg4, null);
			assertTrue(expression.setDependency("p2.x", p2_1));
			
			assertEquals("Result", 7, clone.evaluate());
			assertEquals("Result", 8, expression.evaluate());
		} catch (PPITException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testGetDependencies() {
		try {
			Expression expression = new Expression("(5+4 - p2.x)/2 + p1.x/4");
			ArrayList<String> dep = new ArrayList<String>() {{add("p1.x"); add("p2.x");}};
			assertEquals("Result", dep, expression.getDependencies());
		} catch (InvalidExpression e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testGetExprString() {
		try {
			String expr = new String("p2.x+2*p3.x");
			Expression expression = new Expression(expr);
			assertEquals("Result", expr, expression.getExprString());
		}catch (InvalidExpression e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}

}
