package org.ppit.core.learn;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.rules.BaseRule;
import org.ppit.core.concept.rules.IRule;
import org.ppit.core.concept.rules.Rule;
import org.ppit.core.concept.rules.RuleGroup;
import org.ppit.util.Pair;
import org.ppit.util.exception.IncorrectDependency;
import org.ppit.util.exception.PPITException;

public class GeneralizeComposites implements GeneralizationBase
{
	MappingManager m_mappingManager = new MappingManager();
	String m_name = new String();
	RuleGroup m_leftDep = null;
	RuleGroup m_rightDep = null;
	
	public void initialize(Concept left, Concept right) {
		m_mappingManager.initializeMappingManager((CompositeConcept)left, (CompositeConcept)right);
		m_name = left.getName() + "_" + right.getName();
		try {
			CompositeConcept leftConcept = (CompositeConcept) left;
			m_leftDep = leftConcept.getInternalDependencies();
			CompositeConcept rightConcept = (CompositeConcept) right;
			m_rightDep = rightConcept.getInternalDependencies();
		} catch (IncorrectDependency e) {
			e.printStackTrace();
		}
	}
	
	public void generalize() {
		m_mappingManager.extract();
		GeneralizationBase.m_peList.addAll(m_mappingManager.m_peList);
	}
	
	public ArrayList<Concept> generateConcepts() {
		ConceptManager c_manager = ConceptManager.getInstance();
		ArrayList<Concept> conceptList = new ArrayList<Concept>();
		
		try {
			int name_i = 0;
			for(ArrayList<PairedEntry> peList: m_peList) {
				++name_i;
				String conceptJSON = "{'name' : '" + m_name + name_i + "', ";		
				conceptJSON += "'parent' : '',";
				conceptJSON += "'cr1' : '0',";
				conceptJSON += "'type' : 'c', ";
				conceptJSON += "'compositeConceptIndexAttrs' : [";		
				conceptJSON += "], 'compConceptAttrs' : [";
				
				boolean theFirst = true;
			
				for(PairedEntry pe: peList) {
				    Concept concept = pe.getBase().getConcept();
				    
					if (theFirst == false)
						conceptJSON += ", ";
					else
						theFirst = false;
					
					conceptJSON += "{"; 
					// if the concept is negated then 'negated' value is 1
					conceptJSON += "'negated' : '" + (concept.isNegated() ? '1' : '0') + "',";
					// We want to reuse the names of attributes.
					conceptJSON += concept.getDuplicatePure(null);
					conceptJSON += "}";
				}
				conceptJSON += "]}";
							
				JSONObject newConcept = new JSONObject(conceptJSON);
				CompositeConcept new_concept = c_manager.createCompositeConcept(newConcept);
				
				conceptList.add(new_concept);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (PPITException e) {
			e.printStackTrace();
		}
		
		return conceptList;
	}
	
	public ArrayList<Pair<String, ArrayList<String>>> generateDependencies(ArrayList<PairedEntry> peList) {
		//left and right concepts' attributes' dependencies
		ArrayList<Pair<String, ArrayList<String>>> leftCADeps = new ArrayList<Pair<String, ArrayList<String>>>();
		ArrayList<Pair<String, ArrayList<String>>> rightCADeps = new ArrayList<Pair<String, ArrayList<String>>>();
		
		for(BaseRule br: m_leftDep.getRules()) {
			Rule rl = (Rule) br;
			IRule r = rl.getRule();
//					r.replaceDependencies("a12x", "a21x");
//					for(String dep: r.getDependencies())
			leftCADeps.add(new Pair<String, ArrayList<String>>(br.getPrefix(), r.getDependencies()));
		}
		
		for(BaseRule br: m_rightDep.getRules()) {
			Rule rl = (Rule) br;
			IRule r = rl.getRule();
			rightCADeps.add(new Pair<String, ArrayList<String>>(br.getPrefix(), r.getDependencies()));
		}
		
		//let's make left(left+base) and right(right+base) pairs for each of peLists
		ArrayList<Pair<String, String>> leftPaires = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> rightPaires = new ArrayList<Pair<String, String>>();
		
		ArrayList<Pair<String, ArrayList<String>>> re_leftCADeps = new ArrayList<Pair<String, ArrayList<String>>>();
		ArrayList<Pair<String, ArrayList<String>>> re_rightCADeps = new ArrayList<Pair<String, ArrayList<String>>>();
		ArrayList<Pair<String, ArrayList<String>>> matchedCADeps = new ArrayList<Pair<String, ArrayList<String>>>();
		
		for(PairedEntry pe: peList) {
			leftPaires.add(new Pair<String, String>(pe.getLeft().getConcept().getRelativeName(), pe.getBase().getConcept().getName()));
			rightPaires.add(new Pair<String, String>(pe.getRight().getConcept().getRelativeName(), pe.getBase().getConcept().getName()));
		}
		
		//renaming attributes' names
		for(Pair<String, ArrayList<String>> lDep: leftCADeps) {
			for(Pair<String, String> left_base: leftPaires) {
				if(lDep.first.equals(left_base.first)) {
					ArrayList<String> re_attributeDepList = new ArrayList<String>();
					for(String attributeDeps: lDep.second) {
						int dotIndex = attributeDeps.indexOf(".");
						String name = attributeDeps.substring(0, dotIndex);
						String value = attributeDeps.substring(dotIndex);
						for(Pair<String, String> l_base: leftPaires) {
							if(name.equals(l_base.first)) {
								re_attributeDepList.add(l_base.second + value);
							}
						}
					}
					re_leftCADeps.add(new Pair<String, ArrayList<String>>(left_base.second, re_attributeDepList));
					break;
				}
			}
		}
		
		for(Pair<String, ArrayList<String>> rDep: rightCADeps) {
			for(Pair<String, String> right_base: rightPaires) {
				if(rDep.first.equals(right_base.first)) {
					ArrayList<String> re_attributeDepList = new ArrayList<String>();
					for(String attributeDeps: rDep.second) {
						int dotIndex = attributeDeps.indexOf(".");
						String name = attributeDeps.substring(0, dotIndex);
						String value = attributeDeps.substring(dotIndex);
						for(Pair<String, String> r_base: rightPaires) {
							if(name.equals(r_base.first)) {
								re_attributeDepList.add(r_base.second + value);
							}
						}
					}
					re_rightCADeps.add(new Pair<String, ArrayList<String>>(right_base.second, re_attributeDepList));
					break;
				}
			}
		}
		
		//finding matches
		for(Pair<String, ArrayList<String>> lDep: re_leftCADeps) {
			for(Pair<String, ArrayList<String>> rDep: re_rightCADeps) {
				if(lDep.equals(rDep)) {
					matchedCADeps.add(new Pair<String, ArrayList<String>>(lDep.first, lDep.second));
				}
			}
		}
		
		return matchedCADeps;
	}
	
	public void print_result() {
		int index = 0;
		System.out.println("here we go");
		
		for(ArrayList<PairedEntry> peList: m_peList) {
			if(peList.isEmpty())
				continue;
			
			System.out.print(index + ".");
			
			for(PairedEntry pe: peList)
				System.out.println("\t" + pe.getLeft().getConcept().getName() +"\t" +  pe.getBase().getConcept().getName() +"\t" +  pe.getRight().getConcept().getName());
			
			++index;
		}
	}
}
