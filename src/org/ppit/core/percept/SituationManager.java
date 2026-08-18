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
	
	/**
	 * @brief Build a Situation from a chess-board image by shelling out to
	 *        Solver_train/predict_board.py and mapping the per-cell JSON to
	 *        IdGroups of NucleusInstances. Mirrors createSituationFromFen but
	 *        with NN inference instead of a FEN string as the source of truth.
	 *
	 *        The predict_board.py output is documented in that file's header
	 *        comment. Empty cells (occupancy heuristic) emit type="empty",
	 *        color="none" which we map to integer 0 for both — matching
	 *        Situation.identifyFigure()'s convention.
	 *
	 * @param imagePath path to the source image on disk (any format Pillow
	 *                  can read).
	 * @param sitName   optional situation name; if null the image stem is used.
	 * @return the created Situation, already registered in the library.
	 */
	public Situation createSituationFromImage(java.io.File imagePath, String sitName) throws PPITException {
		if (imagePath == null || !imagePath.isFile()) {
			throw new PPITException("Image file not found: " + imagePath);
		}

		// 1. Resolve paths to the python script + checkpoints. We default to
		// SOLVER_TRAIN_DIR env var, then /opt/solver/Solver_train (the Docker
		// layout), then the working-dir-relative Solver_train.
		java.io.File trainDir = resolveSolverTrainDir();
		java.io.File predictScript = new java.io.File(trainDir, "predict_board.py");
		java.io.File modelsDir     = new java.io.File(trainDir, "artifacts");
		if (!predictScript.isFile()) {
			throw new PPITException("predict_board.py not found at " + predictScript.getAbsolutePath()
					+ ". Set SOLVER_TRAIN_DIR or place Solver_train next to the webapp.");
		}

		// 2. Spawn the subprocess.
		String python = java.util.Optional.ofNullable(System.getenv("SOLVER_PYTHON"))
				.orElse("python3");
		java.util.List<String> cmd = new java.util.ArrayList<String>();
		cmd.add(python);
		cmd.add(predictScript.getAbsolutePath());
		cmd.add("--image");        cmd.add(imagePath.getAbsolutePath());
		cmd.add("--models-dir");   cmd.add(modelsDir.getAbsolutePath());
		cmd.add("--output");       cmd.add("-");
		if (sitName != null && !sitName.isEmpty()) {
			cmd.add("--name"); cmd.add(sitName);
		}

		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(false);
		String stdout, stderr;
		int exit;
		try {
			Process p = pb.start();
			stdout = readStream(p.getInputStream());
			stderr = readStream(p.getErrorStream());
			exit = p.waitFor();
		} catch (Exception e) {
			throw new PPITException("Failed to invoke predict_board.py: " + e.getMessage());
		}
		if (exit != 0) {
			throw new PPITException("predict_board.py exited " + exit + ":\n" + stderr);
		}

		// 3. Parse JSON output.
		JSONObject json;
		try {
			json = new JSONObject(stdout);
		} catch (JSONException e) {
			throw new PPITException("predict_board.py emitted invalid JSON: "
					+ e.getMessage() + "\nstdout was:\n" + stdout
					+ "\nstderr was:\n" + stderr);
		}

		// 4. Build the Situation. Same convention as createSituationFromFen.
		String name = json.optString("name", "");
		if (name.isEmpty()) name = (sitName != null) ? sitName : imagePath.getName();
		Situation situation = new Situation(name);

		PrimitiveType cordXType        = ConceptManager.getInstance().getPrimitiveType("cordX");
		PrimitiveType cordYType        = ConceptManager.getInstance().getPrimitiveType("cordY");
		PrimitiveType figureColorType  = ConceptManager.getInstance().getPrimitiveType("FigureColor");
		PrimitiveType figureTypeType   = ConceptManager.getInstance().getPrimitiveType("FigureType");
		if (cordXType == null || cordYType == null || figureColorType == null || figureTypeType == null) {
			throw new PPITException("Required chess nuclei (cordX/cordY/FigureColor/FigureType) "
					+ "are not registered. Define them in the nucleus editor first.");
		}

		org.json.JSONArray cells;
		try { cells = json.getJSONArray("cells"); }
		catch (JSONException e) { throw new PPITException("predict_board.py JSON missing 'cells' array."); }

		int idCounter = 1;
		for (int i = 0; i < cells.length(); ++i) {
			JSONObject cell;
			try { cell = cells.getJSONObject(i); }
			catch (JSONException e) { throw new PPITException("Bad cells[" + i + "] entry: " + e.getMessage()); }

			int cordX, cordY, figureInt, colorInt;
			try {
				cordX = cell.getInt("x");
				cordY = cell.getInt("y");
				String typeName  = cell.getJSONObject("type").getString("name");
				String colorName = cell.getJSONObject("color").getString("name");
				figureInt = chessFigureCodeFromName(typeName);
				colorInt  = chessColorCodeFromName(colorName);
			} catch (JSONException e) {
				throw new PPITException("Malformed cell[" + i + "]: " + e.getMessage());
			}

			IdGroup idgroup = new IdGroup(idCounter++);
			try {
				new NucleusInstance(cordX,     cordXType,       idgroup, null);
				new NucleusInstance(cordY,     cordYType,       idgroup, null);
				new NucleusInstance(colorInt,  figureColorType, idgroup, null);
				new NucleusInstance(figureInt, figureTypeType,  idgroup, null);
				situation.addElement(idgroup);
			} catch (BrokenCR1 e1) {
				throw new PPITException("CR1 broken on cell[" + i + "]: " + e1.getMessage());
			}
		}

		// 5. Register in the library so existing actions (processSituation, etc.)
		// can find it by name.
		m_library.addSituation(situation);
		m_lastSituationName = situation.getName();
		return situation;
	}

	/** Convenience wrapper. */
	public Situation createSituationFromImage(java.io.File imagePath) throws PPITException {
		return createSituationFromImage(imagePath, null);
	}

	private static java.io.File resolveSolverTrainDir() {
		String env = System.getenv("SOLVER_TRAIN_DIR");
		if (env != null && !env.isEmpty()) {
			java.io.File f = new java.io.File(env);
			if (f.isDirectory()) return f;
		}
		java.io.File docker = new java.io.File("/opt/solver/Solver_train");
		if (docker.isDirectory()) return docker;
		return new java.io.File("Solver_train");
	}

	private static String readStream(java.io.InputStream in) throws java.io.IOException {
		java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
		byte[] chunk = new byte[4096];
		int n;
		while ((n = in.read(chunk)) > 0) buf.write(chunk, 0, n);
		return buf.toString("UTF-8");
	}

	/** Map a piece-type class name to the chess integer Solver_RG uses
	 *  (2016 App. B convention, shared with the board sprites:
	 *  0=Dummy, 1=Pawn, 2=Bishop, 3=Knight, 4=Rook, 5=Queen, 6=King). */
	private static int chessFigureCodeFromName(String name) {
		if (name == null) return 0;
		switch (name.toLowerCase()) {
			case "empty":  return 0;
			case "pawn":   return 1;
			case "bishop": return 2;
			case "knight": return 3;
			case "rook":   return 4;
			case "queen":  return 5;
			case "king":   return 6;
			default:       return 0;
		}
	}

	/** Map a color class name to the chess integer Solver_RG uses
	 *  (0=none/empty, 1=white, 2=black). */
	private static int chessColorCodeFromName(String name) {
		if (name == null) return 0;
		switch (name.toLowerCase()) {
			case "none":  case "empty": return 0;
			case "white": return 1;
			case "black": return 2;
			default:      return 0;
		}
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
