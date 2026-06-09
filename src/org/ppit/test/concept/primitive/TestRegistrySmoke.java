package org.ppit.test.concept.primitive;

import java.util.ArrayList;
import java.util.List;

import org.ppit.core.concept.primitive.Vocabulary;
import org.ppit.core.concept.primitive.VocabularyRegistry;

/**
 * Ad-hoc main used to verify that the classifiers drop-folder is picked up.
 * Run from the project root:
 *   java -cp build/classes/java/main:... org.ppit.test.concept.primitive.TestRegistrySmoke
 */
public class TestRegistrySmoke {
	public static void main(String[] args) {
		VocabularyRegistry reg = VocabularyRegistry.getInstance();
		List<Vocabulary> all = new ArrayList<Vocabulary>(reg.listAll());
		System.out.println("Registry size: " + all.size());
		for (Vocabulary v : all) {
			System.out.println(" - " + v.getId()
					+ " [" + v.getKind() + "]"
					+ " displayName='" + v.getDisplayName() + "'"
					+ " classes=" + v.getClassNames()
					+ " suggestedPrimitiveType=" + v.getSuggestedPrimitiveTypeName());
		}
	}
}
