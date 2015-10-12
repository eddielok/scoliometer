package com.level.scoliometer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MSKITEM {
    @SerializedName("Standard_Deviation")
    private String Standard_Deviation;
    @SerializedName("Mean")
    private String Mean;
    @SerializedName("Ethnic_Gp")
    private String Ethnic_Gp;
    @SerializedName("Gender")
    private String Gender;
    @SerializedName("AgeGroup1")
    private String AgeGroup1;
    @SerializedName("AgeGroup2")
    private String AgeGroup2;
    @SerializedName("Region")
    private String Region;
    @SerializedName("Posture_Movement")
    private String Posture_Movement;
    public String getMean() {
        return Mean;
    }
    public void setmean(String nmean) {
        this.Mean=nmean;
    }
    public void setStandard_Deviation(String nsd) {
        this.Standard_Deviation=nsd;
    }
    public String getStandard_Deviation() {
        return Standard_Deviation;
    }

    public String getEthnic_Gp() {
        return Ethnic_Gp;
    }
    public void setEthnic_Gp(String nEthnic_Gp) {
        this.Ethnic_Gp=nEthnic_Gp;
    }
    public void setGender(String nGender) {
        this.Gender=nGender;
    }
    public String getGender() {
        return Gender;
    }
    public String getAgeGroup1() {
        return AgeGroup1;
    }
    public void setAgeGroup1(String nAgeGroup1) {
        this.AgeGroup1=nAgeGroup1;
    }
    public void setAgeGroup2(String nAgeGroup2) {
        this.AgeGroup2=nAgeGroup2;
    }
    public String getAgeGroup2() {
        return AgeGroup2;
    }
    public String getRegion() {
        return Region;
    }
    public void setRegion(String nRegion) {
        this.Region=nRegion;
    }
    public void setPosture_Movement(String nPosture_Movement) {
        this.Posture_Movement=nPosture_Movement;
    }
    public String getPosture_Movement() {
        return Posture_Movement;
    }
    public MSKITEM() {

    }
}