package org.ppit.test.concept.expression.analyze;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.ppit.core.concept.expression.analyze.InfixToPostfix;
import org.ppit.core.concept.expression.analyze.SplitExpression;

public class InfixToPostfixTest {

	@Test
	public void testDoTrans() {
		InfixToPostfix itp = new InfixToPostfix();
		// (( 5 + 10) / p2.x * 2) - 3
		ArrayList<String> tokens = new ArrayList<String>(){{
			add("5");
			add("10");
			add("+");
			add("p2.x");
			add("/");
			add("2");
			add("*");
			add("3");
			add("-");
		}};
		
		ArrayList<String> actual = new ArrayList<String>(){{
			add("(");
			add("(");
			add("5");
			add("+");
			add("10");
			add(")");
			add("/");
			add("p2.x");
			add("*");
			add("2");
			add(")");
			add("-");
			add("3");
		}};
		
		assertEquals("Result", tokens, itp.doTrans(actual));
	}

}
