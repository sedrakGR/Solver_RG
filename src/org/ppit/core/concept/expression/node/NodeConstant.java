package org.ppit.core.concept.expression.node;

/**
 * 
 * @author Emil
 * @date 24.11.2011
 *
 */
public class NodeConstant extends Node{
	private int value;
	
	public NodeConstant(int value) {
		this.value = value;
	}
	
	@Override
	public NodeConstant clone () {
		// Doesn't need to copy.
		return this;
	}
	
	/**
	 * Return constant value
	 */
	@Override
	public int evaluate(){
		return value;
	}

	@Override
	public void extractDepenencies(DependencyContainer container){
	}
}
