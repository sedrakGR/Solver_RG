package org.ppit.test.concept.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.ppit.core.concept.rules.IRule;
import org.ppit.core.concept.rules.RuleIs;
import org.ppit.core.concept.primitive.Vocabulary;
import org.ppit.util.Definitions;
import org.ppit.util.exception.InvalidExpression;
import org.ppit.util.exception.PPITException;

public class TestRuleIs {

	private static final List<String> FIGURE_NAMES = Arrays.asList(
			"empty", "pawn", "knight", "bishop", "rook", "queen", "king");

	private Vocabulary figureVocab() throws InvalidExpression {
		return new Vocabulary("type_classifier", "Chess Piece Type",
				Vocabulary.Kind.CATEGORICAL, FIGURE_NAMES, null, "FigureType");
	}

	@Test
	public void testIsCheckByName() throws PPITException {
		Vocabulary v = figureVocab();
		IRule king = new RuleIs("king", v);

		assertEquals(Definitions.isOperator, king.getOperator());
		assertEquals("king", king.getExpressionString());

		// check() is integer-native; "king" resolves to index 6.
		assertTrue(king.check(6));
		assertFalse(king.check(0));
		assertFalse(king.check(1));
	}

	@Test
	public void testNotIsInvertsCheck() throws PPITException {
		Vocabulary v = figureVocab();
		IRule notKing = RuleIs.createNegated("king", v);

		assertEquals(Definitions.notIsOperator, notKing.getOperator());
		assertFalse(notKing.check(6));
		assertTrue(notKing.check(1));
		assertTrue(notKing.check(0));
	}

	@Test
	public void testIsOneOf() throws PPITException {
		Vocabulary v = figureVocab();
		IRule minor = new RuleIs("knight, bishop", v);

		assertTrue(minor.check(2)); // knight
		assertTrue(minor.check(3)); // bishop
		assertFalse(minor.check(5)); // queen

		List<Integer> vals = minor.getValues();
		assertEquals(2, vals.size());
		assertTrue(vals.contains(Integer.valueOf(2)));
		assertTrue(vals.contains(Integer.valueOf(3)));
	}

	@Test
	public void testUnknownSymbolRejectedAtConstruction() throws InvalidExpression {
		Vocabulary v = figureVocab();
		try {
			new RuleIs("general", v);
			fail("Expected InvalidExpression for unknown symbol 'general'");
		} catch (InvalidExpression expected) {
			// ok
		}
	}

	@Test
	public void testClonePreservesBehavior() throws PPITException {
		Vocabulary v = figureVocab();
		RuleIs orig = new RuleIs("queen", v);
		IRule copy = orig.clone();

		assertNotNull(copy);
		assertTrue(copy.check(5));
		assertFalse(copy.check(4));
		assertEquals(orig.getOperator(), copy.getOperator());
		assertEquals(orig.getExpressionString(), copy.getExpressionString());
	}

	@Test
	public void testJSONContainsClassifierId() throws PPITException {
		Vocabulary v = figureVocab();
		RuleIs rule = new RuleIs("king", v);
		String json = rule.getJSON();
		assertTrue("JSON should contain the classifier id", json.contains("type_classifier"));
		assertTrue("JSON should contain the symbolic value", json.contains("king"));
		assertTrue("JSON should contain the IS operator", json.contains("IS"));
	}

	@Test
	public void testConjunctionWithE() throws PPITException {
		Vocabulary v = figureVocab();
		RuleIs multi = new RuleIs("knight, bishop, queen", v); // {2,3,5}
		// Conjunct with RuleE for "3" (bishop) and we should keep bishop only.
		org.ppit.core.concept.rules.RuleE bishopOnly = new org.ppit.core.concept.rules.RuleE("3");
		ArrayList<IRule> rg = multi.conjunction(bishopOnly);
		assertFalse(rg.isEmpty());
		IRule r = rg.get(0);
		assertNotNull(r);
		assertTrue(r.check(3));
		assertFalse(r.check(5));
	}
}
