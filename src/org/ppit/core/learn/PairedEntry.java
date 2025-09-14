package org.ppit.core.learn;

class PairedEntry
{
	private BaseEntry m_base;
	private ContainerEntry m_left;
	private ContainerEntry m_right;
	
	public PairedEntry(BaseEntry be, ContainerEntry l, ContainerEntry r) {
		m_base = be;
		m_left = l;
		m_right = r;
	}
	
	public PairedEntry clone() {
		PairedEntry clone = new PairedEntry(m_base, m_left, m_right);
		
		return clone;
	}
	
	public BaseEntry getBase() {
		return m_base;
	}
	
	public ContainerEntry getLeft() {
		return m_left;
	}
	
	public ContainerEntry getRight() {
		return m_right;
	}
	
	public void print() {
		System.out.println(m_left.getConcept().getName() + "\t[" + m_base.getConcept().getName() + "]\t" + m_right.getConcept().getName());
	}
}
