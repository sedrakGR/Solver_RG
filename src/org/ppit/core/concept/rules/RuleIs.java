package org.ppit.core.concept.rules;

import java.util.ArrayList;

import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.primitive.Vocabulary;
import org.ppit.util.Definitions;
import org.ppit.util.ParseUtil;
import org.ppit.util.exception.InvalidExpression;
import org.ppit.util.exception.UnsolvedDependency;

/**
 * "IS" rule — categorical equality against a classifier {@link Vocabulary}.
 *
 * The rule stores symbolic names ("king", "queen") for the human-facing
 * authoring surface but delegates to integer comparison underneath, so the
 * GA/rule pipeline is unchanged. At most one name is typical; a comma-
 * separated list is accepted to mean "any of these" (e.g. minor piece).
 */
public class RuleIs extends IRule {

	/** The vocabulary that gives the names their indices. */
	private Vocabulary m_vocabulary = null;

	/** The symbolic names this rule accepts. */
	private ArrayList<String> m_symbols = new ArrayList<String>();

	/** Cached resolved integer indices (parallel to m_symbols). */
	private ArrayList<Integer> m_resolvedIndices = new ArrayList<Integer>();

	/** Set true for the "NOT IS" variant. */
	protected boolean m_negated = false;

	public RuleIs(String expression, Vocabulary vocabulary) throws InvalidExpression {
		if (vocabulary == null) {
			throw new InvalidExpression("RuleIs requires a non-null Vocabulary.");
		}
		m_vocabulary = vocabulary;
		setExpression(expression);
	}

	protected RuleIs() {}

	public static RuleIs createNegated(String expression, Vocabulary vocabulary) throws InvalidExpression {
		RuleIs r = new RuleIs(expression, vocabulary);
		r.m_negated = true;
		return r;
	}

	private void setExpression(String expression) throws InvalidExpression {
		// Tolerate JSON-array form (e.g. '["pawn","knight"]') in addition to the
		// canonical comma-list form ('pawn, knight'). Some browsers / jQuery
		// versions ship the multi-select payload as the former; we accept both.
		String normalized = expression == null ? "" : expression.trim();
		if (normalized.startsWith("[") && normalized.endsWith("]")) {
			normalized = normalized.substring(1, normalized.length() - 1);
		}
		ArrayList<String> values = ParseUtil.splitExpression(normalized);
		if (values.isEmpty()) {
			throw new InvalidExpression("RuleIs expression cannot be empty.");
		}
		m_symbols = new ArrayList<String>();
		m_resolvedIndices = new ArrayList<Integer>();
		for (String raw : values) {
			// Strip surrounding quotes/brackets/whitespace per token defensively.
			String name = raw.trim();
			while (name.length() > 0 && (name.charAt(0) == '"' || name.charAt(0) == '\'' || name.charAt(0) == '[')) {
				name = name.substring(1).trim();
			}
			while (name.length() > 0 && (name.charAt(name.length()-1) == '"' || name.charAt(name.length()-1) == '\'' || name.charAt(name.length()-1) == ']')) {
				name = name.substring(0, name.length() - 1).trim();
			}
			if (name.isEmpty()) continue;
			int idx = m_vocabulary.indexOf(name); // throws InvalidExpression on unknown
			m_symbols.add(name);
			m_resolvedIndices.add(Integer.valueOf(idx));
		}
		if (m_symbols.isEmpty()) {
			throw new InvalidExpression("RuleIs expression '" + expression + "' resolved to zero symbols.");
		}
	}

	public Vocabulary getVocabulary() {
		return m_vocabulary;
	}

	public ArrayList<String> getSymbols() {
		return new ArrayList<String>(m_symbols);
	}

	@Override
	public IRule clone() {
		RuleIs c = new RuleIs();
		c.m_vocabulary = m_vocabulary;
		c.m_symbols = new ArrayList<String>(m_symbols);
		c.m_resolvedIndices = new ArrayList<Integer>(m_resolvedIndices);
		c.m_negated = m_negated;
		return c;
	}

	@Override
	public boolean setDependency(String depNodeName, NucleusInstance refToInstance) {
		// IS rules reference classifier names, not other nucleus nodes; no dependencies to set.
		return false;
	}

	@Override
	public ArrayList<String> getDependencies() {
		return new ArrayList<String>();
	}

	@Override
	public boolean check(int value) throws UnsolvedDependency {
		boolean hit = false;
		for (Integer idx : m_resolvedIndices) {
			if (idx.intValue() == value) { hit = true; break; }
		}
		return m_negated ? !hit : hit;
	}

	@Override
	public String getOperator() {
		return m_negated ? Definitions.notIsOperator : Definitions.isOperator;
	}

	@Override
	public String getExpressionString() {
		return ParseUtil.composeExpression(m_symbols);
	}

	@Override
	public ArrayList<Integer> getValues() {
		return new ArrayList<Integer>(m_resolvedIndices);
	}

	@Override
	public ArrayList<IRule> conjunction(IRule rule) {
		// Reduce to an integer-valued rule by projecting this RuleIs's resolved indices
		// to a RuleE, then delegate to the integer-native conjunction logic.
		ArrayList<IRule> rg = new ArrayList<IRule>();
		try {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < m_resolvedIndices.size(); ++i) {
				if (i > 0) sb.append(", ");
				sb.append(m_resolvedIndices.get(i).intValue());
			}
			if (m_negated) {
				// NOT IS {a,b,c} means "not in that set": pass to RuleNE-equivalent.
				IRule asNe = new RuleNE(sb.toString());
				rg = asNe.conjunction(rule);
			} else {
				IRule asE = new RuleE(sb.toString());
				rg = asE.conjunction(rule);
			}
		} catch (InvalidExpression e) {
			e.printStackTrace();
		}
		return rg;
	}

	@Override
	public String getJSON() {
		// Emit symbolic names plus the classifier id so readers can resolve without guessing.
		StringBuilder sb = new StringBuilder();
		sb.append("'value' : '").append(getExpressionString()).append("', ");
		sb.append("'oper' : '").append(getOperator()).append("', ");
		sb.append("'").append(Definitions.classifierIdJSON).append("' : '").append(m_vocabulary.getId()).append("' ");
		return sb.toString();
	}

	@Override
	public String toText() {
		String prefix = m_negated ? "is not " : "is ";
		if (m_symbols.size() == 1) {
			return prefix + m_symbols.get(0);
		}
		return prefix + "one of " + String.join(" or ", m_symbols);
	}
}
