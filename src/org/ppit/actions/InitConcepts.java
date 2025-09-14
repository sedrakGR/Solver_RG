package org.ppit.actions;

import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;

public class InitConcepts extends ActionSupport {

	/**
	 * @brief boolean isLoadedDB indicates if initial load is already done or not
	 */
	private static boolean isLoadedDB = false;

    public InitConcepts() {
        super();
    }
	
	public String execute() {
		if (!isLoadedDB)
		{
			ConceptManager.getInstance().initialLoadAll();
			isLoadedDB = true;
		}
		return SUCCESS;
	}

}