package org.ppit.core.learn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.util.Pair;


public class MappingManager
{
	ArrayList<ContainerEntry> m_lContainer = new ArrayList<ContainerEntry>();
	ArrayList<ContainerEntry> m_rContainer = new ArrayList<ContainerEntry>();
	
	ArrayList<BaseEntry> m_bContainer = new ArrayList<BaseEntry>();
	ArrayList<Integer> m_bIdMapping = new ArrayList<Integer>();
	
	ArrayList<ArrayList<PairedEntry>> m_peList = new ArrayList<ArrayList<PairedEntry>>();
	
	public MappingManager() {
		
	}
	
	public MappingManager clone() {
		MappingManager clone = new MappingManager();
		
		for(ContainerEntry ce: m_lContainer)
			clone.m_lContainer.add(ce.clone());
		for(ContainerEntry ce: m_rContainer)
			clone.m_rContainer.add(ce.clone());
		
		for(BaseEntry be: m_bContainer)
			clone.m_bContainer.add(be.clone());
		
		for(Integer id: m_bIdMapping)
			clone.m_bIdMapping.add(id);
		
		/*for(ArrayList<PairedEntry> peList: m_peList) {
			ArrayList<PairedEntry> cl = new ArrayList<PairedEntry>();
			
			for(PairedEntry pe: peList)
				cl.add(pe.clone());
			
			clone.m_peList.add(cl);
		}*/
		
		return clone; 
	}
	
