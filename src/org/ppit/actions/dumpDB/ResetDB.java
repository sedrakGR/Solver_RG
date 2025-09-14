package org.ppit.actions.dumpDB;

import org.ppit.test.TestHelper;

import com.opensymphony.xwork2.ActionSupport;


public class ResetDB extends ActionSupport {
    
	public ResetDB() {
        super();
    }
	
	public String execute() {		
		TestHelper th = new TestHelper();
		th.resetAllResources();
		return SUCCESS;
	}
}
