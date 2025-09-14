package org.ppit.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;

public class JSONToBasicDBObjectConverter {
	
	/**
	 * @param JSONOArray array
	 * @return returns the BasicDBObject of the given JSONArray
	 * */
	public static ArrayList<BasicDBObject> getDBObjectFromJSONArray(JSONArray array)
	{
		ArrayList<BasicDBObject> objectArray = new ArrayList<BasicDBObject>();
		try {
			for (int i = 0; i < array.length(); ++i) {
				objectArray.add(getDBObjectFromJSONObject(array.getJSONObject(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objectArray;
	}
	
	/**
	 * @param JSONOBject object
	 * @return returns the BasicDBObject of the given JSONObject
	 * */
	public static BasicDBObject getDBObjectFromJSONObject(JSONObject object)
	{
		BasicDBObject objectContent = new BasicDBObject();
		Iterator keyIterator = object.keys();
		String key, value;
		try {
			while(keyIterator.hasNext())
			{
				key = keyIterator.next().toString();
				if (object.get(key) instanceof JSONObject)
				{
					// if the current value of the given key
					//is a JSONObject then call getDBObjectFromJSONObject
					objectContent.put(key, getDBObjectFromJSONObject(object.getJSONObject(key)));
				} else if (object.get(key) instanceof JSONArray) {
					// if the current value of the given key
					//is a JSONArray then call getDBObjectFromJSONArray
					objectContent.put(key, getDBObjectFromJSONArray(object.getJSONArray(key)));
				} else {
					// else it's a string and we just put it
					value = object.getString(key);
					objectContent.put(key,value);
				}
	
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return objectContent;
	}
}
