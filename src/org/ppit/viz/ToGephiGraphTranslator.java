package org.ppit.viz;

import java.util.ArrayList;

import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;
import org.ppit.core.brain.GA;
import org.ppit.core.brain.GAEdge;
import org.ppit.core.brain.connection.EdgeType;
import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.gaNode.nucleus.GANucleus;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.ConceptType;
import org.ppit.util.Definitions;

/**
 * This class is designed to translate The internal Graph of Abstracts into 
 * Gephi graph model. 
 * It iterates over all Be, Have Do connections of the meanings and builds the
 * target Graph.
 * 
 * @author Karen Khachatryan
 */
public class ToGephiGraphTranslator {
	
	CognitionManager m_cognition = CognitionManager.getInstance();
	ConceptManager m_manager = ConceptManager.getInstance();
	
	// For local testing.
	void test() {
		ToGephiGraphTranslator gt = new ToGephiGraphTranslator();
		gt.m_manager.initialLoadAll();
		GraphModel gm = gt.generateGraph();
        
		DirectedGraph directedGraph = gm.getDirectedGraph();
        
        //Count nodes and edges
        System.out.println("Nodes: "+directedGraph.getNodeCount()+" Edges: "+directedGraph.getEdgeCount());

        //Get a UndirectedGraph now and count edges
        UndirectedGraph undirectedGraph = gm.getUndirectedGraph();
        System.out.println("Edges: "+undirectedGraph.getEdgeCount());   //The mutual edge is automatically merged

        //Iterate over nodes
        for(Node n : directedGraph.getNodes()) {
            Node[] neighbors = directedGraph.getNeighbors(n).toArray();
            
            char tmpType = (Character)n.getNodeData().getAttributes().getValue(Definitions.visualizationAttributeGANodeType);
            ConceptType gaNodeType = ConceptType.getConceptTypeByType(tmpType);

            System.out.println(n.getNodeData().getId()+" has "+neighbors.length+" neighbors. Type: " +  gaNodeType);
        }
        

        //Iterate over edges
        for(Edge e : directedGraph.getEdges()) {
        	
        	char tmpType = (Character)e.getEdgeData().getAttributes().getValue(Definitions.visualizationAttributeGAEdgeType);
            EdgeType gaEdgeType = EdgeType.getEdgeTypeByType(tmpType);

        	System.out.println(e.getSource().getNodeData().getId()+" -> "+e.getTarget().getNodeData().getId() + ". Type: " + gaEdgeType);
        }
	}
	
	// Generates and returns the graph.
	public GraphModel generateGraph() {	
	    //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        pc.getCurrentWorkspace();

        //List node columns
        AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
        AttributeModel model = ac.getModel();

        //Add boolean column
        model.getNodeTable().addColumn(Definitions.visualizationAttributeGANodeType, AttributeType.CHAR);
        model.getNodeTable().addColumn(Definitions.visualizationAttributeGAEdgeType, AttributeType.CHAR);

        //Get a graph model - it exists because we have a workspace
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

        // Get the Graph of Abstracts.
        GA graphOfAbstracts = m_cognition.getGA();
        
        // Start iterating over all Roots of the Graph.
        // It is supposed that all Nodes of the graph are finally connected to the Root nodes.
        for(GANucleus node: graphOfAbstracts.getRoots()) {
        	insertNode(graphModel, node);
        }
        
        return graphModel;
    }
	
	private Node insertNode(GraphModel graphModel, GANode node) {
		if(node.isVisited()) {
			return (Node)node.getVisitor();
		}
		
		Node n = graphModel.factory().newNode(node.getAbstract().getName());
		
		boolean setSize = node.getAbstract().isZeroLevel();
		int size = 4;
		n.getNodeData().setLabel(node.getAbstract().getName());
		n.getNodeData().getAttributes().setValue(
				Definitions.visualizationAttributeGANodeType,node.getAbstract().getDetailedType().getType());
			
		node.setVisited(n);
		graphModel.getDirectedGraph().addNode(n);
		
		// In order to build the node, we have to build all its neighbors.
		// Building Parent connections. These are Be connections
		for(GAEdge beConn: node.getParents()) {
			GANode p = beConn.getMaster();
			Node pNode = null;
			if(p.isVisited()) {
				pNode = (Node)p.getVisitor();
			} else {
				pNode = insertNode(graphModel, p);
			}
			Edge e = graphModel.factory().newEdge(n, pNode);
			e.getEdgeData().getAttributes().setValue(
					Definitions.visualizationAttributeGAEdgeType, beConn.getEdgeType().getType());
			graphModel.getDirectedGraph().addEdge(e);
			if(setSize) {
				size +=2;
			}
			beConn.setVisited(e);
		}
		// Building Have connections to elements. These are Have connections
		for(GAEdge haveConn: node.getElements()) {
			GANode p = haveConn.getSlave();
			Node pNode = null;
			if(p.isVisited()) {
				pNode = (Node)p.getVisitor();
			} else {
				pNode = insertNode(graphModel, p);
			}
			Edge e = graphModel.factory().newEdge(n, pNode);
			e.getEdgeData().getAttributes().setValue(
					Definitions.visualizationAttributeGAEdgeType, haveConn.getEdgeType().getType());
			graphModel.getDirectedGraph().addEdge(e);
			if(setSize) {
				size +=2;
			}
			haveConn.setVisited(e);
		}
		
		// Building connections to actions. These are Do connections
		for(GAEdge doConn: node.getActions()) {
			GANode p = doConn.getSlave();
			Node pNode = null;
			if(p.isVisited()) {
				pNode = (Node)p.getVisitor();
			} else {
				pNode = insertNode(graphModel, p);
			}
			Edge e = graphModel.factory().newEdge(n, pNode);
			e.getEdgeData().getAttributes().setValue(
					Definitions.visualizationAttributeGAEdgeType, doConn.getEdgeType().getType());
			graphModel.getDirectedGraph().addEdge(e);
			if(setSize) {
				size +=2;
			}
			doConn.setVisited(e);
		}
		
		// Extracting all children.
		ArrayList<GANode> reflexiveNodes = new ArrayList<GANode>();
		for(GAEdge beConn: node.getChildren()) {
			GANode p = beConn.getSlave();
			if(p.isVisited()) {
				continue;
			}
			reflexiveNodes.add(p);
		}
		// Extracting all composers.
		for(GAEdge haveConn: node.getComposers()) {
			GANode p = haveConn.getMaster();
			if(p.isVisited()) {
				continue;
			}
			reflexiveNodes.add(p);
		}
		
		// Extracting all performers.
		for(GAEdge doConn: node.getPerformers()) {
			GANode p = doConn.getMaster();
			if(p.isVisited()) {
				continue;
			}
			reflexiveNodes.add(p);
		}
		
		// Processing all reflexive nodes.s
		for(GANode reflexiveNode: reflexiveNodes) {
			insertNode(graphModel, reflexiveNode);
		}
		
//		n.getAttributes().setValue("size", size);
		n.getNodeData().setSize(size);
		return n;
	}
}

