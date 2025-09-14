package org.ppit.actions.dumpDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.ppit.db.DBActionManager;
import org.ppit.db.DBManager;
import org.ppit.util.ConfigManager;

import com.opensymphony.xwork2.ActionSupport;

public class Dump extends ActionSupport {
	private static String m_dirPath = ConfigManager.getInstance().getJSONDirPath();
	
	public void setDirPath(String dirPath){
		//for now we don't need this
		//m_dirPath = dirPath;
	}	

	public String getDirPath() {
		// for now, we don't need this too
		return m_dirPath;
	}

    public Dump() {
        super();
    }
	
	public String execute() {
		System.out.println("Dupming DB to files in " + m_dirPath + " ...");
		DBManager.getInstance().dumpConcepts(m_dirPath);
		DBActionManager.getInstance().dumpActionCollection(m_dirPath);
		return SUCCESS;
	}
}
