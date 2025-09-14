package org.ppit.actions.dumpDB;

import java.io.File;

import org.ppit.db.DBActionManager;
import org.ppit.db.DBManager;
import org.ppit.util.ConfigManager;

import com.opensymphony.xwork2.ActionSupport;

public class UnDump extends ActionSupport {
	//default value for path is json dir path
	//private static String m_dirPath = ConfigManager.getInstance().getJSONDirPath();

    public UnDump() {
        super();
    }
	
	public String execute() {
//		System.out.println("Loading DB from Dump files from " + m_dirPath + " ...");
//		File dumpDir = new File(m_dirPath);
//		if ((!dumpDir.exists()) || (!dumpDir.isDirectory())) {
//			//TODO: maybe need to throw exception
//			System.out.println("There is no such directory");
//		}
//		DBManager.getInstance().unDump(m_dirPath);
//		DBActionManager actionUnDumper = DBActionManager.getInstance();
//		actionUnDumper.unDump(m_dirPath);
		return SUCCESS;
	}
	
	public void setDirPath(String path) {
		// do nothing for now
		//m_dirPath = path;
	}
}
