package org.ppit.viz;

import java.util.concurrent.TimeUnit;

import org.gephi.graph.api.DirectedGraph;

import org.gephi.graph.api.GraphModel;

import org.gephi.layout.plugin.AbstractLayout;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;


public class InitializeForceDirectedLayout {

	public GraphModel graphModel;
	 
	public GraphModel getGraphModel() {
		return graphModel;
	}
		
	
	public InitializeForceDirectedLayout(AbstractLayout layout, int timeout_seconds) {
		this.script(layout, timeout_seconds);
	}
	
	public void script(AbstractLayout layout, int timeout_seconds) { 		
		ToGephiGraphTranslator gt = new ToGephiGraphTranslator();
		gt.m_manager.initialLoadAll();
		graphModel = gt.generateGraph();
        
		DirectedGraph graph = graphModel.getDirectedGraph();
		
		//Layout for 10 seconds
		AutoLayout autoLayout = new AutoLayout(timeout_seconds, TimeUnit.SECONDS);
		autoLayout.setGraphModel(graphModel);
		
		//YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(1f));
		//ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);
		AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);//True after 10% of layout time
		AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", new Double(500.), 0f);//500 for the complete period
		//autoLayout.addLayout(firstLayout, 0.5f);
		//autoLayout.addLayout(layout, 1f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
		autoLayout.addLayout(layout, 1f);
		autoLayout.execute();
	}
}
