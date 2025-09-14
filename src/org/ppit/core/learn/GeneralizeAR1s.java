package org.ppit.core.learn;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.util.exception.PPITException;

public class GeneralizeAR1s implements GeneralizationBase
{
	public GeneralizeAR1s() {
		
	}
	
	public GeneralizeAR1s clone() {
		GeneralizeAR1s clone = new GeneralizeAR1s();
		
		for(ContainerEntry ce: m_lContainer)
			GeneralizationBase.m_lContainer.add(ce.clone());
		for(ContainerEntry ce: m_rContainer)
			GeneralizationBase.m_rContainer.add(ce.clone());
		
		for(BaseEntry be: m_bContainer)
			GeneralizationBase.m_bContainer.add(be.clone());
		
		return clone;
	}
	
	public void initialize(Concept leftConcept, Concept rightConcept) {
		CompositeConcept left = (CompositeConcept)leftConcept;
		CompositeConcept right = (CompositeConcept)rightConcept;
		
		createContainerEntryList(left, right);
		
		int index = 0;
		int ceIndex = 0;
		boolean contains;
		BaseEntry bEntry = new BaseEntry();
		
		/*iterating through containers entries
		 * checking does our concept already exists in base container
		 * if it is then add some ids to entries(base and container)
		 * else add base entry to the base container and some ids to entries
		 */
		for(ContainerEntry lEntry: m_lContainer) {
			bEntry = new BaseEntry(lEntry.getConcept().getParent(), index);
			bEntry.addLeftCE(ceIndex);
			lEntry.addBaseId(bEntry.getId());
			m_bContainer.add(bEntry);
			++index;
			++ceIndex;
		}
		
		ceIndex = 0;
		for(ContainerEntry rEntry: m_rContainer) {
			contains = false;
			for(BaseEntry be: m_bContainer) {
				if(be.getConcept().getName().equals(rEntry.getConcept().getParent().getName())) {
					contains = true;
					bEntry = be;
					bEntry.addRightCE(ceIndex);
					rEntry.addBaseId(bEntry.getId());
					break;
				}
			}
			
			if(!contains) {
				bEntry = new BaseEntry(rEntry.getConcept().getParent(), index);
				bEntry.addRightCE(ceIndex);
				rEntry.addBaseId(bEntry.getId());
				m_bContainer.add(bEntry);
				++index;
			}
			++ceIndex;
		}
	}
	
	private void createContainerEntryList(CompositeConcept left, CompositeConcept right) {
		for(Concept lattr : left.getAttributes()) {
			m_lContainer.add(new ContainerEntry(lattr));
		}
		for(Concept rattr : right.getAttributes()) {
			m_rContainer.add(new ContainerEntry(rattr));
		}
	}
	
	public void generalize() {
		ArrayList<PairedEntry> peList = new ArrayList<PairedEntry>();
		
		for(BaseEntry be: m_bContainer) {
			if(be.getLeftIdList().isEmpty() || be.getRightIdList().isEmpty())
				continue;
			else
				peList.add(new PairedEntry(be, m_lContainer.get(be.getLeftIdList().get(0)), m_rContainer.get(be.getRightIdList().get(0))));
		}
		
		m_peList.add(peList);
	}
	
	public Concept generateConcepts() {
		String objectString = new String("{\"cr1\":\"1\",\"compositeConceptIndexAttrs\":[],\"name\":\"\",\"parent\":\"\",\"compConceptAttrs\":[]}");
		ConceptManager c_manager = ConceptManager.getInstance();
		CompositeConcept concept = null;
		try {
			//will work one time, cause m_peList contains an element
			for(ArrayList<PairedEntry> peList: m_peList) {
				JSONObject object = new JSONObject(objectString);
				concept = c_manager.createCompositeConcept(object);
				
				for(PairedEntry pe: peList)
					concept.addAttribute(pe.getBase().getConcept().getName(), pe.getBase().getConcept());
				//give some name to the concept
				//concept.setName("shit");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (PPITException e) {
			e.printStackTrace();
		}
		
		return concept;
	}
	
	public void show() {
		int index = 0;
		
		System.out.println("Left Container");
		for(ContainerEntry ce: m_lContainer) {
			System.out.println(index + ". " + ce.getConcept().getName() + "\t" + ce.getBaseIdList());
			++index;
		}
		System.out.println();
		
		index = 0;
		System.out.println("Right Container");
		for(ContainerEntry ce: m_rContainer) {
			System.out.println(index + ". " + ce.getConcept().getName() + "\t" + ce.getBaseIdList());
			++index;
		}
		System.out.println();
		
		index = 0;
		//BaseEntry be = new BaseEntry();
		System.out.println("Base Container");
		for(BaseEntry be: m_bContainer) {
			for(Integer i: be.getLeftIdList())
				System.out.print(m_lContainer.get(i).getConcept().getName() + "\t");
			System.out.print("[" + be.getId() + " Name " + be.getConcept().getName() + "]\t");
			for(Integer i: be.getRightIdList())
				System.out.print(m_rContainer.get(i).getConcept().getName() + "\t");
			System.out.println();
		}
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
