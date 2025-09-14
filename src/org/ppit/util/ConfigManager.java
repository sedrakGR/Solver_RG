package org.ppit.util;

import java.io.*;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




public class ConfigManager {
	private static ConfigManager instance = null;
	//private static String PPIT_HOME = System.getenv("PPIT_HOME");
	
	private boolean m_testEnabled = false; 
	private boolean m_dumpEnabled = false;
	
	//defaultValue
	//String jsonDirPath = PPIT_HOME + "/jsons/";
	
	private ConfigManager(){
		//readTestConfig();
	}
	
	public static ConfigManager getInstance(){
		if(instance == null){
			instance = new  ConfigManager();
		} 
		return instance;
	}
	
	public String getJSONDirPath() {
		return "";
	}

	/**
	 * @return Returns true if configured to run in test mode, else in other case  
	 */
	public boolean isTestEnabled() {
		return m_testEnabled;
	}
	
	public boolean isAutoDumpEnabled() {
		return m_dumpEnabled;
	}
	
	/**
	 * @brief Read if the project is configured to run in test or main mode
	 */
	private void readTestConfig()
	{
//		try {
//			//NOTE: didn't do deep investigation, just used similar to some sample code 
//			File fXmlFile = new File(PPIT_HOME + "/xmls/" + "config.xml");
//			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//			Document doc = dBuilder.parse(fXmlFile);
//			doc.getDocumentElement().normalize();
//	 
//			NodeList nList = doc.getElementsByTagName("Test");
//			Element testEnable = (Element)nList.item(0);
//			if(testEnable.getAttribute("Enabled").equals("true"))
//			{
//				m_testEnabled = true;
//			}
//			nList = doc.getElementsByTagName("AutoDump");
//			Element dumpEnable = (Element)nList.item(0);
//			if(dumpEnable.getAttribute("Enabled").equals("true"))
//			{
//				m_dumpEnabled = true;
//			}
//			
//			nList = doc.getElementsByTagName("JsonDirPath");
//			Element jsonDirPathElement = (Element)nList.item(0);
//			jsonDirPath = PPIT_HOME + "/" + jsonDirPathElement.getTextContent();
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//		} catch (SAXException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// probably configuration xml is missing
//			e.printStackTrace();
//		}
	}
}
