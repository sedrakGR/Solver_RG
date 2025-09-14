package org.ppit.core.concept.expression.node;

import org.ppit.util.exception.UnsolvedDependency;

/**
 * 
 * @author Emil
 * @date 24.11.2011
 *
 */
public abstract class Node implements Cloneable {
	public Node() {		
	}
	
	public abstract Node clone();
	public abstract int evaluate() throws UnsolvedDependency;
	public abstract void extractDepenencies(DependencyContainer container);
}
