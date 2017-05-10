package com.acando.newshunter.content;

import java.io.Serializable;

public class NewsEntry implements Serializable {
    public final String title, link, pubDate, desc, imageURL;

    public NewsEntry(String title, String desc, String link, String pubDate, String imageURL) {
        this.title = title;
        this.desc = desc;
        this.link = link;
        this.pubDate = pubDate;
        this.imageURL = imageURL;
    }
}