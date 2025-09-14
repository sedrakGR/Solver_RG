package org.ppit.core.brain.instance.nucleus;

import java.util.Collection;
import java.util.HashMap;

import org.ppit.core.concept.primitive.PrimitiveType;
import org.ppit.util.exception.BrokenCR1;

/**
 * @author Khachatryan Karen S.
 * @brief contains the ID of the PElement group in the situation 
 * as well as the list of PElements in that group. 
 */
public class IdGroup {
	
	private static int emptyIDCounter = Integer.MAX_VALUE; 
	
	/**
	 * the internal id of the primitive elements in the situation.
	 */
	private int m_id = 0;
	
	/**
	 * the elements in the ID group.
	 */
	private HashMap<PrimitiveType, NucleusInstance> m_pElements = null;
	
	/**
	 * @brief Constructor
	 * @param id - the of the group.  
	 */
	public IdGroup(int id) {
		m_id = id;
		m_pElements = new HashMap<PrimitiveType, NucleusInstance>();
	}
	
	/**
	 * @param type - the type of the element
	 * @param pElement - the primitive element
	 * @throws BrokenCR1
	 */
	public void addPElement(PrimitiveType type, NucleusInstance pElement) throws BrokenCR1{
		if(m_pElements.get(type) != null) {
			throw new BrokenCR1("Error: Trying to set more than one instance for " + 
					type.getType() + " primitive type");
		}
		m_pElements.put(type, pElement);
	}
	
	/**
	 * @brief adds PElement by force so if there is an instance already with the given type, it is being overridden
	 *@param type - the type of the element
	 * @param pElement - the primitive element
	 */
	
	public void forceAddPElement(PrimitiveType type, NucleusInstance pElement) {
		m_pElements.put(type, pElement);
	}

	/**
	 * @param type the type of the element
	 * @return the element which corresponds to the given type
	 */
	public NucleusInstance getElement(PrimitiveType type) {
		return m_pElements.get(type);
	}
	
	public int getIdGroup() {
		return m_id;
	}
	
	public Collection<NucleusInstance> getElements() {
		return m_pElements.values();
	}
	
	public static IdGroup getEmptyIDGroup() {
		return new IdGroup(emptyIDCounter--);
	}
}
