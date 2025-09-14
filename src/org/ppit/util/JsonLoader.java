package org.ppit.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * TODO: This seems will not be used for the current version of project
 */
public class JsonLoader {

	public static JSONObject loadJson(String fileName, int level){
		JSONObject jsonObject = null;
		
//		try {
//			JSONObject jsonObjects = loadJson(fileName);
//			jsonObject = (JSONObject) jsonObjects.getJSONArray("concepts").get(level);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		return jsonObject;//test
	}      
	
	public static JSONObject loadJson(String fileName){
		JSONObject json = null;
//		try {
//			StringBuffer strBuf = new StringBuffer();
//			String jsonPath = ConfigManager.getInstance().getJSONDirPath()+fileName;
//			BufferedReader in = new BufferedReader(new FileReader(jsonPath));
//			char cbuf [] = new char[1024];
//			while(in.read(cbuf) != -1){
//				strBuf.append(cbuf);
//			}
//			//String jsonStr = in.;
//			json = new JSONObject(strBuf.toString());
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return json;
	}
}
