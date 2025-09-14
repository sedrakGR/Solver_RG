package org.ppit.actions.chess;

import java.util.ArrayList;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Emil
 *
 */

public class InitChessAction extends ActionSupport{
	private ArrayList<String> numbers = null;
	private ArrayList<String> letters = null;
	public InitChessAction() {
		super();
	}
	
	public ArrayList<String> getNumbers() {
		return numbers;
	}

	public void setNumbers(ArrayList<String> numbers) {
		this.numbers = numbers;
	}

	public ArrayList<String> getLetters() {
		return letters;
	}

	public void setLetters(ArrayList<String> letters) {
		this.letters = letters;
	}
	
	public String execute(){
		numbers = new ArrayList<String>(){{
			add("8");
			add("7");
			add("6");
			add("5");
			add("4");
			add("3");
			add("2");
			add("1");
		}};
		
		letters = new ArrayList<String>(){{
			add("a");
			add("b");
			add("c");
			add("d");
			add("e");
			add("f");
			add("g");
			add("h");
		}};
		return SUCCESS;
	}
}
