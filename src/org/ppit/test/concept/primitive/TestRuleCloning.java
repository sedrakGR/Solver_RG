package org.ppit.test.concept.primitive;

import static org.junit.Assert.*;

import org.junit.Test;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.brain.instance.ar1.AR1Instance;
import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.primitive.PrimitiveType;
import org.ppit.core.concept.rules.*;
import org.ppit.test.TestHelper;
import org.ppit.util.exception.PPITException;
import org.ppit.util.exception.UnsolvedDependency;

public class TestRuleCloning {

	TestHelper m_testHelper = new TestHelper();
	
	@Test
	public void testIRuleCloning() {
		try {
			IRule rule1 = new RuleE("x.v");
			IRule rule2 = new RuleE("y.v, 55");
			
			IRule rule2_clone = rule2.clone();
			
			NucleusInstance ni_1 = m_testHelper.getNucleusInstance();
			NucleusInstance ni_2 = m_testHelper.getNucleusInstance();;
			
			ni_1.setValue(18);
			rule2.setDependency("y.v", ni_1);
			
			rule1.setDependency("x.v", ni_2);
			ni_2.setValue(20);
			
			IRule rule1_clone = rule1.clone(); 
			
			assertEquals(rule2.check(18), true);
			boolean pending = false;
			try{
				assertEquals(rule2_clone.check(18), false);
			} catch (UnsolvedDependency e) {
				pending = true;
			}
			assertEquals(true, pending);
			
			assertEquals(rule1.check(19), false);
			assertEquals(rule1_clone.check(20), true);
		} catch (PPITException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEmptyRuleCloning() {
		try {
			IRule rule1 = new RuleE("x.v");
			
			Rule r1 = new Rule(rule1, "p1.x");
			Rule r1_clone = r1.clone();
			
			NucleusInstance x1_1 = m_testHelper.getNucleusInstance();
			NucleusInstance p1_1 = m_testHelper.getNucleusInstance();
						
			x1_1.setValue(18);
			assertEquals(Rule.R_L_PENDING, r1.check());
			
			r1.setLeft(p1_1);
			assertEquals(Rule.R_R_PENDING, r1.check());
			
			r1.setDependency("x.v", x1_1);
			
			p1_1.setValue(19);
			assertEquals(Rule.R_FAIL, r1.check());
			
			p1_1.setValue(18);
			assertEquals( Rule.R_OK, r1.check());

			assertEquals(Rule.R_L_PENDING, r1_clone.check());
			
			NucleusInstance x1_2 = m_testHelper.getNucleusInstance();
			NucleusInstance p1_2 = m_testHelper.getNucleusInstance();
						
			p1_2.setValue(22);

			r1_clone.setLeft(p1_2);
			assertEquals(Rule.R_R_PENDING, r1_clone.check());
			
			x1_2.setValue(23);
			
			r1_clone.setDependency("x.v", x1_2);
			assertEquals(Rule.R_FAIL, r1_clone.check());
			
			x1_2.setValue(22);
			assertEquals( Rule.R_OK, r1_clone.check());
		
		} catch (PPITException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFilledRuleCloning() {
		try {
			IRule rule1 = new RuleE("x.v");
			
			Rule r1 = new Rule(rule1, "p1.x");
			
			NucleusInstance x1_1 = m_testHelper.getNucleusInstance();
			NucleusInstance p1_1 = m_testHelper.getNucleusInstance();
						
			x1_1.setValue(18);
			p1_1.setValue(18);
			
			r1.setLeft(p1_1);
			r1.setDependency("x.v", x1_1);
			
			assertEquals( Rule.R_OK, r1.check());
			
			Rule r1_clone = r1.clone();
			assertEquals( Rule.R_OK, r1_clone.check());
			
			NucleusInstance x1_2 = m_testHelper.getNucleusInstance();
			NucleusInstance p1_2 = m_testHelper.getNucleusInstance();
						
			p1_2.setValue(22);
			x1_2.setValue(22);

			r1_clone.setLeft(p1_2);
			assertEquals( Rule.R_OK, r1.check());
			assertEquals( Rule.R_FAIL, r1_clone.check());
			
			r1.setDependency("x.v", x1_2);
			assertEquals( Rule.R_FAIL, r1.check());
			assertEquals( Rule.R_FAIL, r1_clone.check());
			
			r1.setLeft(p1_2);
			assertEquals( Rule.R_OK, r1.check());
			assertEquals( Rule.R_FAIL, r1_clone.check());
			
			r1_clone.setDependency("x.v", x1_2);
			assertEquals( Rule.R_OK, r1.check());
			assertEquals( Rule.R_OK, r1_clone.check());
			
		} catch (PPITException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRuleGroupCloning(){
		try {
			IRule rule1 = new RuleE("p2.x");
			IRule rule2 = new RuleE("p2.y.v");
			
			Rule r1 = new Rule(rule1, "p1.x");
			Rule r2 = new Rule(rule2, "p1.y");
			
			RuleGroup rg = new RuleGroup();
			rg.addRule(r1);
			rg.addRule(r2);
			
			PrimitiveType x = new PrimitiveType("x");
			PrimitiveType y = new PrimitiveType("y");
			
			IdGroup idg1 = new IdGroup(1);
			IdGroup idg2 = new IdGroup(2);
			IdGroup idg3 = new IdGroup(3);
			
			NucleusInstance x1_1 = new NucleusInstance(11, x, idg1, null);
			NucleusInstance y1_1 = new NucleusInstance(12, y, idg1, null);
			
			NucleusInstance x1_3 = new NucleusInstance(11, x, idg3, null);
			NucleusInstance y1_3 = new NucleusInstance(12, y, idg3, null);
			
			NucleusInstance p1_x1 = new NucleusInstance(11, x, idg2, null);
			NucleusInstance p1_y1 = new NucleusInstance(12, y, idg2, null);
						
			AR1Instance p2_1 = new AR1Instance(null, idg1, 2);
			assertEquals(false, p2_1.activateElement("x", x1_1));
			assertEquals(true, p2_1.activateElement("y", y1_1));

			AR1Instance p2_2 = new AR1Instance(null, idg3, 2);
			assertEquals(false, p2_2.activateElement("x", x1_3));
			assertEquals(true, p2_2.activateElement("y", y1_3));

			AR1Instance p1_1 = new AR1Instance(null, idg2, 2);
			assertEquals(false, p1_1.activateElement("x", p1_x1));
			assertEquals(true, p1_1.activateElement("y", p1_y1));
			
			// Testing RuleGroup cloning. 
			AbstractInstance ab_1 = new AbstractInstance(null, rg.clone(), 2);
			assertEquals(AbstractInstance.A_R_PENDING, ab_1.activateElement("p1", p1_1));
			
			// If Rule Group cloning fails, here we will wait for "A_A_PENDING"
			// Which means that rules are satisfied, however, as we know
			//   "p1" is not set yet for ab_2 instance.
			AbstractInstance ab_2 = new AbstractInstance(null, rg.clone(), 2);
			assertEquals(AbstractInstance.A_R_PENDING, ab_2.activateElement("p2", p2_1));

			assertEquals(Rule.R_PENDING, rg.clone().setAndCheckDependency("ab", ab_2));
			
			assertEquals(AbstractInstance.A_FIRE, ab_2.activateElement("p1", p1_1));
			assertEquals(Rule.R_OK, rg.clone().setAndCheckDependency("ab", ab_2));

			// Testing Abstract Instance cloning. 
			assertEquals(Rule.R_PENDING, rg.clone().setAndCheckDependency("ab", ab_1));
			
			AbstractInstance ab_3 = ab_1.clone();
			assertEquals(AbstractInstance.A_FIRE, ab_3.activateElement("p2", p2_2));

			assertEquals(Rule.R_OK, rg.clone().setAndCheckDependency("ab", ab_3));
			assertEquals(Rule.R_PENDING, rg.clone().setAndCheckDependency("ab", ab_1));
			
		} catch (PPITException e) {
			e.printStackTrace();
		} 
	}
	
	

}
