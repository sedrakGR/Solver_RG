package org.ppit.test.situation;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ppit.core.brain.gaNode.listener.GAListenerCollecter;
import org.ppit.core.brain.instance.IdGroupContainer;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.percept.Situation;
import org.ppit.core.percept.SituationManager;
import org.ppit.test.brain.TestVirtualActivation;
import org.ppit.util.exception.PPITException;

public class TestDemo {

	@Test
	public void testDemoMatching() {
		
		TestVirtualActivation initializer = new TestVirtualActivation();;
		initializer.testAbstractActivation();
		
//		ConceptManager c_manager = ConceptManager.getInstance();
		SituationManager s_manager = SituationManager.getInstance();
		CognitionManager m_cognition = CognitionManager.getInstance();
		
		/**
		 * P = Pawn 1, K = King 6, Q = Queen 5, R = Rook 4, B = Bishop 2, N = knight 3, D = Dummy 0
		 * W = White 1, B = Black 2, D = Dummy 0
		 */
		
		String board =
		 "8 DD DD DD DD DD DD DD DD " +
		 "7 DD DD DD DD DD DD DD DD " +
		 "6 DD DD DD DD DD DD DD DD " +
		 "5 DD DD DD DD DD DD DD DD " +
		 "4 DD DD DD BK DD DD DD DD " +
		 "3 DD WN DD DD WP DD DD DD " +
		 "2 WP WP DD DD DD DD DD DD " +
		 "1 DD DD DD WK DD DD DD DD "
		 ;
		 /**    1  2  3  4  5  6  7  8
		 *    
		 * The Matrix bellow are reversed a little (to match the index to the position in the above picture.
		 */
		String concept = "King";
		
		int[][] fc = new int[8][8];
		int[][] ft = new int[8][8];
		
		String[][] original = new String[8][8];
		
		String[] line = board.split(" ");
		for (int i = 0; i < line.length; ++i) {
			String string = line[i];
			if (string.length() != 2) {
				continue;
			}
			try {
				fc[7 - i / 9][i % 9 - 1] = getColorValue(string.charAt(0));
				ft[7 - i / 9][i % 9 - 1] = getTypeValue(string.charAt(1));
			} catch (PPITException e) {
				e.printStackTrace();
				return;
			}
			original[7 - i / 9][i % 9 - 1] = string.toLowerCase();
		}
		
		String xName = ("cordX");
		String yName = ("cordY");
		String fType = ("FigureType");
		String fColor = ("FigureColor");

		int groupId = 1;
		String situationString = new String ("{\"name\":\"sit1\",\"elements\":[");
		for(int y = 0; y < 8; ++y) {
			for (int x = 0; x < 8; ++x, ++groupId) {
				// I officially declare that Java is a sun of a beach!
				// This shit, threats 1 in a "x + 1" expression in a string not as a value :(
				// So, "()" shall be used! Fuck it!
				situationString += 
				"{\"groupid\":\"" + groupId + "\",\"instances\":[" +
				"{\"type\":\"" + xName + "\",\"value\":\"" + (x + 1) + "\"}," +
				"{\"type\":\"" + yName + "\",\"value\":\"" + (y + 1) + "\"}," +
				"{\"type\":\"" + fType + "\",\"value\":\"" + ft[y][x] + "\"}," +
				"{\"type\":\"" + fColor + "\",\"value\":\"" + fc[y][x] + "\"}" +
				"]}";
				
				if(!(x == 7 && y == 7)) {
					situationString += ",";
				}
			}
		}
		situationString += "]}";
		
		Situation stu1 = null;
		try {
			JSONObject tmpJSON = new JSONObject(situationString);
			stu1 = s_manager.createSituation(tmpJSON);
						
			assertEquals(64, stu1.getElements().size());
			
			m_cognition.processSituation(stu1);
			
			assertEquals(64, m_cognition.getWM().getActivatedInstances("Field").getActiveInstanceCount());
			//assertEquals(6, m_cognition.getWM().getActivatedInstances("Figure").getActiveInstanceCount());
			
			GAListenerCollecter listener = m_cognition.getWM().getActivatedInstances(concept);

			int i = 0;
			for(InstanceBase ib: listener.getInstaces()){
				System.out.println("\nPrinting the " + (i++) + "-th instance of " + concept + " abstract.");
				
				IdGroupContainer idgContainer = ib.getIdGroupsInInstance();
				printMatch(original, idgContainer);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			fail(e.getMessage());
			return;
		} catch (PPITException e) {
			e.printStackTrace();
			fail(e.getMessage());
			return;
		}
	}

	private int getColorValue(char color) throws PPITException {
		switch (color) {
		case 'D':
			return 0;
		case 'W':
			return 1;
		case 'B':
			return 2;
		default:
			throw new PPITException("Not supported color string. Expecte D, W, B, but received: " + color);
		}
	}
	
	private int getTypeValue(char type) throws PPITException {
		switch (type) {
		case 'D':
			return 0;
		case 'P':
			return 1;
		case 'B':
			return 2;
		case 'N':
			return 3;
		case 'R':
			return 4;
		case 'Q':
			return 5;
		case 'K':
			return 6;
		default:
			throw new PPITException("Not supported type string. Expecte D, P, K, N, R, Q, B, but received: " + type);
		}
	}
	
	private char getColor(int color) throws PPITException {
		switch (color) {
		case 0:
			return 'D';
		case 1:
			return 'W';
		case 2:
			return 'B';
		default:
			throw new PPITException("Not supported color value. Expecte 0-2, but received: " + color);
		}
	}
	
	private char getType(int type) throws PPITException {
		switch (type) {
		case 0:
			return 'D';
		case 1:
			return 'P';
		case 2:
			return 'B';
		case 3:
			return 'N';
		case 4:
			return 'R';
		case 5:
			return 'Q';
		case 6:
			return 'K';
		default:
			throw new PPITException("Not supported color value. Expecte 0-6, but received: " + type);
		}
	}

	private void printMatch(String [][] original, IdGroupContainer idgContainer) throws PPITException {
		String [][] local = new String[8][8];
		for(int i = 0; i < 8; ++  i)
			for (int j = 0; j < 8; ++j)
				local[i][j] = original[i][j];
		
		for(IdGroup idg: idgContainer.getIdGroups()) {
			int cordX = 0, cordY = 0, color = 0, type = 0;
			for(NucleusInstance ni : idg.getElements()) {
				if (ni.getType().getType().equals("FigureColor")) {
					color = ni.getValue();
				}
				if (ni.getType().getType().equals("cordX")) {
					cordX = ni.getValue();
				}
				if (ni.getType().getType().equals("cordY")) {
					cordY = ni.getValue();
				}
				if (ni.getType().getType().equals("FigureType")) {
					type = ni.getValue();
				}
			}
			char colorChar = getColor(color); 
			char typeChar = getType(type);
			
			local[cordY - 1][cordX - 1] = "" + colorChar + typeChar;
		}
		for (int i = 7; i >= 0; --i) {
			for (int j = 0; j < 8; ++j) {
				System.out.print(local[i][j] + ' ');
			}
			System.out.println();
		}
	}
}
