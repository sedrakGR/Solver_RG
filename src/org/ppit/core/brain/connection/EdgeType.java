package org.ppit.core.brain.connection;

/**
 * @brief Enumeration for modeling Edge Types.
 * 
 * @author Karen
 */
public enum EdgeType {
	BE_EDGE ('b', "BeEdge"),
	HAVE_EDGE('h', "HaveEdge"),
	DO_EDGE('d', "DoEdge")
	;
	
	private char m_type;
	private String m_name = null;
	
	private EdgeType(char type, String name) {
		m_type = type;
		m_name = name;
	}
	
	public char getType () {
		return m_type;
	}
	
	public String getName() {
		return m_name;
	}
	
	public static EdgeType getEdgeTypeByType(char type) {
		switch (type) {
			case 'b':
				return BE_EDGE;
			case 'h' :
				return HAVE_EDGE;
			case 'd' :
				return DO_EDGE;
			}
		return null;
	}
}
