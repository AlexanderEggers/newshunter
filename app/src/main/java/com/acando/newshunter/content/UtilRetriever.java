package com.acando.newshunter.content;

import com.acando.newshunter.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class UtilRetriever {

    protected static int getJSONIntegerValue(JSONObject jsonObject, String key) throws JSONException {
        if (!jsonObject.isNull(key)) {
            return jsonObject.getInt(key);
        }
        return Util.ERROR;
    }

    protected static String getJSONStringValue(JSONObject jsonObject, String key) throws JSONException {
        if (!jsonObject.isNull(key)) {
            String value = jsonObject.getString(key);
            if (!value.equals("null")) {
                return value;
            }
        }
        return "";
    }
}
