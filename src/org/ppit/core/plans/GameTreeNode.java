package org.ppit.core.plans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.action.ActionInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.percept.Situation;
import org.ppit.util.exception.PPITException;

/**
 * 
 * @author Sedrak
 * @brief this describes the game tree node, which keeps in it situation of the node
 * and the evaluation value given by some criteria
 *
 */
public class GameTreeNode {
	/**
	 * situation of the node
	 */
	private Situation m_Situation;
	
	/**
	 * @brief the tree to which this node belongs	
	 */
	private GameTree m_tree;
	
	/**
	 * depth of the node from the tree root
	 * root depth is 1
	 * value of depth equals to parent node's depth + 1
	 */
	private int m_depth;
	/**
	 * all the activated abstracts when processing the situation
	 */
	private ArrayList<InstanceBase> m_activeAbstracts;
	
	private ArrayList<String> m_activateAbstractNames;
	/**
	 * list of active in the situation actions
	 */
	private ArrayList<ActionInstance> m_activeActions;
	/**
	 * describes the side that is allowed to perform action in the current situation
	 */
	private int m_sideToAct;
	
	/**
	 * evaluation value for the situation
	 */
	private HashMap<Integer, Integer> m_evaluationValue = null;
	
	/**
	 * the list of nodes that are being reproduced from this node
	 */
	private HashMap<GameTreeNode, ActionInstance> m_reproducedNodes;
	
	/**
	 * parent node from which this node is being generated
	 */
	private GameTreeNode m_parentNode;
	
	public GameTreeNode(GameTreeNode parent, Situation situation, int side) {
		m_Situation = situation;
		m_sideToAct = side;
		m_parentNode = parent;
		m_reproducedNodes = new HashMap<GameTreeNode, ActionInstance>();
		if (parent != null) {
			m_depth = parent.getDepth() + 1;
			m_tree = parent.m_tree;
		} else {
			m_depth = 0;
		}
		
		processNode();
	}
	
	public void processNode() {
		try {
			CognitionManager.getInstance().processSituation(m_Situation);
			m_activeAbstracts = CognitionManager.getInstance().getWM().getActiveInstances();
			m_activateAbstractNames = CognitionManager.getInstance().getWM().getActivatedAbstracts();
			m_activeActions = CognitionManager.getInstance().getWM().getActiveActions(m_sideToAct);
		} catch (PPITException e) {
			System.out.println("Error when processing the situation: " + e.getMessage());
			System.out.println("Situation name is: " + getSituation().getName());
			e.printStackTrace();
		}
	}

	public Situation getSituation() {
		return m_Situation;
	}
	
	public ArrayList<InstanceBase> getActiveAbstracts() {
		return m_activeAbstracts;
	}
	
	public int getDepth() {
		return m_depth;
	}
	
	public ArrayList<ActionInstance> getActiveActions() {
		return m_activeActions;
	}
	
	public int getSide() {
		return m_sideToAct;
	}

	public HashMap<Integer, Integer> getEvaluationValue() {
		return m_evaluationValue;
	}

	public void setEvaluationValue(HashMap<Integer, Integer> evaluationValue) {
		m_evaluationValue = evaluationValue;
	}
	
	/**
	 * Compares two nodes evaluations
	 * @param otherNode Node to compare with
	 * @return Returns 1/0/-1 value range, 1 - if this node is better, 0 if they are equal and -1 if otherNode is better
	 */
	public int compareEvaluation(GameTreeNode otherNode) throws PPITException {
		return Evaluator.compareEvaluation(m_evaluationValue, otherNode.m_evaluationValue);
	}
	
	/**
	 * adds a child not which is reproduced from this node
	 */
	public void addChildNode(GameTreeNode child, ActionInstance action) {
		m_reproducedNodes.put(child, action);
	}
	
	public GameTreeNode getParent() {
		return m_parentNode;
	}
	
	public boolean hasActiveInstances(String name) {
		return m_activateAbstractNames.contains(name);
	}
	
	public void setTree(GameTree tree) {
		m_tree = tree;
	}
	
	/**
	 * @brief Removes the subtree starting from the node.
	 * If the node is achieved by the opponent if also removes the parent node, beacuse that node does not satisfy too.
	 * @param ownSide the side of player that generates the tree
	 */
	public void remove(int ownSide) {
		removeChildNodes();
		if (m_parentNode != null) {
			m_tree.removeNodeFromNodesList(this);
			m_parentNode.removeChild(this);
			if (ownSide == m_sideToAct) {
				// opponet achieved this situation thus we need to remove also
				// its parent
				m_parentNode.remove(ownSide);
			} else if (m_parentNode.m_reproducedNodes.isEmpty()) {
				// no good move to overcome this situation, then need to avoid this situation
				m_parentNode.remove(ownSide);
			}
		}
	}
	
	public void removeChild(GameTreeNode childNode) {
		if (m_reproducedNodes.remove(childNode) == null) {
			System.out.println("child not not found to remove");
		}
	}
	
	private void removeChildNodes() {
		for (GameTreeNode child : m_reproducedNodes.keySet()) {
			m_tree.removeNodeFromNodesList(child);
			child.removeChildNodes();
		}
	}
	
	public Collection<ActionInstance> getAppliedActions() {
		return m_reproducedNodes.values();
	}

}
