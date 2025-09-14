package org.ppit.core.brain;

import java.util.HashSet;

import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.gaNode.abstractNode.GAAbstract;
import org.ppit.core.brain.gaNode.action.GAAction;
import org.ppit.core.brain.gaNode.ar1.GAAR1;
import org.ppit.core.brain.gaNode.listener.WorkingMemoryListener;
import org.ppit.core.brain.gaNode.nucleus.GANucleus;
import org.ppit.core.brain.gaNode.set.GASet;
import org.ppit.core.concept.*;
import org.ppit.core.concept.action.*;
import org.ppit.core.concept.primitive.*;
import org.ppit.core.concept.set.SetConcept;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.graphHelper.IdentityChecker;
import org.ppit.core.concept.rules.RuleGroup;
import org.ppit.core.concept.rules.RuleWildcard;
import org.ppit.util.exception.*;

public class GACreator {
	HashSet<String> m_insertedPrimaries = new HashSet<String>();
	
	// Will be initialized on the first call.
	private ConceptManager m_cmanager = null;
	
	private GA m_ga = null;
	private WorkingMemoryListener m_wm = null;
	
	public GACreator(GA ga) {
		m_ga = ga;
	}
	/**
	 * Registers Working Memory Listener to all Primary abstracts added after registration.
	 * @param wmListener - the WM Listener instance
	 */
	public void registerWMListener(WorkingMemoryListener wmListener) {
		m_wm = wmListener;
	}
	
	public void reset() {
		m_ga.reset(); 
		m_insertedPrimaries.clear();
		if(m_wm != null) {
			m_wm.reset();
		}
	}
	

	/**
	 * @param library
	 */
	public void addConcepts(ConceptLibrary library) throws PPITException {
		// Only insert the Primary (ZeroLevel) abstracts, the rest will be inserted automatically.

		for (PrimitiveType type : library.getListOfTypes()) {
			insertPrimaryAbstract(library.getPrimitiveConcept(type.getName()));
		}
		for (PrimitiveConcept concept : library.getListOfPrimitives()) {
			if(concept.isZeroLevel()) {
				insertPrimaryAbstract(concept);
			}
		}
		for (CompositeConcept concept : library.getListOfComposites()) {
			if(concept.isZeroLevel()) {
				insertPrimaryAbstract(concept);
			}
		}
		for (SetConcept concept : library.getListOfSets()) {
			if(concept.isZeroLevel()) {
				insertPrimaryAbstract(concept);
			}
		}
	}
	

	public void addActions(ActionLibrary library) throws PPITException {
		for (Action action : library.getListOfActions()) {
			insertPrimaryAbstract(action);
		}
	}

	public void removePrimaryAbstract(AbstractBase source) throws PPITException {
		// TODO implement me!
	}
	
	/**
	 * Abstract is considered Primary, if it is not an attribute in any other abstract. 
	 * All Primary abstracts are being registered to Working Memory List (if there is any). 
	 * @param source - the abstract
	 * @return - the GA Node
	 * @throws PPITException
	 */
	public GANode insertPrimaryAbstract(AbstractBase source) throws PPITException {
		if (source.getName().equalsIgnoreCase("fieldundercheckofking4")) {
			int i = 19;
			int j = i;
		}
		if(exists(source)) {
			return source.getGANode();
		}
		boolean isVirtualSlot = false;
		GANode node = insertAbstract(source, isVirtualSlot);
		if(m_wm != null) {
			m_wm.registerTo(source.getName(), node);
		}
		return node;
	}
	
	/**
	 * This is helper method
	 * 		inserts the abstract treating it as a parent into a GA.
	 * The interesting trick here is that if the parent is not inserted there directly the parent, 
	 * 		but rather the root holder of the parent is inserted.
	 * For example abstract [X --parent-> K.T.P], in this case the "K" will be inserted, 
	 * 		which will lead to insertion of K.T and the latter to K.T.P
	 * @throws PPITException 
	 */
	private void insertParent(Concept parent) throws PPITException {
		String parentRootName = parent.getRoot();
		if(m_cmanager == null) {
			m_cmanager = ConceptManager.getInstance();
		}
		Concept concept = m_cmanager.getConcept(parentRootName);
		if(concept == null) {
			Action action = m_cmanager.getAction(parentRootName);
			if(action == null) {
				throw new PPITException("Library loading error. There is no Concept/Action with the name: " + parentRootName);
			}
			insertPrimaryAbstract(action);
		} else {
			insertPrimaryAbstract(concept);
		}
	}

	
	private GANode insertAbstract(AbstractBase source, boolean isVirtualSlot) throws PPITException
	{
		GANode node = insertAbstractInternal(source, isVirtualSlot);
		if(node != null && m_wm != null) {
			m_wm.registerNegation(source.getName(), node);
		}
		source.setGANode(node);
		return node;
	}
	
