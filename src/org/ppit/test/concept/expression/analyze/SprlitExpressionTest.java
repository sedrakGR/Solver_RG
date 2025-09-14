package org.ppit.test.concept.expression.analyze;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.ppit.core.concept.expression.analyze.SplitExpression;

public class SprlitExpressionTest {

	@Test
	public void testSeparateString() {
		SplitExpression sp = new SplitExpression("( 5+ 10) - p2.x");
		ArrayList<String> tokens = new ArrayList<String>(){{
			add("(");
			add("5");
			add("+");
			add("10");
			add(")");
			add("-");
			add("p2.x");
		}}; 
		
		assertEquals("Result", tokens, sp.separateString());
	}

}
