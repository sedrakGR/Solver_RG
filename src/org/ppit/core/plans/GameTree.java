package org.ppit.core.plans;

import java.util.ArrayList;

import org.ppit.core.brain.instance.action.ActionInstance;
import org.ppit.core.percept.Situation;
import org.ppit.util.exception.PPITException;

public class GameTree {
	/**
	 * root node of the tree
	 */
	GameTreeNode m_root;
	
	/**
	 * depth of the tree
	 */
	int m_depth;
	
	ArrayList<GameTreeNode> m_allTreeNodes;
	
	/**
	 * TODO:
	 * Currently assume side has only two values 1 and 2
	 * and 0 value means none of them
	 */
	int m_playerSide;
	
	int m_sidesCount = 2; 
	
	public GameTree(Situation situation, int side, int depth) {
		m_root = new GameTreeNode(null, situation, side);
		m_root.setTree(this);
		m_depth = depth;
		m_playerSide = side;
		m_allTreeNodes = new ArrayList<GameTreeNode>();
		m_allTreeNodes.add(m_root);
		generateTree();
	}
	
	public void generateTree() {
		createNodes(m_root, m_root.getSide(), m_depth);
	}
	
	public GameTreeNode createNextNode(GameTreeNode parent, ActionInstance actionToApply, int side) {
		GameTreeNode node = null;
		try {
			Situation newSituation = parent.getSituation().applyAction(
					actionToApply);
			node = new GameTreeNode(parent, newSituation, side);
			parent.addChildNode(node, actionToApply);

		} catch (PPITException e) {
			System.out.println("exception when applyng action");
			e.printStackTrace();
		}
		return node;
	}
	
	public void createNodes(GameTreeNode parent, int side, int depth) {
		if (depth <= 0) {
			return;
		}
		if (parent == null || parent.getActiveActions() == null) {
			return;
		}
		for (ActionInstance actionToApply : parent.getActiveActions()) {
			// 2 - side + 1 means 1 if side is 2 and 2 if side is 1
			//TODO: correct this if other algorithm of side selection is developed
			try {
				GameTreeNode node = new GameTreeNode(parent, parent.getSituation().applyAction(actionToApply), (2 - side + 1));
				parent.addChildNode(node, actionToApply);
				m_allTreeNodes.add(node);
				createNodes(node, node.getSide(), depth - 1);
			} catch (PPITException e) {
				System.out.println("exeption when creating node");
				e.printStackTrace();
			}
		}
		//createNextNode(parent, actionToApply, side);
	}
	
	public GameTreeNode getRoot() {
		return m_root;
	}
	
	public ArrayList<GameTreeNode> getAllNodes() {
		return m_allTreeNodes;
	}
	
	public ArrayList<GameTreeNode> getNodesInDepth(int depth) {
		ArrayList<GameTreeNode> nodesWithTheGivenDepth = new ArrayList<GameTreeNode>();
		for (GameTreeNode node : m_allTreeNodes) {
			if (node.getDepth() == depth) {
				nodesWithTheGivenDepth.add(node);
			}
		}
		return nodesWithTheGivenDepth;
	}
	
	public void removeNode(GameTreeNode nodeToRemove) {
		nodeToRemove.remove(m_playerSide);
	}
	
	public void removeNodeFromNodesList(GameTreeNode nodeToRemove) {
		m_allTreeNodes.remove(nodeToRemove);
	}
}
