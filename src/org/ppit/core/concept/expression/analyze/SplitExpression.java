package org.ppit.core.concept.expression.analyze;

import java.util.ArrayList;

/**
 *  @author Emil
 *  @date 24.11.2011 
 *
 */
public class SplitExpression {
	private String expression;
	private ArrayList<String> tokens = null;
	
	/**
	 * @description constructor
	 * @param expression: infix expression, for instance a+b
	 */
	public SplitExpression(String expression) {
		this.expression = expression;
		tokens = new ArrayList<String>();
	}
	
	/**
	 * function separate infix expression into tokens, 
	 * and returns ArrayList of tokens. In case of a+b expression, 
	 * the function returns this list a,+,b
	 *  
	 * @return {@link ArrayList}
	 */
	public ArrayList<String> separateString(){
		String operand = "";
		for(int i = 0; i < this.expression.length(); i ++){
			char ch = this.expression.charAt(i);
			if(ch == ' ' || ch == '\t'){
				continue;
			}
			if(ch == '+'){
				addToken("+", operand);
				operand = "";
			} else
			if(ch == '-'){				
				addToken("-", operand);
				operand = "";
			} else
			if(ch == '*'){
				addToken("*", operand);
				operand = "";
			} else
			if(ch == '/'){
				addToken("/", operand);
				operand = "";
			}  else
			if(ch == '('){
				addToken("(", operand);
				operand = "";
			}  else
			if(ch == ')'){
				addToken(")", operand);
				operand = "";
			} else{
				operand += ch;
			}
		}		
		if(operand != ""){
			tokens.add(operand);
		}
		return tokens;
	}
	
	private void addToken(String token, String operand){
		if(operand != ""){
			tokens.add(operand);			
		}
		tokens.add(token);
	}
}
