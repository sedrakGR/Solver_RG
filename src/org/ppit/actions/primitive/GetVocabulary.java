package org.ppit.actions.primitive;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ppit.core.concept.primitive.Vocabulary;
import org.ppit.core.concept.primitive.VocabularyRegistry;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Returns the full vocabulary for a single classifier id, primarily so the
 * value-attribute dropdown in the primitive editor can populate its options.
 *
 * Request parameter: classifierId (required).
 * Response shape:
 * <pre>
 * {
 *   "classifierId": "type_classifier",
 *   "displayName": "Chess Piece Type",
 *   "kind": "categorical",
 *   "classNames": ["pawn","knight","bishop","rook","queen","king"]
 * }
 * </pre>
 * If the classifier is unknown, returns {"error":"not_found","classifierId":"..."}.
 */
public class GetVocabulary extends ActionSupport {
	private static final long serialVersionUID = 1L;

	private String classifierId;
	private JSONObject json;

	public String execute() {
		json = new JSONObject();
		if (classifierId == null || classifierId.isEmpty()) {
			json.put("error", "missing_classifierId");
			return SUCCESS;
		}
		Vocabulary v = VocabularyRegistry.getInstance().getById(classifierId);
		if (v == null) {
			json.put("error", "not_found");
			json.put("classifierId", classifierId);
			return SUCCESS;
		}
		json.put("classifierId", v.getId());
		json.put("displayName", v.getDisplayName());
		json.put("kind", v.getKind().name().toLowerCase());
		JSONArray names = new JSONArray();
		names.addAll(v.getClassNames());
		json.put("classNames", names);
		if (v.getSuggestedPrimitiveTypeName() != null) {
			json.put("suggestedPrimitiveTypeName", v.getSuggestedPrimitiveTypeName());
		}
		return SUCCESS;
	}

	public void setClassifierId(String classifierId) {
		this.classifierId = classifierId;
	}

	public String getClassifierId() {
		return classifierId;
	}

	public JSONObject getJson() {
		return json;
	}
}
