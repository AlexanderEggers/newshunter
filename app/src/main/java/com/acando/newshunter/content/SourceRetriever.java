package com.acando.newshunter.content;

import android.content.Context;
import android.util.Log;

import com.acando.newshunter.Global;
import com.acando.newshunter.UtilNetwork;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SourceRetriever {

    public static ArrayList<Source> retrieveAllSources(Context context) throws Exception {
        ArrayList<Source> sources = new ArrayList<>();

        String url = "https://newsapi.org/v1/sources?language=en";
        String data = ((Global) context.getApplicationContext()).getDataFromMemCache(url);
        if (data == null) {
            if (UtilNetwork.isNetworkAvailable(context)) {
                data = UtilNetwork.websiteToString(url);
                ((Global) context.getApplicationContext()).addDataToMemoryCache(url, data);
            } else {
                return sources;
            }
        }

        JSONObject json = new JSONObject(data);
        JSONArray articleArray = json.getJSONArray("sources");
        for (int i = 0; i < articleArray.length(); i++) {
            JSONObject sourceJSON = articleArray.getJSONObject(i);
            Source source = new Source();
            source.internalName = UtilRetriever.getJSONStringValue(sourceJSON, "id");
            source.name = UtilRetriever.getJSONStringValue(sourceJSON, "name");
            source.url = UtilRetriever.getJSONStringValue(sourceJSON, "url");
            sources.add(source);
        }

        return sources;
    }
}
