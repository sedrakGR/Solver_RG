package org.ppit.core.brain.instance.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.instance.IdGroupContainer;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.rules.RuleIn;
import org.ppit.util.Definitions;

public class SetInstance extends InstanceBase {
	/**
	 * @brief List of active instances of  set attribute.
	 */
	private ArrayList<AbstractInstance> m_elements = new ArrayList<AbstractInstance>();
	private RuleIn m_count = null;
	//private 
	private int m_minValue = 0;
	private int m_maxValue = 0;
	
	private HashMap<String, NucleusInstance> m_constantInstances = new HashMap<String, NucleusInstance>();
	
	private String m_continuous = "";
	
	
	public SetInstance(GANode type, RuleIn count) {
		super(type);
		m_count = count;
	}
	
	public SetInstance clone()
	{
		SetInstance clone;
		clone = (SetInstance)super.clone();
		
		// TODO add the holder class to attributes (InstanceBase).
		// Don't forget to remove the holder later, when destroying the instance.
		
		clone.m_elements = new ArrayList<AbstractInstance>();
		clone.m_elements.addAll(m_elements);
		
		if(m_count != null) {
			clone.m_count = m_count.clone();
		}
		clone.m_minValue = m_minValue;
		clone.m_maxValue = m_maxValue;
		clone.m_constantInstances = new HashMap<String, NucleusInstance>(); 
		for (String instancePath : m_constantInstances.keySet()) {
			clone.m_constantInstances.put(instancePath, (NucleusInstance)m_constantInstances.get(instancePath));
		}
		return clone;
	}

	public int getCount() {
		return m_elements.size();
	}
	
	public ArrayList<AbstractInstance> getElements() {
		return m_elements;
	}
	
	public InstanceBase getInstance(String path)
	{
		if(path.isEmpty()) {
			return this;
		}
		if(m_elements.isEmpty()) {
			return null;
		}
		
		// For now always use the first attribute. instead of cases when selected top or bottom
		int attid = 0;
		
		if (path.contains(".TOP")) {
			String attributeName = path.substring(0, path.indexOf(".TOP"));
			attributeName = attributeName.substring(attributeName.indexOf(Definitions.setElementName) +
					Definitions.setElementName.length() + 1);
			if (attributeName == null || attributeName.isEmpty()) {
				System.out.println("Error: wrong access of TOP element");
				return null;
			}
			if (!m_continuous.equals(attributeName)) {
				System.out.println("Error: not continuous TOP is being searched");
				return null;
			}
			int valueMax = Integer.MIN_VALUE;
			NucleusInstance topInstance = null;
			for (AbstractInstance instance : m_elements) {
				NucleusInstance current = (NucleusInstance)instance.getInstance(attributeName);
				int value = current.getValue();
				if (value > valueMax) {
					valueMax = value;
					topInstance = current;
				}
			}
			return topInstance;
		} else if (path.contains(".BOTTOM")) {
			String attributeName = path.substring(0, path.indexOf(".BOTTOM"));
			attributeName = attributeName.substring(attributeName.indexOf(Definitions.setElementName) +
					Definitions.setElementName.length() + 1);
			if (attributeName == null || attributeName.isEmpty()) {
				System.out.println("Error: wrong access of BOTTOM element");
			}
			if (!m_continuous.equals(attributeName)) {
				System.out.println("Error: not continuous BOTTOM is being searched");
			}
			int valueMin = Integer.MAX_VALUE;
			NucleusInstance bottomInstance = null;
			for (AbstractInstance instance : m_elements) {
				NucleusInstance current = (NucleusInstance)instance.getInstance(attributeName);
				int value = current.getValue();
				if (value < valueMin) {
					valueMin = value;
					bottomInstance = current;
				}
			}
			return bottomInstance;
		} else {

			int rootEnd = path.indexOf(Definitions.subattributeAccessor);
			if (rootEnd != -1) {
				String root = path.substring(0, rootEnd);

				if (root.equals(Definitions.setElementName)) {
					return m_elements.get(attid).getInstance(
							path.substring(rootEnd + 1));
				}
			}
			return m_elements.get(attid).getInstance(path);
		}
	}

