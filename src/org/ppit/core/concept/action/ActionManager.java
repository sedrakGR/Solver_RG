package org.ppit.core.concept.action;


import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.ConceptManager;
import org.ppit.db.DBActionManager;
import org.ppit.util.Pair;
import org.ppit.util.exception.PPITException;

/**
 * @author SedrakG
 * @brief This is ActionManager class, manages actions, the class is singleton
 */
public class ActionManager {
	// action library
	private ActionLibrary m_library = null;
	// handler for action creating
	private ActionCreator m_creator = null;
	// DBActionManager manager of action changes in DB
	private DBActionManager m_dbActionManager = DBActionManager.getInstance();
		
	/**
	 * @brief private constructor, as it's singleton, it's constructor might be private
	 */
	public ActionManager(ConceptManager conceptManager)
	{
		m_library = new ActionLibrary();
		m_creator = new ActionCreator(m_library, conceptManager);
	};
	
	/**
	 * @brief creates an action with the given JSON object
	 * @param actionJSON JSON of the action
	 * @return Returns the created action
	 * @throws JSONException
	 * @throws PPITException
	 */
	public Pair<Action, Boolean> createAction(JSONObject actionJSON) throws JSONException, PPITException	{
		Pair<Action, Boolean> res = m_creator.createAction(actionJSON); 
		Action action = res.first;
		m_dbActionManager.writeAction(action);
		return res;
	}
	
	/**
	 * 
	 * @param name, Name of the action to be removed
	 * @return returns the removed action
	 * @throws PPITException 
	 */
	public Action removeAction(String name) throws PPITException {
		Action action = m_library.removeAction(name);
		m_dbActionManager.removeAction(action);
		return action;
	}
	
	/**
	 * @brief getAction() get the action with the given name from the action library 
	 * @param name, Name of the action to be get
	 * @return Returns the action if it is found and null in other case
	 */
	public Action getAction(String name) {
		return m_library.getAction(name);
	}
	
	/**
	 * @brief This function is for initial load of actions from DB when program is launched
	 */
	public void initialLoadActions()
	{
		try {
			for (JSONObject actionJSON : m_dbActionManager.loadFromMongo()) {
				m_creator.createAction(actionJSON);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("**************************************\ntraqaaaaaaa");
			System.out.println("in ActionManager.initialLoadActions().");
		} catch (PPITException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @brief Retrieves the list of names of actions  in a string
	 * @return Returns a string containing names of actions
	 */
	public String getActionNames()
	{
		String actionList = "{'actionList' : [";
		for (Action action : m_library.getListOfActions()) {
			actionList += "{ 'name' : '" + action.getName() + "' },";
		}
		actionList += "{}]}";
		return actionList;
	}
	
	public ActionLibrary getLibrary() {
		return m_library;
	}
}