	private GANode insertAbstractInternal(AbstractBase source, boolean isVirtualSlot) throws PPITException
	{
		if (source.getName().equals("Check") || source.getName().equals("test") || source.getName().equals("test.check")) {
			System.out.println("creatingCheck");
		}
		GANode node = null;
		if (source.CONCEPT_TYPE == ConceptType.ACTION) {
			node = new GAAction(source);
			
			// The Precondition of an Action is a Primary abstract!
			node.createDoConnection(insertPrimaryAbstract(((Action)source).getPreConditionConcept()));
			return node;
		} else {
			Concept parent = ((Concept) source).getParent();
			if (parent == null) {

				// The abstract which does not have a parent, can't be a virtual
				// slot,
				// in other words, can't be an attribute of the virtual
				// abstract,
				// as because all attributes have parents.
				if (isVirtualSlot != false) {
					throw new GAException(
							"Abstract which does not have a parent can not be a virtual slot!");
				}

				switch (source.CONCEPT_TYPE) {
				case COMPOSITE: {
					CompositeConcept concept = (CompositeConcept) source;
					boolean isAbstract = concept.isAbstract();
					boolean isCR1 = concept.isCR1();
					boolean isNegated = concept.isNegated();
					if (isCR1) {
						node = new GAAR1(source, isAbstract, isNegated);

					} else {
						// The internal Rules shall be set regardless the
						// abstract is virtual or not
						// If it is not virtual then this set of rules are
						// checked when an attribute is
						// activated, (incrementally)
						// Otherwise they are checked when a child is activated
						// (applying post-filtering)
						// the whole set of rules is initialized at once in this
						// case (at once).
						node = new GAAbstract(source, isAbstract, isNegated,
								concept.getInternalDependencies());
					}
					for (Concept attribute : concept.getAttributes()) {
						GANode attr = insertAbstract(attribute, isAbstract);
						if (isAbstract == false) {
							// this is needed only if it's not CR1, because if
							// it's cr1, the same is happening automatically
							if (!isCR1
									&& attribute.CONCEPT_TYPE == ConceptType.PRIMITIVE) {
								// suppose it's source is the attribute abstract
								GAAR1 ar1 = new GAAR1(attribute, isAbstract, isNegated);
								ar1.createHaveConnection(
										attribute.getRelativeName(), attr);
								node.createHaveConnection(
										attribute.getRelativeName(), ar1);
							} else {
								node.createHaveConnection(
										attribute.getRelativeName(), attr);
							}
						}
					}
				}
					break;
				case SET: {
					node = new GASet(source);
					SetConcept concept = (SetConcept) source;
					node.createHaveConnection(concept.getRelativeName(),
							insertAbstract(concept.getElement(), isVirtualSlot));

					((GASet) node).setRule(concept.getElementCount());
				}
					break;
				case PRIMITIVE: {
					// The nucleus which has no parent, can not be dependent,
					// and cannot be abstract,
					// if you have such cases, please connect with Support team
					// (namely Sedrak)
					PrimitiveConcept concept = (PrimitiveConcept) source;
					node = new GANucleus(concept);
					m_ga.addRoot(concept.getType(), (GANucleus) node);
				}
					break;
				default:
					throw new GAException(
							"Trying to insert an invalid type of abstract");
				}
				return node;
			} else {

				// Checks, if the Parent is not inserted into the Graph,
				// inserts!
				insertParent(parent);

				switch (source.CONCEPT_TYPE) {
				case COMPOSITE: {
					CompositeConcept concept = (CompositeConcept) source;
					boolean identical = IdentityChecker.check(
							concept.getParent(), concept);
					if (identical) {
						return parent.getGANode();
					}

					boolean isAbstract = concept.isAbstract();
					boolean isUsage = concept.isUsage();
					boolean isNegated = concept.isNegated();

					if (isVirtualSlot && !isUsage) {
//						if (isUsage) {
//							return null;
//						}

						// If abstract is a virtual slot, then there is no need
						// to create dummy
						// intermediate GACR1 or GAAbstract nodes,
						// we rather iterate over the attributes until nucleus
						// ones and create
						// only them.
						for (Concept attribute : concept.getAttributes()) {
							insertAbstract(attribute, true);
						}
						return node;
					}

					// Not a VirtualSlot! It is either Virtual, Usage,
					// Specification or Normal abstract
					//
					boolean isCR1 = concept.isCR1();
					if (isCR1) {
						node = new GAAR1(source, isAbstract || isUsage, isNegated);

						if (concept.isUsage()) {
							parent.getGANode().createBeConnection(node);
						} else {
							node.createBeConnection(parent.getGANode());
						}
					} else {
						// See the comments above. In addition nothing is
						// changed when the node is "Usage"
						//
						if (concept.isUsage()) {
							RuleGroup ruleGroup = concept
									.getInternalDependencies();
							ruleGroup.addRuleGroup(concept
									.getChangedEIndependentRules());
							node = new GAAbstract(source,
									isAbstract || isUsage, isNegated, ruleGroup);
							parent.getGANode().createBeConnection(node);
						} else {
							node = new GAAbstract(source,
									isAbstract || isUsage, isNegated,
									concept.getInternalDependencies());
							node.createBeConnection(parent.getGANode());
						}
					}
					// For the usage node there is no need to create attributes
					// at all, even nucleus ones!
					if (isUsage == false) {
						for (Concept attribute : concept.getAttributes()) {
							// For abstract
							GANode attr = insertAbstract(attribute, isAbstract);
							if (isAbstract == false) {
								// this is needed only if it's not CR1, because
								// if it's cr1, the same is happening
								// automatically
								if (!isCR1
										&& attribute.CONCEPT_TYPE == ConceptType.PRIMITIVE) {
									// suppose it's source is the attribute
									// abstract
									GAAR1 ar1 = new GAAR1(attribute, isAbstract, isNegated);
									ar1.createHaveConnection(
											attribute.getRelativeName(), attr);
									node.createHaveConnection(
											attribute.getRelativeName(), ar1);
								} else {
									node.createHaveConnection(
											attribute.getRelativeName(), attr);
								}
							}
						}
					}
					return node;
				}
				case SET: {
					// Set can not be abstract, if you get something like that,
					// please, never doubt to kick ass of the our support team
					// (namely Sedrak)
					SetConcept concept = (SetConcept) source;
					boolean identical = IdentityChecker.check(
							concept.getParent(), concept);
					if (identical) {
						// TODO: here I'm not sure there are not any issues with
						// set parent
						return parent.getGANode();
					}
					node = new GASet(source);
					node.createBeConnection(parent.getGANode());
					// We don't need to build to create Be connection, because
					// Sets are not abstracts.
					// If you, by any means, ever in your lifetime will think
					// that you need one, don't
					// take this personal, you are an asshole.
					// node.createBeConnection(source.getParent().getGANode();

					node.createHaveConnection(concept.getRelativeName(),
							insertAbstract(concept.getElement(), isVirtualSlot));
					((GASet) node).setRule(concept.getElementCount());
					return node;
				}
				case PRIMITIVE: {
					PrimitiveConcept concept = (PrimitiveConcept) source;
					// Check if Rules are the same
					// Rule is WildCard
					// Rule is Dependent
					boolean passthroughNode = IdentityChecker.check(
							concept.getParent(), concept)
							|| (concept.getRule() instanceof RuleWildcard)
							|| !(concept.getRule().getDependencies().isEmpty());

					if (passthroughNode) {
						return parent.getGANode();
					}
					boolean isAbstract = concept.isAbstract();
					if (isAbstract) {
						// If the nucleus concept is abstract, it is not being
						// connected with "have" connection,
						// However, we can still get here when crating nucleus
						// attributes of virtual abstracts.
						// However, if the child concept will request for the
						// parent GANode,
						// to create a Be connection, the "source".getParent()
						// will see that it has no GANode
						// and will return its parent’s GANode.
						return null;
					}
					// Dependent and [*] rules are handled in pass-through
					// checking. (see above)
					node = new GANucleus(concept);
					node.createBeConnection(parent.getGANode());
					return node;
				}
				default:
					throw new GAException("Trying to insert an invalid type of abstract");
				}
			}
		}
		// Everyone shall be happy. 
		// And they are :-) - almost :/
	}
	
	boolean exists(AbstractBase abstr) {
		return !m_insertedPrimaries.add(abstr.getName());
	}
}