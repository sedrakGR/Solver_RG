package org.ppit.test.concept.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.ppit.core.concept.primitive.Vocabulary;
import org.ppit.core.concept.primitive.VocabularyRegistry;
import org.ppit.util.exception.InvalidExpression;

public class TestVocabulary {

	private static List<String> FIGURE_NAMES = Arrays.asList(
			"empty", "pawn", "knight", "bishop", "rook", "queen", "king");

	@Test
	public void testIndexOfRoundTrip() throws InvalidExpression {
		Vocabulary v = new Vocabulary("type_classifier",
				"Chess Piece Type",
				Vocabulary.Kind.CATEGORICAL,
				FIGURE_NAMES,
				null, "FigureType");
		assertEquals(7, v.size());
		assertEquals(0, v.indexOf("empty"));
		assertEquals(6, v.indexOf("king"));
		assertEquals("pawn", v.nameOf(1));
		assertEquals("king", v.nameOf(6));
		assertNull(v.nameOf(99));
		assertTrue(v.hasName("queen"));
		assertFalse(v.hasName("pope"));
	}

	@Test(expected = InvalidExpression.class)
	public void testUnknownNameThrows() throws InvalidExpression {
		Vocabulary v = new Vocabulary("type_classifier", null,
				Vocabulary.Kind.CATEGORICAL, FIGURE_NAMES, null, null);
		v.indexOf("general");
	}

	@Test
	public void testDuplicateClassNameRejected() {
		List<String> bad = Arrays.asList("a", "b", "a");
		try {
			new Vocabulary("dup", null, Vocabulary.Kind.CATEGORICAL, bad, null, null);
			fail("Expected InvalidExpression for duplicate class name");
		} catch (InvalidExpression expected) {
			// ok
		}
	}

	@Test
	public void testEmptyClassListRejected() {
		try {
			new Vocabulary("empty", null, Vocabulary.Kind.CATEGORICAL,
					new ArrayList<String>(), null, null);
			fail("Expected InvalidExpression for empty class list");
		} catch (InvalidExpression expected) {
			// ok
		}
	}

	@Test
	public void testRegistryTestSeed() throws InvalidExpression {
		VocabularyRegistry.resetForTest();
		Vocabulary v = new Vocabulary("type_classifier", "Chess Piece Type",
				Vocabulary.Kind.CATEGORICAL, FIGURE_NAMES, null, "FigureType");
		VocabularyRegistry.getInstance().registerForTest(v);

		Vocabulary looked = VocabularyRegistry.getInstance().getById("type_classifier");
		assertNotNull(looked);
		assertEquals(7, looked.size());

		Vocabulary bySuggested = VocabularyRegistry.getInstance()
				.getBySuggestedPrimitiveTypeName("FigureType");
		assertNotNull(bySuggested);
		assertEquals("type_classifier", bySuggested.getId());

		assertNull(VocabularyRegistry.getInstance().getById("nope"));
	}
}
