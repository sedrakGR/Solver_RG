package org.ppit.viz;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Set;

import org.ppit.core.brain.GA;
import org.ppit.core.brain.GAEdge;
import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptLibrary;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.ConceptType;
import org.ppit.util.exception.PPITException;

public class Skeleton {
	
	CognitionManager m_cognition ;	
	
	GA graphOfAbstracts ;	
	
	AbstractSet<String> skeleton ;
	
	ConceptLibrary m_library ;
	
	public Skeleton() {
		m_cognition = CognitionManager.getInstance();
		
		graphOfAbstracts = m_cognition.getGA();
		
		m_library = ConceptManager.getInstance().getLibraryForTest();
		
		skeleton = null;
	}
	
	/*
	* @return JSON witch contains names of skeleton members.
	*/
	public Set<String> getSkeleton (String name) {
		GANode node;		
		Concept concept	= m_library.getConcept(name);
		try {
			node = concept.getGANode();			
			skeleton = new HashSet<String>();
			generateSkeleton(node);
			
		} catch (PPITException e) {
			e.printStackTrace();
		}
	    return skeleton;
	}
	
	private void generateSkeleton(GANode node) {
		ConceptType type = node.getAbstract().getDetailedType();
		String name = node.getAbstract().getName();
		skeleton.add(node.getAbstract().getName());
		/*
		 * 1.	If the node is a nucleus then process only its Be connections, when the latter is source.
		 */
		if(type.equals(ConceptType.NUCLEUS)) {
			return;
		}
		if(type.equals(ConceptType.PRIMITIVE)) {
			/*
			 * be edges where node is a destination
			 */
			for(GAEdge beConn: node.getParents()) {
				generateSkeleton(beConn.getMaster());
			}
			return;
		}
		
		/*
		 * 2.	If the node is neither a virtual nor a usage then as a next layer of the skeleton select 
		 * 		only Have connections where the role of the node is a source. 
		 * 		This means, that none of Be, Do connections or Have connections, where the node is a destination, will be considered.
		 */
		if(!type.equals(ConceptType.VIRTUAL) && !type.equals(ConceptType.USAGE) ) {
			/*
			 * the node is source of have connection
			 */
			for(GAEdge haveConn: node.getElements()) {
				generateSkeleton(haveConn.getSlave());
			}
		}
		/*
		 * 3.	If the node is a virtual or a usage then only Be edges, where the node is a destination, 
		 * 		must be selected for further processing. For virtual nodes, this includes the set of all specifications of the node. 
		 * 		For the usage nodes, this is basically the base virtual node (it is being selected because of the reverse Be connection)
		 */
		else {
			/*
			 * be edges where node is a destination
			 */
			for(GAEdge beConn: node.getChildren()) {
				generateSkeleton(beConn.getSlave());
			}
		}
	}
}
