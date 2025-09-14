package org.ppit.actions.viz;

import it.uniroma1.dis.wiserver.gexf4j.core.Gexf;
import it.uniroma1.dis.wiserver.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wiserver.gexf4j.core.viz.EdgeShape;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.ppit.viz.GexfStreamGenerator;
import org.ppit.viz.LayoutType;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.gephi.layout.plugin.AbstractLayout;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;


/**
 * @description Action for managing gexf Stream 
 * creation and sending to web view.
 * @author Shushanik Nersesyan
 */

public class VisualizationManagerAction extends ActionSupport implements ServletResponseAware{

	private static final long serialVersionUID = 1L;

	private static	String layoutAlgorithm = null;	
	private static Gexf gexf = null;
	private int timeout_seconds = 10;

	HttpServletResponse response;

	public String view() {
		return SUCCESS;
	}

	private AbstractLayout initializeLayout(String layoutAlgorithm ) {
		AbstractLayout layout;

		LayoutType type = LayoutType.valueOf(layoutAlgorithm);
		switch(type){
		case FORCE_ATLAS:
			timeout_seconds = 15;
			layout = new ForceAtlasLayout(null);
			break;
		case YIFANHU_MULTILEVEL:
			timeout_seconds = 10;
			layout = new YifanHuLayout(null, new StepDisplacement(1f));;
			break;	
		default:
			layout = new ForceAtlasLayout(null);
		}

		return layout ;
	}

	public String execute(){
		if(gexf == null) {
			gexf = new GexfStreamGenerator().script(initializeLayout(layoutAlgorithm), timeout_seconds);
		}
		
		StaxGraphWriter	graphWriter = new StaxGraphWriter();
		response.setContentType("text/xml");
		try {			
			Writer out = response.getWriter();
			gexf.setVisualization(true);
			gexf.setVariant("lastVariant");
			graphWriter.writeToStream(gexf, out, "UTF-8");

		} catch (IOException e) {
			e.printStackTrace();
			return ERROR;
		}
		return null;
	}

	
	public static void removeGexf() {
		VisualizationManagerAction.gexf = null ;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getLayoutAlgorithm() {
		return layoutAlgorithm;
	}

	public void setLayoutAlgorithm(String layoutAlg) {
		if(layoutAlgorithm == null || !layoutAlgorithm.equals(layoutAlg)){
			gexf = null;
		}
		layoutAlgorithm = layoutAlg;
	}
}
