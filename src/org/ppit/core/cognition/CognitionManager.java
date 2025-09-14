package org.ppit.core.cognition;

import org.ppit.actions.viz.VisualizationManagerAction;
import org.ppit.core.brain.*;
import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.gaNode.listener.WorkingMemoryListener;
import org.ppit.core.brain.instance.nucleus.*;
import org.ppit.core.concept.*;
import org.ppit.core.concept.action.ActionLibrary;
import org.ppit.core.percept.*;
import org.ppit.util.exception.PPITException;

/** 
 * @author Karen
 * The Module is designed to integrate Abstracts' management (ConceptManager),
 * T-Print management (SituationManager) and Matching.
 * The connection between Abstracts' manager is as follows:
 * 		The initialization, modification or definition of a new meaning
 * 		is forwarded this module, which takes care about integration of a meaning into a GA.
 * T-Print manager, on the other hand, sends the new situation or a delta situation to the Cognition module, 
 * 		which later performs the matching and keeps, or fires the results. 
 */
public class CognitionManager {
	
	private static CognitionManager m_instance; 
	
	static {
		m_instance = new CognitionManager();
	}

	public static CognitionManager getInstance() {
		return m_instance;
	}
	
	private GA m_ga = null;
	private GACreator m_gaCreator = null;
	private WorkingMemoryListener m_wm = null;
	
	
	private CognitionManager() {		
		m_ga = new GA();
		m_gaCreator = new GACreator(m_ga);
		
		m_wm = new WorkingMemoryListener();
		m_gaCreator.registerWMListener(m_wm);
		
		
	}

	public void processSituation(Situation situation) throws PPITException {
		// Clean the GA from the previous state.
		cleanCaches();
		
		// for removeing vizualization stream.
		VisualizationManagerAction.removeGexf();
		
		// Process the situation on the GA.
		for(IdGroup idg : situation.getElements()) {
			for(NucleusInstance ni:  idg.getElements()) {
				m_ga.processInstance(ni);
			}
		}
		//post processing is required
		m_wm.clearNegationCaches();
		m_wm.postProcess();
	}
	
	/**
	 * Only for testing purposes!
	 * @param ni - the nucleus instance.
	 */
	public void processNInstance(NucleusInstance ni) throws PPITException {
		m_ga.processInstance(ni);
	}
	
	public GANode insertAbstract(AbstractBase source) throws PPITException {
		return m_gaCreator.insertPrimaryAbstract(source);
	}
	
	public void removeAbstract(AbstractBase source) throws PPITException {
		m_gaCreator.removePrimaryAbstract(source);
	}
	
	public void addConcepts(ConceptLibrary library) throws PPITException {
		m_gaCreator.addConcepts(library);
	}
	
	public void addActions(ActionLibrary library) throws PPITException {
		m_gaCreator.addActions(library);
	}
	
	public WorkingMemoryListener getWM() {
		return m_wm;
	}
	
	/**
	 * Reset the abstracts and the parts connected to them, so that to be initialized from the scratch.
	 */
	public void reset() {
		m_gaCreator.reset();
		m_ga.reset();
		m_wm.reset();
	}
	
	/**
	 * Clean all the local caches, including the caches of GA for partial matches and WM for active instances.
	 */
	private void cleanCaches() {
		m_ga.cleanCaches();
	}
	
	/**
	 * This can be only for test purposes
	 */
	public GA getGA()	{
		return m_ga;
	}
	
	/**
	 * This can be only for test purposes.
	 */
	public GACreator getGACreator() {
		return m_gaCreator;
	}
}
