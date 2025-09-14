package org.ppit.core.percept;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.brain.instance.action.ActionInstance;
import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.ConceptLibrary;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.primitive.PrimitiveType;
import org.ppit.core.plans.Evaluator;
import org.ppit.core.plans.Plan;
import org.ppit.core.plans.PlanWrapper;
import org.ppit.db.DBManager;
import org.ppit.util.exception.BrokenCR1;
import org.ppit.util.exception.PPITException;

/**
 * 
 * @author Karen Khachatryan S.
 * @detailed The Container which holds and manages all situations.
 *
 */

public class SituationManager {
	
	private static SituationManager m_instance;
	
	private SituationLibrary m_library;
	private SituationCreator m_creator;
	private DBManager m_dbManager = DBManager.getInstance();
	
	private Integer m_situationId = 0;
	private String m_lastSituationName;
	
	private Plan m_plan;
	private ArrayList<ActionInstance> m_suggestedActionsByThePlan = null;
	
	static {
		m_instance = new SituationManager();
	}
	
	/**
	 * @brief getInstance(), Get the instance of Concept manager
	 * @return ConceptManager
	 */
	public static SituationManager getInstance() {
		return m_instance;
	}
	
	private SituationManager() {
		m_library = new SituationLibrary();
		m_creator = new SituationCreator();
	}
	
	private String getNextSituationId() {
		++m_situationId;
		return m_situationId.toString();
	}
	
	public String getLastSituationName() {
		return m_lastSituationName;
	}
	
	public Situation createSituation(JSONObject sitJSON) throws JSONException, PPITException
	{
		Situation situation = m_creator.createSituation(sitJSON);
		
		if(situation.getName().isEmpty()) {
			situation.setName(getNextSituationId());
		}
		
		m_lastSituationName = situation.getName();

		// If there is with the same name in DB then modify it (delete + add new one).
		Situation duplicateSit = m_library.getSituation(situation.getName());
		
		if (duplicateSit != null){
			m_dbManager.removeSituation(duplicateSit);
		}
		
		m_library.addSituation(situation);
		m_dbManager.writeSituation(situation);
		
		return situation;
	}
	
	public ArrayList<ActionInstance> processPlan(Plan plan, Situation situation, int side) {
		CognitionManager.getInstance().getGA().cleanCaches();
		m_plan = plan;
		m_suggestedActionsByThePlan = plan.executePlan(situation, side);
		return m_suggestedActionsByThePlan;
	}
	
	public Plan selectBestPlan(Situation situation, int side) throws PPITException {
		//TODO: need to handle side
		CognitionManager.getInstance().processSituation(situation);	
		ArrayList<PlanWrapper> goodPlans = new ArrayList<PlanWrapper>();
		ConceptManager.getInstance().resetPlanMatchingCount();
		for (PlanWrapper planWrapperInstance : ConceptManager.getInstance().getPlanWrapperList()) {
			if(planWrapperInstance.isGoodForProcessedSituation()) {
				goodPlans.add(planWrapperInstance);
				
			}
		}
		PlanWrapper bestPlanWrapper = null;
		HashMap<Integer, Integer> evaluationValue = new HashMap<Integer, Integer>();
		for (PlanWrapper planWrapper : goodPlans) {
			HashMap<Integer, Integer> value = planWrapper.getEvaluator().evaluate(planWrapper.getPreConditionActiveInstance());
			// we assume all evaluators for the same situation must have the same criteria
			if (bestPlanWrapper != null) {
				int comparison = Evaluator.compareEvaluation(evaluationValue, value);
				if (comparison == 0) {
					if (bestPlanWrapper.getPlan().getMatchCount() < planWrapper.getPlan().getMatchCount()) {
						bestPlanWrapper = planWrapper;
						evaluationValue = value;
					}
				} else if (comparison < 0) {
					bestPlanWrapper = planWrapper;
					evaluationValue = value;
				}
			} else {
				bestPlanWrapper = planWrapper;
				evaluationValue = value;
			}
		}
		return bestPlanWrapper.getPlan();
	}
	
