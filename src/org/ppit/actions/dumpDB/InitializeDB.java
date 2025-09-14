package org.ppit.actions.dumpDB;

import org.ppit.test.brain.TestVirtualActivation;

import com.opensymphony.xwork2.ActionSupport;

public class InitializeDB extends ActionSupport {
    
	public InitializeDB() {
        super();
    }
	
	public String execute() {		
		TestVirtualActivation init = new TestVirtualActivation();
		boolean enable_asserts = false;
		init.test(enable_asserts);
		return SUCCESS;
	}
}