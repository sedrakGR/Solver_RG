package org.ppit.core.concept.primitive;

/**
 * 
 * @author Karen Khachatryan S.
 * @detailed The type of Primitive concept. These are represeting the  
 * nucleus type of primitive attributes and are used in Situations.
 *
 */

public class PrimitiveType {

	/**
	 * @brief The name of the type which serves as an identifier.
	 */
	private String m_type = null;
	
	/**
	 * @brief Constructor
	 * @detailed initializes the identifier
	 */
	public PrimitiveType(String type){
		m_type = type;
	}

	/**
	 * @brief getType() returns the type identifier. 
	 * @return Returns type identifier 
	 */
	public String getType(){
		return m_type;
	}
	
	/**
	 * @brief getName() returns the name of the NUCLEUS concept.
	 * @detailed for now the type matches with the name of the NUCLEUS concept,
	 * therefore, it we do return the same attribute, however, this function shall
	 * be used in order to find the exact NUCLEUS concepts, when they are requested.
	 * @return the name of the NUCLEUS concept.
	 */
	public String getName(){
		return m_type;
	}
	
	/**
	 * @brief Constructs and returns the JSON representation of the header.
	 */
	public String getHeaderJson(){
		return "'type' : 'n', 'parent' : '', 'name' : '" + this.getName() + "'";
	}
}
