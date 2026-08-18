package org.ppit.actions.situation;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

import net.sf.json.JSONObject;

import org.ppit.core.percept.Situation;
import org.ppit.core.percept.SituationManager;
import org.ppit.util.Logger;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Accepts a chess-board image as base64-encoded POST data, shells out to
 * Solver_train/predict_board.py through {@link SituationManager#createSituationFromImage},
 * and returns the resulting Situation as JSON (name + FEN + cell list).
 *
 * Request parameters:
 *   imageBase64  — required. The image bytes, base64-encoded. The optional
 *                  data-URL prefix "data:image/png;base64," is tolerated.
 *   sitName      — optional. Defaults to "image_<epoch>".
 *
 * Response shape:
 *   {
 *     "name":  "...",
 *     "fen":   "...",
 *     "cells": <pass-through from predict_board.py>,
 *     "error": "..."   // only on failure
 *   }
 */
public class CreateSituationFromImage extends ActionSupport {
	private static final long serialVersionUID = 1L;

	private String imageBase64;
	private String sitName;
	private JSONObject json;

	public String execute() {
		json = new JSONObject();
		if (imageBase64 == null || imageBase64.isEmpty()) {
			json.put("error", "missing imageBase64");
			return SUCCESS;
		}

		// Strip optional data-URL prefix.
		String b64 = imageBase64;
		int comma = b64.indexOf(',');
		if (b64.startsWith("data:") && comma >= 0) {
			b64 = b64.substring(comma + 1);
		}
		// Defensive: drop whitespace browsers may insert in long base64 strings.
		b64 = b64.replaceAll("\\s+", "");

		byte[] bytes;
		try {
			bytes = Base64.getDecoder().decode(b64);
		} catch (IllegalArgumentException e) {
			json.put("error", "imageBase64 is not valid base64: " + e.getMessage());
			return SUCCESS;
		}

		File tmp = null;
		try {
			tmp = File.createTempFile("solver-board-", ".img");
			FileOutputStream out = new FileOutputStream(tmp);
			try { out.write(bytes); } finally { out.close(); }

			String name = (sitName != null && !sitName.isEmpty())
					? sitName : ("image_" + System.currentTimeMillis());

			Situation situation = SituationManager.getInstance()
					.createSituationFromImage(tmp, name);

			json.put("name", situation.getName());
			try {
				json.put("fen", situation.toFen());
			} catch (PPITException e) {
				json.put("fenError", e.getMessage());
			}
			json.put("cellCount", situation.getElements().size());
		} catch (PPITException e) {
			Logger.getInstance().log(Logger.ERROR,
					"createSituationFromImage failed: " + e.getMessage());
			json.put("error", e.getMessage());
		} catch (Exception e) {
			Logger.getInstance().log(Logger.ERROR,
					"createSituationFromImage unexpected: " + e.getMessage());
			json.put("error", "internal error: " + e.getMessage());
		} finally {
			if (tmp != null) tmp.delete();
		}
		return SUCCESS;
	}

	public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
	public void setSitName(String sitName) { this.sitName = sitName; }
	public JSONObject getJson() { return json; }
}
