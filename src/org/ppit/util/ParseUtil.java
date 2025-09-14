package org.ppit.util;

import java.util.ArrayList;

/**
 * 
 * @author Karen Khachatryan S.
 * @detailed The utility class which will be used to hold parsing utilities.
 *
 */

public class ParseUtil {
	/**
	 * @brief The separator used in expressions.
	 */
	public static String separator = ",";

	/**
	 * @brief Parses the passed expression using ',' as separator.
	 * @param expression the input expression
	 * @return the list of distinct expressions.
	 */
	public static ArrayList<String> splitExpression(String expression)
	{
		ArrayList<String> expressionList = new ArrayList<String>();
		// Split it into pieces of expression, because this attribute can have a value like this
		// '= 1, 2, 5, p1.x'
		// Temporary array of strings to keep the split values
		String[] tempExpressions = expression.split(separator);
		for (String ex: tempExpressions) {
			expressionList.add(ex);
		}
		return expressionList;
	}
	/**
	 * @brief Composes the expression from the group of subexpressions.
	 * @param exprList
	 * @return
	 */
	public static String composeExpression(ArrayList<String> exprList)
	{
		String expression = new String();
		for(String ex: exprList) {
			if(!expression.equals("")){
				expression += separator;
			}
			expression += ex;
		}
		return expression;
	} 
}