	//creates mapping manager, initializing base, left, right containers
	public void initializeMappingManager(CompositeConcept left, CompositeConcept right) {
		createContainerEntryList(left, right);
		
		int index = 0;
		int ceIndex = 0;
		Concept lCopy;
		Concept rCopy;
		boolean contains;
		BaseEntry bEntry = new BaseEntry();
		
		/*iterating through containers entries
		 * checking does our concept already exists in base container
		 * if it is then add some ids to entries(base and container)
		 * else add base entry to the base container and some ids to entries
		 */
		for(ContainerEntry lEntry: m_lContainer) {
			lCopy = lEntry.getConcept();
			contains = false;
			while(true) {
				for(BaseEntry be: m_bContainer)
					if(be.getConcept() == lCopy) {
						contains = true;
						bEntry = be;
						bEntry.addLeftCE(ceIndex);
						break;
					}
				
				if(!contains) {
					bEntry = new BaseEntry(lCopy, index);
					bEntry.addLeftCE(ceIndex);
					m_bContainer.add(bEntry);
					lEntry.addBaseId(index);
					++index;
				}
				else
					lEntry.addBaseId(bEntry.getId());
					
				if(lCopy.getParent() == null)
					break;
				
				lCopy = lCopy.getParent();
			}
			++ceIndex;
		}
		
		ceIndex = 0;
		for(ContainerEntry rEntry: m_rContainer) {
			rCopy = rEntry.getConcept();
			contains = false;
			while(true) {
				for(BaseEntry be: m_bContainer)
					if(be.getConcept() == rCopy) {
						contains = true;
						bEntry = be;
						bEntry.addRightCE(ceIndex);
						break;
					}
				
				if(!contains) {
					bEntry = new BaseEntry(rCopy, index);
					bEntry.addRightCE(ceIndex);
					m_bContainer.add(bEntry);
					rEntry.addBaseId(index);
					++index;
				}
				else
					rEntry.addBaseId(bEntry.getId());
					
				if(rCopy.getParent() == null)
					break;
				
				rCopy = rCopy.getParent();
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
	
	//removes base entry from base container and manages entries id lists
	private void removeBE(Integer id) {
		int index = 0;
		
		for(BaseEntry be: m_bContainer) {
			if(be.getId() == id) {
				for(Integer lId: be.getLeftIdList())
					m_lContainer.get(lId).removeBaseId(id);
				
				for(Integer rId: be.getRightIdList())
					m_rContainer.get(rId).removeBaseId(id);
				
				m_bContainer.remove(index);
				break;
			}
			++index;
		}
	}
	
	//removes two(paired) container entries from given MM and manages entries id lists
	private void removePE(Integer left, Integer right) {
		for(Integer id: m_lContainer.get(left).getBaseIdList()) {
			for(BaseEntry be: m_bContainer) {
				if(be.getId() == id) {
					be.removeLeftCE(left);
				}
			}
		}
		
		for(Integer id: m_rContainer.get(right).getBaseIdList()) {
			for(BaseEntry be: m_bContainer) {
				if(be.getId() == id) {
					be.removeRightCE(right);
				}
			}
		}
		
		m_lContainer.get(left).removeBaseIds();
		m_rContainer.get(right).removeBaseIds();
	}
	
	//removes base entries which have not left or right references on and sets -1 its value in id mapping
	private void preprocess() {
		BaseEntry be;
		
		for(int index=0; index<m_bContainer.size(); ++index) {
			be = m_bContainer.get(index);
			if(be.getLeftIdList().isEmpty() || be.getRightIdList().isEmpty()) {
				removeBE(be.getId());
				--index;
			}
		}
	}
	
		
	public ArrayList<ArrayList<PairedEntry>> extract() {
		ArrayList<PairedEntry> peList = new ArrayList<PairedEntry>();
		ArrayList<ArrayList<PairedEntry>> list = new ArrayList<ArrayList<PairedEntry>>();
		extract_internal(peList, list);
		m_peList = list;
		return list;
	}
	
	private ArrayList<PairedEntry> get_clone(ArrayList<PairedEntry> peList) {
		ArrayList<PairedEntry> new_pe = new ArrayList<PairedEntry>();
		
		for(PairedEntry pe: peList) {
			new_pe.add(pe.clone());
		}
		return new_pe;
	}
	
	
	private void extract_internal(ArrayList<PairedEntry> peList, ArrayList<ArrayList<PairedEntry>> list) {
		preprocess();
		
		//delete just added list, cause that's not full list
		if(!m_bContainer.isEmpty() && !list.isEmpty())
			list.remove(list.size()-1);
		
		sort();
		
		for(Integer i: m_bIdMapping) {
			if(m_bContainer.get(i).getLeftIdList().size() == 1 && m_bContainer.get(i).getRightIdList().size() == 1) {
				peList.add(new PairedEntry(m_bContainer.get(i), m_lContainer.get(m_bContainer.get(i).getLeftIdList().get(0)), m_rContainer.get(m_bContainer.get(i).getRightIdList().get(0))));
				removePE(m_bContainer.get(i).getLeftIdList().get(0), m_bContainer.get(i).getRightIdList().get(0));
				removeBE(m_bContainer.get(i).getId());
				
				//when BE deleted bIdMapping must take changes
				for(int it=0; it!=m_bIdMapping.size(); ++it)
					if(m_bIdMapping.get(it) > i)
						m_bIdMapping.set(it, m_bIdMapping.get(it)-1);
			}
		}
		
		preprocess();
		sort();
		
		ArrayList<PairedEntry> n_peList = new ArrayList<PairedEntry>();
		if(m_bContainer.isEmpty())
			return;
		
		//every time we need to work with the first level of bContainer
		Integer index = m_bIdMapping.get(0);
		for(Integer lId: m_bContainer.get(index).getLeftIdList()) {
			for(Integer rId: m_bContainer.get(index).getRightIdList()) {
				n_peList = get_clone(peList);;
				n_peList.add(new PairedEntry(m_bContainer.get(index), m_lContainer.get(lId), m_rContainer.get(rId)));
				MappingManager m = this.clone();
				m.removePE(lId, rId);
				m.removeBE(m_bContainer.get(index).getId());
				list.add(n_peList);
				m.extract_internal(n_peList, list);
			}
		}
	}
	
	class CompareHelper implements Comparator<Pair<Integer, Integer>> {
		@Override
		public int compare(Pair<Integer, Integer> arg0, Pair<Integer, Integer> arg1) {
			if(arg0.first == arg1.first)
				return 0;
			if(arg0.first < arg1.first)
				return -1;
			return 1;
		}
	}
	
	void sort() {
		ArrayList<Pair<Integer, Integer>> counts = new ArrayList<Pair<Integer, Integer>> ();
		int index = 0;
		
		for(BaseEntry be: m_bContainer) {
			counts.add(new Pair<Integer, Integer>(be.RLreferenceSize(), index));//be.getId()
			++index;
		}
		
		Collections.sort(counts, new CompareHelper());
		
		// Re-integrate the sorted entries
		ArrayList<Integer> bIdMapping = new ArrayList<Integer>();
		
		for(Pair<Integer, Integer> entry: counts) {
			bIdMapping.add(entry.second);
		}
		m_bIdMapping = bIdMapping;
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