	public ActionInstance getSuggestedAction(int currentId) {
		return m_suggestedActionsByThePlan.get(currentId);
	}
	
	public int getSuggestedActionCount() {
		return m_suggestedActionsByThePlan.size();
	}
	
	/**
	 * @brief loads all situations initially
	 */
	public void initialLoadSituations() {
		try {
			for (JSONObject situation : m_dbManager.initialSituationsLoad()) {
				createSituation(situation);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("**************************************\ntraqaaaaaaa Situation create aneluc karces te");
			System.out.println("(y) 4 people Like this - Karen, Sedrak and 2 more people.");
		} catch (PPITException e) {
			System.out.println("Error while loading situations" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public SituationLibrary getLibraryForTest() {
		return m_library;
	}
	
	//TODO: move some part to createSituation
	public Situation createSituationFromFen(String FENSituation) {
		//"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
		int idCounter = 1;
		Situation situation = new Situation("tempSituation");
		PrimitiveType cordXType = ConceptManager.getInstance().getPrimitiveType("cordX");
		PrimitiveType cordYType = ConceptManager.getInstance().getPrimitiveType("cordY");
		PrimitiveType FigureColorType = ConceptManager.getInstance().getPrimitiveType("FigureColor");
		PrimitiveType FigureTypeType = ConceptManager.getInstance().getPrimitiveType("FigureType");
		for (int i = 0; i < FENSituation.length(); ++i) {
			char field = FENSituation.charAt(i);
			if (Character.isSpaceChar(field)) {
				break;
			}
			if (field == '/') {
				continue;
			}
			if (Character.isLetter(field)) {
				int color = getColorFromFen(field);
				int type = getFigureFromFen(field);
				boolean increament = (idCounter % 8 == 0);
				int cordY = 8 - idCounter / 8;
				int cordX = idCounter % 8;
				if (increament) {
					cordX = 8;
					++cordY;
				}
				IdGroup idgroup = new IdGroup(idCounter++);
				try {
					new NucleusInstance(cordX, cordXType, idgroup, null);
					new NucleusInstance(cordY, cordYType, idgroup, null);
					new NucleusInstance(color, FigureColorType, idgroup, null);
					new NucleusInstance(type, FigureTypeType, idgroup, null);
				} catch (BrokenCR1 e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					situation.addElement(idgroup);
				} catch (PPITException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (Character.isDigit(field)) {
				int number = field - '0';
				for (int j = 0; j < number; ++j) {
					boolean increament = (idCounter % 8 == 0);
					int cordY = 8 - idCounter / 8;
					int cordX = idCounter % 8;
					if (increament) {
						cordX = 8;
						++cordY;
					}
					IdGroup idgroup = new IdGroup(idCounter++);
					try {
						new NucleusInstance(cordX, cordXType, idgroup, null);
						new NucleusInstance(cordY, cordYType, idgroup, null);
						new NucleusInstance(0, FigureColorType, idgroup, null);
						new NucleusInstance(0, FigureTypeType, idgroup, null);
					} catch (BrokenCR1 e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						situation.addElement(idgroup);
					} catch (PPITException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		return situation;
	}
	
	private int getColorFromFen(char field) {
		if (Character.isUpperCase(field)) {
			return 1;
		} else {
			return 2;
		}
	}
	
	private int getFigureFromFen(char field) {
		int value = 0;
		switch (Character.toLowerCase(field)) {
		case 'r':
			value = 4;
			break;
		case 'n':
			value = 2;
			break;
		case 'b':
			value = 3;
			break;
		case 'q':
			value = 5;
			break;
		case 'k':
			value = 6;
			break;
		case 'p':
			value = 1;
			break;
		default:
			break;
		}
		return value;
	}
}
