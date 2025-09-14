package org.ppit.core.concept.expression.analyze;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @author Emil
 * @date 24.11.2011
 *
 */
public class InfixToPostfix {
	private Stack<String> theStack;	
	private ArrayList<String> output = null;	
	
	/**
	 * Constructor
	 *   
	 */
	public InfixToPostfix() {	    
	    output = new ArrayList<String>();	    
	}
	
	/**
	 * Converts ArrayList which consists of infix expression tokens, 
	 * into ArrayList, which consists of postfix expression tokens.
	 * For instance, converts a,+,b to a,b,+
	 * 
	 * @param ArrayList of infix expression tokens
	 * @return ArrayList of postfix expression tokens 		
	 */
	public ArrayList<String> doTrans(ArrayList<String> input) {
	    theStack = new Stack<String>();	    
		for (int j = 0; j < input.size(); j++) {
	      String ch = input.get(j);	      
	      if(ch == "-" || ch == "+"){	// it’s + or -      	      
	        gotOper(ch, 1);				// go pop operators: (precedence 1)
	      } else
	      if(ch == "*" || ch == "/"){	// it’s * or /
	    	  gotOper(ch, 2); 			
	      } else
	      if(ch == "("){
	    	  theStack.push(ch);
	      } else
    	  if(ch == ")"){
    		  gotParen(ch);
    	  } else{	       
	        output.add(ch);	        
	      }
	    }
	    while (!theStack.isEmpty()) {
	      output.add(theStack.pop());
	    }	    
	    return output; 
	  }

	  public void gotOper(String opThis, int prec1) {
	    while (!theStack.isEmpty()) {
	    String opTop = theStack.pop();
	      if (opTop == "(") {
	        theStack.push(opTop);
	        break;
	      }
	      else {
	        int prec2;
	        if (opTop == "+" || opTop == "-")
	          prec2 = 1;
	        else
	          prec2 = 2;
	        if (prec2 < prec1) 
	        { 
	          theStack.push(opTop); 
	          break;
	        } else	           
	          output.add(opTop);
	      }
	    }
	    theStack.push(opThis);
	  }

	  public void gotParen(String ch){ 
	    while (!theStack.isEmpty()) {
	    	String chx = theStack.pop();
	    	if (chx == "(") 
	    		break; 
	    	else
	    		output.add(chx);
	    }
	  }
}