	public int activateElement(AbstractInstance instance, ArrayList<String> constants, String continuous) {
		if (continuous != null && !continuous.isEmpty()) {
			if (m_continuous == null || m_continuous.isEmpty()) {
				m_continuous = continuous;
			} else if (!m_continuous.equals(continuous)) {
				System.out.println("ERROR: continuous is different");
				return S_FAIL;
			}
		}
		//TODO: make sure constants and intancePath set match
		if (m_constantInstances.size() != constants.size()) {
			for (String constant : constants) {
				String attributeName = constant.substring(constant.indexOf('.') + 1);
				if ((NucleusInstance)instance.getInstance(constant) == null) {
					System.out.println("not found element**************************************");
				}
				m_constantInstances.put(constant, (NucleusInstance)instance.getInstance(constant));
			}
		} else {
			for (String instancePath : m_constantInstances.keySet()) {
				if (m_constantInstances.get(instancePath).getValue() != ((NucleusInstance) instance
						.getInstance(instancePath)).getValue()) {
					return S_FAIL;
				}
			}
		}
		m_elements.add(instance);
		int size = m_elements.size();
		
		// for handling just assume there are only 1 constants.
		// basically this is the main case, at least for chess
		
		
		int minValue = Integer.MAX_VALUE;
		int maxValue = Integer.MIN_VALUE;
		HashSet<Integer> values = new HashSet<Integer>();
		if (!continuous.isEmpty()) {
			for (AbstractInstance element : m_elements) {
				int value = ((NucleusInstance)element.getInstance(continuous)).getValue();
				if (value < minValue) {
					minValue = value;
				}
				if (value > maxValue) {
					maxValue = value;
				}
				if (values.contains(value)) {
					return S_FAIL;
				}
				values.add(value);
				
			}
			
			int maxRuleState = m_count.check_max(size);
			if (maxRuleState == RuleIn.RI_FAIL) {
				return S_FAIL;
			}
			
			if (m_count.check_max(maxValue - minValue) == RuleIn.RI_FAIL) {
				// too far from each other
				return S_FAIL;
			}
			if (m_count.check_min(size) != RuleIn.RI_FAIL) {
				for (int i = minValue; i <= maxValue; ++i) {
					if (!values.contains(i)) {
						return S_PENDING;
					}
				}
				if (maxRuleState == RuleIn.RI_U_LIM) {
					return S_FIRE_REMOVE;
				} else if (maxRuleState == RuleIn.RI_OK) {
					return S_FIRE_KEEP;
				}
			} else {
				return S_PENDING;
			}
			// should not rich here
			return S_FAIL;
		}
		
		int status;
		// If there are not enough number of elements then don't process further.
		if (m_count.check_min(size) == RuleIn.RI_FAIL) {
			return S_PENDING;
		}

		// If there are enough number of elements (or we don't know the lower bound)
		// Then, if upper bound is reached Fire and Remove, 
		// Otherwise, Fire and keep the instance.
		switch (m_count.check_max(size)) {
		case RuleIn.RI_U_LIM:
			return S_FIRE_REMOVE;
		case RuleIn.RI_FAIL:
			return S_FAIL;
		default:
			return S_FIRE_KEEP;
		}
	}	
	
	public static final int S_FIRE_REMOVE = 1;	// The Instance is ready and full.
	public static final int S_FIRE_KEEP = 2; // The instance is ready
	public static final int S_PENDING = 3; // There is not satisfied Rule.
	public static final int S_FAIL = 4;	// The Instance is invalid.

	@Override
	public IdGroupContainer getIdGroupsInInstance() {
		IdGroupContainer idGroups = new IdGroupContainer();
		for(AbstractInstance element: m_elements) {
			idGroups.addIdGroup(element.getIdGroupsInInstance());
		}
		return idGroups;
	}
}
