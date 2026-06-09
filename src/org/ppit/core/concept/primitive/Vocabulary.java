package org.ppit.core.concept.primitive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.ppit.util.exception.InvalidExpression;

/**
 * A Vocabulary is a name-to-index table, optionally bound to a trained NN
 * classifier's {@code class_names} output vocabulary. It is the bridge that
 * lets authors refer to categorical classifier values by name (e.g. "king")
 * while the rule engine continues to operate on integers underneath.
 *
 * Vocabularies are immutable once constructed. They do not know about Solver
 * concepts or rules; they are pure lookup tables.
 */
public final class Vocabulary {

	/** Stable machine id (matches the NN manifest's classifierId). */
	private final String m_id;

	/** Human-readable label shown in the UI. */
	private final String m_displayName;

	/** Kind discriminator. Categorical = usable with RuleIs today. */
	public enum Kind { CATEGORICAL, RELATION, REGRESSION }
	private final Kind m_kind;

	/**
	 * Ordered class names. Index in this list is the integer value the rule
	 * engine sees. The order is authoritative and comes from the manifest.
	 */
	private final List<String> m_classNames;

	/** Reverse lookup built once at construction. */
	private final HashMap<String, Integer> m_nameToIndex;

	/** Optional path to the backing checkpoint. May be null. */
	private final String m_checkpointPath;

	/** Optional producer hint: which PrimitiveType this vocab was intended for. */
	private final String m_suggestedPrimitiveTypeName;

	public Vocabulary(String id,
	                  String displayName,
	                  Kind kind,
	                  List<String> classNames,
	                  String checkpointPath,
	                  String suggestedPrimitiveTypeName) throws InvalidExpression {
		if (id == null || id.isEmpty()) {
			throw new InvalidExpression("Vocabulary id must be non-empty.");
		}
		if (classNames == null || classNames.isEmpty()) {
			throw new InvalidExpression("Vocabulary '" + id + "' must declare a non-empty classNames list.");
		}

		m_id = id;
		m_displayName = (displayName != null && !displayName.isEmpty()) ? displayName : id;
		m_kind = (kind != null) ? kind : Kind.CATEGORICAL;
		m_classNames = Collections.unmodifiableList(new ArrayList<String>(classNames));
		m_checkpointPath = checkpointPath;
		m_suggestedPrimitiveTypeName = suggestedPrimitiveTypeName;

		m_nameToIndex = new HashMap<String, Integer>();
		for (int i = 0; i < m_classNames.size(); ++i) {
			String name = m_classNames.get(i);
			if (name == null || name.isEmpty()) {
				throw new InvalidExpression("Vocabulary '" + id + "' has a null/empty class name at index " + i + ".");
			}
			if (m_nameToIndex.containsKey(name)) {
				throw new InvalidExpression("Vocabulary '" + id + "' has duplicate class name: " + name);
			}
			m_nameToIndex.put(name, i);
		}
	}

	public String getId() {
		return m_id;
	}

	public String getDisplayName() {
		return m_displayName;
	}

	public Kind getKind() {
		return m_kind;
	}

	public List<String> getClassNames() {
		return m_classNames;
	}

	public int size() {
		return m_classNames.size();
	}

	public String getCheckpointPath() {
		return m_checkpointPath;
	}

	public String getSuggestedPrimitiveTypeName() {
		return m_suggestedPrimitiveTypeName;
	}

	public boolean hasName(String name) {
		return m_nameToIndex.containsKey(name);
	}

	/**
	 * @return the integer index assigned to {@code name}.
	 * @throws InvalidExpression if the name is not in the vocabulary.
	 */
	public int indexOf(String name) throws InvalidExpression {
		Integer idx = m_nameToIndex.get(name);
		if (idx == null) {
			throw new InvalidExpression("Vocabulary '" + m_id + "' has no class named '" + name + "'. "
					+ "Known: " + m_classNames);
		}
		return idx.intValue();
	}

	/**
	 * @return the name at {@code index}, or null if out of range.
	 */
	public String nameOf(int index) {
		if (index < 0 || index >= m_classNames.size()) {
			return null;
		}
		return m_classNames.get(index);
	}
}
