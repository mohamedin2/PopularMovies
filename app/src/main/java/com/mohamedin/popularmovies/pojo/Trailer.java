package com.mohamedin.popularmovies.pojo;

public class Trailer {
    private String name, key, type;

    public Trailer(){}

    public Trailer(String name, String key, String type) {
        this.name = name;
        this.key = key;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
