package org.ppit.actions.viz;

import it.uniroma1.dis.wiserver.gexf4j.core.impl.StaxGraphWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletResponseAware;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ppit.viz.GexfStreamGenerator;
import org.ppit.viz.Skeleton;

import com.opensymphony.xwork2.ActionSupport;

public class SkeletonAction extends ActionSupport/* implements ServletResponseAware*/ {
	
	HttpServletResponse response;
	 
	String activeNode;
	
	Skeleton skeleton;
	
	JSONObject json;

	public String execute(){		
		skeleton = new Skeleton();
		json = new JSONObject();
		
		JSONArray array = new JSONArray();
		Set<String> list = new HashSet<String>();
		list = skeleton.getSkeleton(activeNode);
		
		/*
		list.add("Figure");
		list.add("FigureColor");
		list.add("Figure.fc");
		*/
		array.addAll(list);
		json.put("array", array);			
		
		return SUCCESS;
	
	}

	/*@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = response;
		
	}*/
	
	/*public String getActiveNode() {
		return activeNode;
	}*/
	
	public JSONObject getJson() {
		return json;
	}

	public void setActiveNode(String activeNode) {
		this.activeNode = activeNode;
	}
}
