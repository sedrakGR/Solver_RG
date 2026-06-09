package org.ppit.actions.primitive;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ppit.core.concept.primitive.Vocabulary;
import org.ppit.core.concept.primitive.VocabularyRegistry;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Returns the set of NN classifiers currently registered in the
 * {@link VocabularyRegistry} so the authoring UI can present them as options
 * when the user declares a nucleus or primitive.
 *
 * Response shape:
 * <pre>
 * {
 *   "classifiers": [
 *     {
 *       "classifierId": "type_classifier",
 *       "displayName": "Chess Piece Type",
 *       "kind": "categorical",
 *       "pluggable": true,
 *       "classNames": ["pawn","knight","bishop","rook","queen","king"],
 *       "suggestedPrimitiveTypeName": "FigureType"
 *     },
 *     ...
 *   ]
 * }
 * </pre>
 * "pluggable" is true iff the kind is categorical (RuleIs can bind to it).
 */
public class ListClassifiers extends ActionSupport {
	private static final long serialVersionUID = 1L;

	private JSONObject json;

	public String execute() {
		json = new JSONObject();
		JSONArray arr = new JSONArray();

		VocabularyRegistry reg = VocabularyRegistry.getInstance();
		List<Vocabulary> vocabs = new ArrayList<Vocabulary>(reg.listAll());
		for (Vocabulary v : vocabs) {
			JSONObject entry = new JSONObject();
			entry.put("classifierId", v.getId());
			entry.put("displayName", v.getDisplayName());
			entry.put("kind", v.getKind().name().toLowerCase());
			entry.put("pluggable", v.getKind() == Vocabulary.Kind.CATEGORICAL);
			if (v.getSuggestedPrimitiveTypeName() != null) {
				entry.put("suggestedPrimitiveTypeName", v.getSuggestedPrimitiveTypeName());
			}
			JSONArray names = new JSONArray();
			names.addAll(v.getClassNames());
			entry.put("classNames", names);
			arr.add(entry);
		}
		json.put("classifiers", arr);
		return SUCCESS;
	}

	public JSONObject getJson() {
		return json;
	}
}
