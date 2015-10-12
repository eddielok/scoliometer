package com.level.scoliometer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHIU on 6/8/2015.
 */
public class tblMSKGROUP {

    @SerializedName("tblMSKGROUP")
    @Expose
    private List<MSKITEM> MSKITEM = new ArrayList<MSKITEM>();
    public List<MSKITEM> getMSKITEM() {
        return MSKITEM;
    }
    public void setMSKITEM(List<MSKITEM> MSKITEM) {
        this.MSKITEM = MSKITEM;
    }

}
