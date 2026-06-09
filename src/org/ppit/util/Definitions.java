package org.ppit.util;

/**
 * 
 * @author SedrakG
 * @description this class is used for definitions
 *
 */
public class Definitions {
	
	// The definition for Attribute separator "."
	public static final String subattributeAccessor = ".";
	// The definition for Attribute separator Char "."
	public static final int subattributeAccessorChar = '.';
	//Empty string definition
	public static final String emptyString = "";
	
	/**
	 * Definitions for Rules 
	 */
	// Wildcard rule
	public static final String ruleWildcardValue = "*"; // expression value
	//Abstract rule
	public static final String ruleAbstractValue = "?"; // expression value
	// "Equals To"
	public static final String equalsOperator = "="; // operator also used as operator for abstract rule
	// "Greater than"
	public static final String greaterOperator = ">";
	// "Greater than or equals to"
	public static final String greaterEqualsOperator = ">=";
	// "IN"
	public static final String inOperator = "IN";
	// "Less than"
	public static final String lessOperator = "<";
	// "Less or equals to"
	public static final String lessEqualsOperator = "<=";
	// "Not equals to"
	public static final String notEqualsOperator = "!=";
	// "IS" — categorical membership against a classifier vocabulary (name-valued equality)
	public static final String isOperator = "IS";
	// "NOT IS" — categorical non-membership against a classifier vocabulary
	public static final String notIsOperator = "NOT IS";
	//operator - this is used in JSONs to get the operator for rules or etc...
	//TODO: NOTE: for now this should not be used in Actions because we suppose to have only "=" operator for them
	public static final String operatorJSON = "oper";
	// JSON key on a value attribute that names the classifier/vocabulary that owns its symbols
	public static final String classifierIdJSON = "classifier";
	//minValue
	public static final String minValueJSON = "minValue";
	//maxValue
	public static final String maxValueJSON = "maxValue";
	/**
	 * END OF DEFINITIONS OF RULES
	 */
	
	// 
	public static final String setElementName = "element";
	
	public static final String constantAttributeValueJSON = "const";
	
	public static final String continuousAttributeValueJSON = "-";
	
	/**
	 * Definitions used in interface and backed - JSON naming definitions
	 */
	//name - this is used in JSONs to get/set the name of the concept or action or situation
	public static final String nameJSON = "name";
	//parent - this is used in JSONs to get/set the parent name of the concept or action or maybe even situation
	public static final String parentJSON = "parent";
	//sideIndicator indicator of the side to act
	public static final String sideIndicatorJSON = "sideIndicator";
	//postConditionList - this is used in JSONs to get the list of postcondition attributes of action
	public static final String postConditionListJSON = "expressions";
	//postCondExp - this is used in JSONs to get the expression of a postcondition attribute of an action
	public static final String postCondExpJSON = "right";
	//postCondName - this is used in JSONs to get the name of a postcondition attribute of an action
	public static final String postCondNameJSON = "left";
	//preCondName - this is used in JSONs to get the name of a postcondition attribute of an action
	public static final String preCondConceptJSON = "precondition";
	//setElementCount - this represents the count of the elements in the Set.
	public static final String setElementCountJSON = "count";
	
	public static final String setMandatoryAttributes = "setManAttr";
	public static final String setAdditionalAttributes = "setAddAttr";
	//value - represents the value part of the attributes., or situation element nucleus instance value
	public static final String valueJSON = "value";
	// identifier of is_key json key, which indicates if the nucleus abstract is key for side definition of players
	public static final String keyJSON = "is_key";
	// value is 1, it indicates it's true, as we don't collect value 'true'
	public static final String trueJSON = "1";
	// value - only attribute of nucleus abstract, this is array, because it was defined before,
	//and now, as all of us know, we don't want to make changes in interface
	public static final String valueAttributeJSON = "nucleusConceptValueAttrs";
	// cr1
	public static final String cr1JSON = "cr1";
	public static final String negatedJSON = "negated";
	// level attribute, indicates the level of concept, the root is 0 level, and it's attributes are 1 level.
	// It is used only in DB, in interface it's not used
	public static final String levelJSON = "level";
	// type of the concept - 'c' - composite, etc. or situation element nucleus instance type
	public static final String typeJSON = "type";
	// name of set abstract's attribute composite abstract
	public static final String setAttributeJSON = "attribute";
	/**
	 * END OF DEFINITIONS OF CONCEPTS AND ACTIONS
	 */

	/**
	 * Definitions used in part connected with DB (db package mostly) 
	 */
	// mainDBName this is the name of main DB
	public static final String mainDBName = "concepts";
	// testDBName this is the name of test DB
	public static final String testDBName = "test";
	/**
	 * END OF DEFINITIONS OF DB
	 */
	
	/**
	 * DEFINITIONS OF SITUATIONS. 
	 * The format of situation from Interface to Back-end is:
	 * {"name":"$sit_name", "elements":
	 * 		[
	 * 		{"groupid":"$id(number)","instances":
	 * 			[
	 * 			{"type":"$nucleus_type", "value":"$the value(number)"}, 
	 * 			...
	 * 			{"type":"$nucleus_type", "value":"$the value(number)"}
	 * 			]
	 * 		},
	 * 		...
	 * 		{"groupid":"$id(number)","instances":
	 * 			[
	 * 			{"type":"$nucleus_type", "value":"$the value(number)"}, 
	 * 			...
	 * 			{"type":"$nucleus_type", "value":"$the value(number)"}
	 * 			]
	 * 		}
	 * 		]
	 * }
	 */
	public static final String situationElementsJSON = "elements";
	public static final String situationElementIdJSON = "groupid";
	public static final String situationElementInstancesJSON = "instances";
	public static final String situationActiveAbstractsJSON = "actives";
	
	/**
	 * The format from Back-end to Interface is:
	 * {"instanceid":"$instance_id","groupids":
	 * 		[
	 * 			{"id":"$groupId"},
	 * 			...
	 * 			{"id":"$groupId"}
	 * 		]
	 * }
	 */
	public static final String situationInstanceId = "instanceid";
	public static final String situationInstanceGroupIds = "groupids";
	public static final String situationInstanceGroupId = "id";
	
	/**
	 * END OF DEFINITIONS OF SITUATIONS
	 */
	
	/** 
	 * Definitions for Visualization.
	 */
	public static final String visualizationAttributeGANodeType = "ganodetype";
	public static final String visualizationAttributeGAEdgeType = "gaedgetype";

}
