package com.acando.newshunter.content;

import java.io.Serializable;

public class Article implements Serializable {
    public String title, link, desc, imageURL, source;
    public long date;
}