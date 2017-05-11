package com.acando.newshunter.content;

public class Source {

    public String internalName, name, url;
    public boolean supportsLatest;

    public Source(String internalName, String name, String url, boolean supportsLatest) {
        this.internalName = internalName;
        this.name = name;
        this.url = url;
        this.supportsLatest = supportsLatest;
    }

    public Source() {

    }
}
