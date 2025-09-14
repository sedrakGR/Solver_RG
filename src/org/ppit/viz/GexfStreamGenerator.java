package org.ppit.viz;

import it.uniroma1.dis.wiserver.gexf4j.core.EdgeType;
import it.uniroma1.dis.wiserver.gexf4j.core.Gexf;
import it.uniroma1.dis.wiserver.gexf4j.core.Mode;
import it.uniroma1.dis.wiserver.gexf4j.core.Node;
import it.uniroma1.dis.wiserver.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wiserver.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wiserver.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wiserver.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wiserver.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wiserver.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wiserver.gexf4j.core.impl.data.AttributeListImpl;
import it.uniroma1.dis.wiserver.gexf4j.core.impl.viz.ColorImpl;
import it.uniroma1.dis.wiserver.gexf4j.core.impl.viz.PositionImpl;
import it.uniroma1.dis.wiserver.gexf4j.core.viz.Color;
import it.uniroma1.dis.wiserver.gexf4j.core.viz.EdgeShape;
import it.uniroma1.dis.wiserver.gexf4j.core.viz.Position;

/*import org.gephi.graph.api.*;*/

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;

import org.gephi.layout.plugin.AbstractLayout;
import org.ppit.core.concept.ConceptType;
import org.ppit.util.Definitions;



public class GexfStreamGenerator {
	
	public Node getGexfNode(
			it.uniroma1.dis.wiserver.gexf4j.core.Graph graph,String id ) {
		
		for(Node node : graph.getNodes()){
			if(node.getId().equals(id)) {
				return node;
			}
		}
		return null;
	}

	public Gexf script(AbstractLayout absLayout, int timeout_seconds) {
		Gexf gexf = new GexfImpl();
		Calendar date = Calendar.getInstance();
		InitializeForceDirectedLayout initForceDL = new InitializeForceDirectedLayout(absLayout, timeout_seconds);
		org.gephi.graph.api.Graph gephiGraph = initForceDL.getGraphModel().getDirectedGraph();	
		
		gexf.getMetadata()
			.setLastModified(date.getTime())
			.setCreator("SSRGT")
			.setDescription("SSRGT graph.");

		it.uniroma1.dis.wiserver.gexf4j.core.Graph gexfGraph = gexf.getGraph();
		
		gexfGraph.setDefaultEdgeType(EdgeType.DIRECTED).setMode(Mode.STATIC);
		
		AttributeList nodeAttrList = new AttributeListImpl(AttributeClass.NODE);
		gexfGraph.getAttributeLists().add(nodeAttrList);		
		Attribute nodeType = nodeAttrList.createAttribute( AttributeType.STRING,"NodeTypeName" );
		
		AttributeList edgeAttrList = new AttributeListImpl(AttributeClass.EDGE);
		gexfGraph.getAttributeLists().add(edgeAttrList);		
		Attribute edgeType = edgeAttrList.createAttribute( AttributeType.STRING,"EdgeTypeName" );
		
		for(org.gephi.graph.api.Node gephiNode : gephiGraph.getNodes()) {
			
			Node gexfNode = gexfGraph.createNode(gephiNode.getNodeData().getId());
			
			
			gexfNode
				.setLabel(gephiNode.getNodeData().getLabel())				
				.getAttributeValues()
					.addValue(nodeType,  ConceptType.getConceptTypeByType( (Character)gephiNode.getAttributes()
							.getValue(Definitions.visualizationAttributeGANodeType)).getName());
			
			Position position = new PositionImpl();
			position.setX(gephiNode.getNodeData().x());
			position.setY(gephiNode.getNodeData().y());	
			gexfNode.setPosition(position);
		//	gexfNode.setSize((Float) gephiNode.getAttributes().getValue("size"));
			
			gexfNode.setSize(gephiNode.getNodeData().getSize());
			
			Color nodeColor = new ColorImpl();
			ConceptType gaNodeType = ConceptType.getConceptTypeByType((Character)gephiNode.getAttributes()
					.getValue(Definitions.visualizationAttributeGANodeType));
			
			switch (gaNodeType) {
						case COMPOSITE:
							nodeColor.setR(61);
							nodeColor.setG(116);
							nodeColor.setB(153);
							break;
						case PRIMITIVE :
						case NUCLEUS :
							nodeColor.setR(96);
							nodeColor.setG(239);
							nodeColor.setB(204);
							break;
						case SET :
							nodeColor.setR(184);
							nodeColor.setG(255);
							nodeColor.setB(99);
							break;
						case ACTION :
							nodeColor.setR(25);
							nodeColor.setG(109);
							nodeColor.setB(77);
							break;
						case VIRTUAL :
							nodeColor.setR(115);
							nodeColor.setG(75);
							nodeColor.setB(129);
							break;
						case USAGE :
							nodeColor.setR(201);
							nodeColor.setG(89);
							nodeColor.setB(143);
							break;
						case AR1 :
							nodeColor.setR(189);
							nodeColor.setG(137);
							nodeColor.setB(235);
							break;
					}
		
			gexfNode.setColor(nodeColor);
//			gexfNode.setSize(4);
		}
		
	
		for(org.gephi.graph.api.Edge gephiEdge : gephiGraph.getEdges()) {
			
			Node sourceNode = getGexfNode(gexfGraph,
					gephiEdge.getSource().getNodeData().getId());
			
			Node targetNode = getGexfNode(gexfGraph,
					gephiEdge.getTarget().getNodeData().getId());
			
			it.uniroma1.dis.wiserver.gexf4j.core.Edge gexfEdge = 
												sourceNode.connectTo(targetNode);
			
			org.ppit.core.brain.connection.EdgeType gaEdgeType = org.ppit.core.brain.connection.EdgeType.getEdgeTypeByType(
					(Character) gephiEdge.getAttributes().getValue(Definitions.visualizationAttributeGAEdgeType));
			
			gexfEdge
			//.setLabel(gephiEdge.getEdgeData().getLabel())				
			.getAttributeValues()
				.addValue(edgeType, gaEdgeType.getName());
			Color edgeColor = new ColorImpl();
			
			
			switch (gaEdgeType) {
			case BE_EDGE:
				edgeColor.setR(255);
				edgeColor.setG(27);
				edgeColor.setB(0);
				gexfEdge.setShape(EdgeShape.SOLID);
				gexfEdge.setLabel("Be");
				break;
			case HAVE_EDGE :
				edgeColor.setR(29);
				edgeColor.setG(0);
				edgeColor.setB(176);
				gexfEdge.setShape(EdgeShape.DOTTED);
				gexfEdge.setLabel("Have");
				break;
			case DO_EDGE :
				edgeColor.setR(97);
				edgeColor.setG(229);
				edgeColor.setB(0);
				gexfEdge.setShape(EdgeShape.DASHED);
				gexfEdge.setLabel("Do");
				break;
			}
			gexfEdge.setColor(edgeColor);
        }
		
		return gexf;
	}
	
}
