package org.ppit.core.concept.primitive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.util.Logger;
import org.ppit.util.exception.InvalidExpression;

/**
 * Process-wide registry of {@link Vocabulary} objects loaded from a drop
 * folder of NN classifier manifests. Manifest format is documented in
 * NN_CLASSIFIER_INTEGRATION_PLAN.md §6.2.
 *
 * Discovery:
 *   1. Directory given by system property "solver.classifiers.dir"
 *   2. Directory given by environment variable SOLVER_CLASSIFIERS_DIR
 *   3. "./classifiers" under the process working directory
 * Each subfolder containing a {@code manifest.json} is registered. Bad
 * manifests are logged and skipped; they do not block startup.
 *
 * The registry also supports {@link #registerForTest(Vocabulary)} so tests
 * can seed vocabularies without touching the filesystem.
 */
public final class VocabularyRegistry {

	private static VocabularyRegistry s_instance = null;

	public static synchronized VocabularyRegistry getInstance() {
		if (s_instance == null) {
			s_instance = new VocabularyRegistry();
			s_instance.loadFromDefaultLocation();
		}
		return s_instance;
	}

	/** Visible for tests — resets singleton state. */
	public static synchronized void resetForTest() {
		s_instance = new VocabularyRegistry();
	}

	/** Id -> Vocabulary. */
	private final HashMap<String, Vocabulary> m_byId = new HashMap<String, Vocabulary>();

	/** suggestedPrimitiveTypeName -> Vocabulary (best-effort hint lookup). */
	private final HashMap<String, Vocabulary> m_bySuggestedType = new HashMap<String, Vocabulary>();

	private VocabularyRegistry() {}

	public synchronized Vocabulary getById(String id) {
		return m_byId.get(id);
	}

	public synchronized Vocabulary getBySuggestedPrimitiveTypeName(String name) {
		return m_bySuggestedType.get(name);
	}

	public synchronized Collection<Vocabulary> listAll() {
		return Collections.unmodifiableCollection(new ArrayList<Vocabulary>(m_byId.values()));
	}

	public synchronized void registerForTest(Vocabulary v) {
		register(v);
	}

	private void register(Vocabulary v) {
		m_byId.put(v.getId(), v);
		String suggested = v.getSuggestedPrimitiveTypeName();
		if (suggested != null && !suggested.isEmpty()) {
			m_bySuggestedType.put(suggested, v);
		}
	}

	/**
	 * Scans the first existing candidate directory and loads every subfolder's
	 * {@code manifest.json}. Failures are logged and skipped.
	 */
	public synchronized void loadFromDefaultLocation() {
		File root = resolveClassifiersDir();
		if (root == null) {
			Logger.getInstance().log(Logger.INFO,
					"VocabularyRegistry: no classifiers directory found; registry is empty.");
			return;
		}
		loadFromDirectory(root);
	}

	public synchronized void loadFromDirectory(File root) {
		if (root == null || !root.isDirectory()) {
			return;
		}
		File[] children = root.listFiles();
		if (children == null) {
			return;
		}
		for (File child : children) {
			if (!child.isDirectory()) {
				continue;
			}
			File manifest = new File(child, "manifest.json");
			if (!manifest.isFile()) {
				continue;
			}
			try {
				Vocabulary v = loadManifest(manifest);
				if (v != null) {
					register(v);
					Logger.getInstance().log(Logger.INFO,
							"VocabularyRegistry: loaded '" + v.getId() + "' (" + v.size() + " classes).");
				}
			} catch (Exception e) {
				Logger.getInstance().log(Logger.WARNING,
						"VocabularyRegistry: failed to load " + manifest.getAbsolutePath() + ": " + e.getMessage());
			}
		}
	}

	private static File resolveClassifiersDir() {
		String prop = System.getProperty("solver.classifiers.dir");
		if (prop != null && !prop.isEmpty()) {
			File f = new File(prop);
			if (f.isDirectory()) return f;
		}
		String env = System.getenv("SOLVER_CLASSIFIERS_DIR");
		if (env != null && !env.isEmpty()) {
			File f = new File(env);
			if (f.isDirectory()) return f;
		}
		File f = new File("classifiers");
		if (f.isDirectory()) return f;
		return null;
	}

	private static Vocabulary loadManifest(File manifestFile) throws IOException, JSONException, InvalidExpression {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new FileReader(manifestFile));
		try {
			String line;
			while ((line = r.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			r.close();
		}
		JSONObject json = new JSONObject(sb.toString());

		String id = json.getString("classifierId");
		String displayName = json.optString("displayName", id);
		String kindStr = json.optString("kind", "categorical");
		Vocabulary.Kind kind;
		if (kindStr.equalsIgnoreCase("relation")) {
			kind = Vocabulary.Kind.RELATION;
		} else if (kindStr.equalsIgnoreCase("regression")) {
			kind = Vocabulary.Kind.REGRESSION;
		} else {
			kind = Vocabulary.Kind.CATEGORICAL;
		}

		List<String> classNames = new ArrayList<String>();
		if (json.has("classNames")) {
			JSONArray arr = json.getJSONArray("classNames");
			for (int i = 0; i < arr.length(); ++i) {
				classNames.add(arr.getString(i));
			}
		}
		// Relation/regression manifests may have empty classNames — still register them
		// so the UI can list them, but only categorical ones are usable with RuleIs.
		if (classNames.isEmpty() && kind == Vocabulary.Kind.CATEGORICAL) {
			throw new InvalidExpression("Categorical manifest '" + id + "' has empty classNames.");
		}
		if (classNames.isEmpty()) {
			// Placeholder entry for non-categorical manifests so they appear in listAll().
			classNames.add("__noncategorical__");
		}

		String checkpointPath = json.optString("checkpointPath", null);
		if (checkpointPath != null && !checkpointPath.isEmpty()) {
			File cp = new File(checkpointPath);
			if (!cp.isAbsolute()) {
				cp = new File(manifestFile.getParentFile(), checkpointPath);
			}
			checkpointPath = cp.getAbsolutePath();
		}

		String suggested = json.optString("suggestedPrimitiveTypeName", null);

		return new Vocabulary(id, displayName, kind, classNames, checkpointPath, suggested);
	}
}
