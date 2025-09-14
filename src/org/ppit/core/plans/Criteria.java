package org.ppit.core.plans;

import java.util.ArrayList;

import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.expression.Expression;
import org.ppit.util.exception.InvalidExpression;
import org.ppit.util.exception.UnsolvedDependency;

public class Criteria {
	// the expression defining criteria
	Expression m_expression = null;
	
	CriteriaType m_type;
	
	public Criteria(CriteriaType type, String expression) {
		m_type = type;
		try {
			m_expression = new Expression(expression);
		} catch (InvalidExpression e) {
			e.printStackTrace();
		}
	}
	
	public CriteriaType getCriteriaType() {
		return m_type;
	}
	
	public String getExpressionString() {
		return m_expression.getExprString();
	}	
	
	/**
	 * @return returns the evaluated value for the given situation
	 */
	public int evaluate(AbstractInstance instance) {
		ArrayList<String> dependencies = m_expression.getDependencies();
		for (String dependency : dependencies) {
			InstanceBase attr = instance.getInstance(dependency);
			if (attr == null) {
				//attempt to access without first (first may be the postConditionName)
				attr = instance.getInstance(dependency.substring(dependency.indexOf('.') + 1));
			}
			if (attr != null && attr instanceof NucleusInstance) {
				m_expression.setDependency(dependency, (NucleusInstance)attr);
			} else {
				// could not complete dependencies thus cannot be evaluated
				return 0;
			}
		} 
		int evaluationValue = 0;
		try {
			evaluationValue = m_expression.evaluate();
		} catch (UnsolvedDependency e) {
			System.out.println("a dependency is unsolved in expression");
			e.printStackTrace();
			return 0;
		}
		if (m_type == CriteriaType.MIN) {
			//normalize to get the max value always
			return 0 - evaluationValue;
		}
		return evaluationValue;
	}
	
	//{"criteria" : "expression1", "prioroity" : "1", "type" : "MIN"}
	// priority is outside the expression
	public String getJSON() {
		String criteriaJSON = "'criteria' : '" + m_expression.getExprString() + "' , ";
		criteriaJSON += "'type' : '" + m_type.getText() + "'";
		return criteriaJSON;
	}
}
