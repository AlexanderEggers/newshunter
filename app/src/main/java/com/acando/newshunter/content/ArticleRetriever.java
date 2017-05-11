package com.acando.newshunter.content;

import android.content.Context;

import com.acando.newshunter.Global;
import com.acando.newshunter.R;
import com.acando.newshunter.UtilNetwork;
import com.acando.newshunter.database.table.SourceTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

public class ArticleRetriever {

    public static ArrayList<Article> retrieveArticles(Context context, String sort) throws Exception {
        ArrayList<Article> articles = new ArrayList<>();
        ArrayList<Source> sources = SourceTable.getAll(context);

        for (Source sourceItem : sources) {
            articles.addAll(getArticlesBySource(context, sourceItem.internalName, sort, 5));
        }

        Collections.sort(articles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                if (o1.date < o2.date) {
                    return 1;
                } else if (o1.date > o2.date) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        return articles;
    }

    public static ArrayList<Article> getArticlesBySource(Context context, String source,
                                                         String sort, int max) throws Exception {
        ArrayList<Article> articles = new ArrayList<>();
        String url = "https://newsapi.org/v1/articles?source=" + source + "&sortBy=" + sort
                + "&apiKey=" + context.getString(R.string.api_key);

        String data = ((Global) context.getApplicationContext()).getDataFromMemCache(url);
        if (data == null) {
            if (UtilNetwork.isNetworkAvailable(context)) {
                data = UtilNetwork.websiteToString(url);
                ((Global) context.getApplicationContext()).addDataToMemoryCache(url, data);
            } else {
                return articles;
            }
        }

        JSONObject json = new JSONObject(data);
        JSONArray articleArray = json.getJSONArray("articles");
        for (int i = 0; i < articleArray.length() || i <= max; i++) {
            JSONObject articleJSON = articleArray.getJSONObject(i);
            Article article = new Article();
            article.source = source;
            article.desc = UtilRetriever.getJSONStringValue(articleJSON, "description");
            article.title = UtilRetriever.getJSONStringValue(articleJSON, "title");
            article.link = UtilRetriever.getJSONStringValue(articleJSON, "url");
            article.imageURL = UtilRetriever.getJSONStringValue(articleJSON, "urlToImage");

            String date = UtilRetriever.getJSONStringValue(articleJSON, "publishedAt");
            if(!data.equals("")) {
                String dateTimeSplit[] = date.split("T");
                String dateSplit[] = dateTimeSplit[0].split("-");
                String timeSplit[] = dateTimeSplit[1].replace("Z", "").split(":");

                System.out.println(Arrays.asList(dateSplit));
                System.out.println(Arrays.asList(timeSplit));

                Calendar cal = new GregorianCalendar();
                cal.set(
                        //DATE
                        Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]),
                        Integer.parseInt(dateSplit[2]),

                        //TIME
                        Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]),
                        Integer.parseInt(timeSplit[2]));

                article.date = cal.getTimeInMillis();
            } else {
                article.date = System.currentTimeMillis();
            }

            articles.add(article);
        }

        return articles;
    }
}
