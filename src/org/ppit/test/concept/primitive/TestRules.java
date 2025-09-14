package org.ppit.test.concept.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.rules.*;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.PPITException;

public class TestRules {
	
	TestHelper m_testHelper = new TestHelper();
	
	@Test
	public void testRulesEqualNotEqual() {
		try {
			IRule rule1 = new RuleE("12");
			IRule rule2 = new RuleE("a.b, 55, 33 - a.b");
			IRule rule3 = new RuleNE("55, 57, 100");
			
			assertEquals("In Rule EQUALS TO: ", rule1.getExpressionString(), "12"); 
			assertEquals("In Rule EQUALS TO: ", rule2.getExpressionString(), "a.b, 55, 33 - a.b");
			
			assertEquals("Checking ruleE's value equals to", rule1.check(12), true);
			assertEquals("Checking ruleE's value equals to", rule1.check(25), false);
			
			assertEquals("Checking ruleNE's value not equals to", rule3.check(55), false);
			assertEquals("Checking ruleNE's value not equals to", rule3.check(57), false);
			assertEquals("Checking ruleNE's ! (value not equals to)", rule3.check(56), true);
			assertEquals("Checking ruleNE's ! (value not equals to)", rule3.check(3), true);
			
			NucleusInstance ni = m_testHelper.getNucleusInstance();
			ni.setValue(18);
			rule2.setDependency("a.b", ni);
			
			assertEquals("Checking ruleE's value be equals to", rule2.check(15), true);
			
			// Test cloning.
			IRule clone = rule2.clone();
			NucleusInstance ni_1 = m_testHelper.getNucleusInstance();
			ni_1.setValue(10);
			clone.setDependency("a.b", ni_1);
			assertEquals("Checking ruleE's value (after clone) be equals to", rule2.check(15), true);
			assertEquals("Checking ruleE clone's value be equals to", clone.check(10), true);
			assertEquals("Checking ruleE clone's value not be equals to", clone.check(15), false);
			assertEquals("Checking ruleE clone's value be equals to", clone.check(55), true);
			
		} catch (PPITException e) {
			e.printStackTrace();
		} 
	}

	@Test
	public void testRulesGE() {
		try {
			NucleusInstance conceptInstance = m_testHelper.getNucleusInstance();
			
			IRule rule = new RuleGE("10 + p.x");
			rule.setDependency("p.x", conceptInstance);
			conceptInstance.setValue(111);
			
			assertEquals("Checking ruleGE's value greater than or equals to", rule.check(121), true);
			assertEquals("Checking ruleGE's value greater than or equals to", rule.check(150), true);
			assertEquals("Checking ruleGE's value ! (greater than or equals to)", rule.check(120), false);
			
			// Test cloning.
			IRule clone = rule.clone();
			NucleusInstance ni_1 = m_testHelper.getNucleusInstance();
			ni_1.setValue(12);
			clone.setDependency("p.x", ni_1);
			assertEquals("Checking ruleGE's value (after cloning) greater than or equals to", rule.check(150), true);
			assertEquals("Checking ruleGE's value (after cloning) ! (greater than or equals to)", rule.check(120), false);
			
			assertEquals("Checking ruleGE clone's value greater than or equals to)", clone.check(30), true);
			assertEquals("Checking ruleGE clone's value ! (greater than or equals to)", rule.check(20), false);
		} catch (UnknownError e) {
			fail("Failed to set the value for primitive concept maybe");
		} catch (PPITException e) {
			e.printStackTrace();
			System.out.println("Exception in creation of the rule");
		}
	}
	
	@Test
	public void testRulesG() {
		try {
			NucleusInstance conceptInstance = m_testHelper.getNucleusInstance();
			
			IRule rule = new RuleG("10 + p.x");
			rule.setDependency("p.x", conceptInstance);
			conceptInstance.setValue(111);
			
			assertEquals("Checking ruleGE's value greater than ", rule.check(150), true);
			assertEquals("Checking ruleGE's value ! (greater than)", rule.check(121), false);
			assertEquals("Checking ruleGE's value ! (greater than)", rule.check(120), false);
		} catch (PPITException e) {
			e.printStackTrace();
			System.out.println("Exception in creation of the rule");
		} 
	}
	
	@Test
	public void testRulesL() {
		try {
			NucleusInstance conceptInstance = m_testHelper.getNucleusInstance();
			
			IRule rule = new RuleL("10 + p.x");
			rule.setDependency("p.x", conceptInstance);
			conceptInstance.setValue(111);
			assertEquals("Checking ruleGE's value ! (less than )", rule.check(121), false);
			assertEquals("Checking ruleGE's value ! (less than )", rule.check(150), false);
			assertEquals("Checking ruleGE's value less than", rule.check(120), true);
			assertEquals("Checking ruleGE's value less than", rule.check(15), true);
		} catch (PPITException e) {
			e.printStackTrace();
			System.out.println("Exception in creation of the rule");
		} 
	}
	
	@Test
	public void testRulesLE() {
		try {
			NucleusInstance conceptInstance = m_testHelper.getNucleusInstance();
			
			IRule rule = new RuleLE("10 + p.x");
			rule.setDependency("p.x", conceptInstance);
			conceptInstance.setValue(111);
			assertEquals("Checking ruleGE's value less than or equals to", rule.check(121), true);
			assertEquals("Checking ruleGE's value ! (less than or equals to)", rule.check(150), false);
			assertEquals("Checking ruleGE's value less than or equals to", rule.check(120), true);
			assertEquals("Checking ruleGE's value less than or equals to", rule.check(15), true);
		} catch (PPITException e) {
			e.printStackTrace();
			System.out.println("Exception in creation of the rule");
		} 
	}
	
	@Test
	public void testRulesIn() {
		try {
			NucleusInstance conceptInstance = m_testHelper.getNucleusInstance();
			
			IRule rule = new RuleIn("10 + p.x, 160");
			rule.setDependency("p.x", conceptInstance);
			conceptInstance.setValue(111);
			assertEquals("Checking ruleGE's value IN", rule.check(121), true); // the upper and lower bounds are also allowed values for this rule
			assertEquals("Checking ruleGE's value IN", rule.check(150), true);
			assertEquals("Checking ruleGE's value IN", rule.check(160), true);
			assertEquals("Checking ruleGE's value ! IN", rule.check(120), false);
			assertEquals("Checking ruleGE's value ! IN", rule.check(161), false);
			assertEquals("Checking ruleGE's value ! IN", rule.check(15), false);
		} catch (PPITException e) {
			e.printStackTrace();
			System.out.println("Exception in creation of the rule");
		} 
	}
}
