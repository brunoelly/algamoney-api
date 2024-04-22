package com.algaworks.algamoneyapi.dto;

public class Attachements {

    private String name;

    private String url;

    public Attachements(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}