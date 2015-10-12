package com.level.scoliometer.models;

import com.google.gson.annotations.SerializedName;

public class Version {
    @SerializedName("version")
    private String Version;


    public String getversion() {
        return Version;
    }
    public void setversion(String nversion) {
        this.Version=nversion;
    }

    public Version() {

    }

}