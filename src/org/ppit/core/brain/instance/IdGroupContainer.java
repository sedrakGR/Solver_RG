package org.ppit.core.brain.instance;

import java.util.HashSet;

import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.util.Definitions;

/**
 * @author Karen
 * @brief the class is designed to represent the unique IdGroups in an instance.    
 */
public class IdGroupContainer {
	
	private HashSet<IdGroup> m_idGroups = new HashSet<IdGroup>();; 
	
	public IdGroupContainer () {
	}
	
	public IdGroupContainer(IdGroup idGroup) {
		addIdGroup(idGroup);
	}
	
	public void addIdGroup(IdGroup idGroup) {
		m_idGroups.add(idGroup);
	}
	
	public void addIdGroup(IdGroupContainer idGroups) {
		m_idGroups.addAll(idGroups.getIdGroups());
	}
	
	public HashSet<IdGroup> getIdGroups() {
		return m_idGroups;
	}
	
	public String getOnlyIdsJSON(int instanceId) {
		String jsonStr = "{'" + Definitions.situationInstanceId + "':'" + instanceId + "','" +
		Definitions.situationInstanceGroupIds + "':";
		jsonStr += getOnlyIdsJSON() + "}";
		return jsonStr;
	}
	
	private String getOnlyIdsJSON() {
		String jsonStr = "["; 
		boolean first = true;
		for(IdGroup id: m_idGroups) {
			if(first == false) {
				jsonStr += ",";
			}
			jsonStr += "{'" + Definitions.situationInstanceGroupId + "':'" + id.getIdGroup() + "'}";
			first = false;
		}
		jsonStr += "]";
		return jsonStr;
	}
	
	public void print(boolean detailed){
		System.out.println("Dumping the Id Groups in the Instance.");
		for(IdGroup idg: m_idGroups) {
			System.out.print("Id: " + idg.getIdGroup());
			if(detailed) {
				System.out.println(", Attributes:");
				for(NucleusInstance ni : idg.getElements()) {
					System.out.println("Type: " + ni.getType().getType() + ", Value:" + ni.getValue());
				}
			}
			System.out.println(""); 
		}
		System.out.println("End of the Instance.");
	}
}
