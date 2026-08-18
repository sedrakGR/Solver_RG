package org.ppit.core.percept;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.brain.instance.action.ActionInstance;
import org.ppit.core.brain.instance.nucleus.*;
import org.ppit.core.concept.action.Action;
import org.ppit.core.concept.action.PostConditionAttribute;
import org.ppit.util.Definitions;
import org.ppit.util.Pair;
import org.ppit.util.exception.InvalidSituation;
import org.ppit.util.exception.PPITException;

/**
 * @author Khachatryan Karen S.
 * @brief the situation which contains the list of pElements.
 */
public class Situation {
	
	/**
	 * the Id of the Situation
	 */
	private String m_name;
	
	/**
	 * the local generated id of the situation
	 */
	private static int m_currentId = 0;
	
	/**
	 * @return - the next valid Situation Id.
	 */
	private Integer getNextId() {
		return m_currentId++;
	}
	
	/**
	 * The list of AR1 instances in the situation.
	 */
	private HashMap<Integer, IdGroup> m_ar1s;
	
	/**
	 * Constructor
	 * @param name - The Id of the Situation.
	 */
	public Situation(String name) {
		m_ar1s = new HashMap<Integer, IdGroup>();
		m_name = name;
		if(m_name == null) {
			m_name = new String(this.getNextId().toString());
		}
	}
	
	public String getName() {
		return m_name;
	}
	
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * Adds the given AR1 element to the Situation. 
	 */
	public void addElement(IdGroup element) throws PPITException {
		if(m_ar1s.put(element.getIdGroup(), element) != null) {
			throw new InvalidSituation("There are elements with duplicate Id Group: " + element.getIdGroup());
		}
	}
	
	public void forceAddElement(IdGroup element) throws PPITException {
		m_ar1s.put(element.getIdGroup(), element);
	}
	
	public Collection<IdGroup> getElements() {
		return m_ar1s.values();
	}
	
	/**
	 * @brief Applies the action to the situation, so the situation is changed and new situation is formed
	 * @param actionInstance instance of the action to apply on the situation
	 * @return The new situation formed after applying the action
	 */
	public Situation applyAction(ActionInstance actionInstance)
			throws PPITException {
		Situation newSituation = new Situation(m_name
				+ actionInstance.getGANode().getAbstract().getName());
		HashMap<NucleusInstance, Integer> postConditionValuesMapping = new HashMap<NucleusInstance, Integer>();
		HashMap<IdGroup, ArrayList<NucleusInstance>> idGroupToChangingInstanceMapping = new HashMap<IdGroup, ArrayList<NucleusInstance>>();
		ArrayList<PostConditionAttribute> expressitions = ((Action) actionInstance
				.getGANode().getAbstract()).getPostCondition();

		for (PostConditionAttribute postConditionAttribute : expressitions) {
			ArrayList<String> dependencies = postConditionAttribute
					.getDependencies();
			for (String dependency : dependencies) {
				InstanceBase instanceToSet = actionInstance.getInstance(dependency);
				if (instanceToSet instanceof NucleusInstance) {
					postConditionAttribute.setDependency(dependency,
							(NucleusInstance) actionInstance.getInstance(dependency));
				} else {
					System.out
							.println("can't solve one of action dependencies");
					throw new PPITException(
							"instance with the given path is not a nucleus one in the action");
				}
			}
			NucleusInstance instnaceToChange = (NucleusInstance) actionInstance
					.getInstance(postConditionAttribute.getAttributeName());
			if (idGroupToChangingInstanceMapping.get(instnaceToChange
					.getIdGroup()) == null) {
				idGroupToChangingInstanceMapping.put(
						instnaceToChange.getIdGroup(),
						new ArrayList<NucleusInstance>());
			}
			idGroupToChangingInstanceMapping.get(instnaceToChange.getIdGroup())
					.add(instnaceToChange);
			postConditionValuesMapping.put(instnaceToChange,
					postConditionAttribute.evaluate());
		}
		for (IdGroup idGroup : m_ar1s.values()) {
			if (idGroupToChangingInstanceMapping.containsKey(idGroup)) {
				IdGroup tempIdGroup = new IdGroup(idGroup.getIdGroup());
				for (NucleusInstance instance : idGroup.getElements()) {
					tempIdGroup.addPElement(instance.getType(), instance);
				}
				ArrayList<NucleusInstance> instancesToApply = idGroupToChangingInstanceMapping
						.get(idGroup);
				for (NucleusInstance nucleusInstance : instancesToApply) {
					int value = postConditionValuesMapping.get(nucleusInstance);
					tempIdGroup.forceAddPElement(
							nucleusInstance.getType(),
							new NucleusInstance(value, nucleusInstance
									.getType(), tempIdGroup, nucleusInstance
									.getGANode()));
				}
				newSituation.forceAddElement(tempIdGroup);
			} else {
				newSituation.forceAddElement(idGroup);
			}
		}
		return newSituation;
	}
	
	
//	primitive type: cordX
//	primitive type: cordY
//	primitive type: FigureType
//	primitive type: FigureColor
	// chess specific function
	public String toFen() throws PPITException {
		char[][] boardInterpretation = new char [8][8];
		String fen = "";
		for (IdGroup field : m_ar1s.values())  {
			Collection<NucleusInstance> elements = field.getElements();
			int x = 0;
			int y = 0;
			int color = 0;
			int figure = 0;
			for (NucleusInstance nucleusInstance : elements) {
				String type = nucleusInstance.getType().getType();
				if (type.equals("cordX")) {
					x = nucleusInstance.getValue();
				} else if (type.equals("cordY")) {
					y = nucleusInstance.getValue();
				} else if (type.equals("FigureType")) {
					figure = nucleusInstance.getValue();
				} else if (type.equals("FigureColor")) {
					color = nucleusInstance.getValue();
				} else {
					throw new PPITException("no specified type of nucleus instance for chess");
				}
			}
			boardInterpretation[8 - y][x - 1] = identifyFigure(figure, color);
		}
		
		String line = "";
		int lineNumber = 0;
		for (char[] chars : boardInterpretation) {
			if (lineNumber++ != 0) {
				line = "/";
			}
			int counter = 0;
			for (char c : chars) {
				if (c == '0') {
					++counter;
				} else {
					if (counter > 0) {
						line += counter;
						counter = 0;
					}
					line += c;
				}
			}
			if (counter > 0) {
				line += counter;
			}
			fen += line;
		}
		return fen;
	}
	
	private char identifyFigure(int figure, int color) {
		if (color == 0) {
			return '0';
		}
		char figureChar = '0';
		switch (figure) {
		case 1:
			figureChar='p';
			break;
		// NOTE: the Solver's chess convention (2016 User Guide App. B, the board
		// sprites in ChessSituation.css and the legacy abstracts) is
		// Bishop(2), Knight(3) -- NOT the FEN letter order. Map accordingly.
		case 2:
			figureChar='b';
			break;
		case 3:
			figureChar='n';
			break;
		case 4:
			figureChar='r';
			break;
		case 5:
			figureChar='q';
			break;
		case 6:
			figureChar='k';
			break;
		default:
			break;
		}
		if (color == 1) {
			figureChar = Character.toUpperCase(figureChar);
		}
		return figureChar;
	}
}
