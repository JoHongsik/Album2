package com.example.myapplication;

import java.io.Serializable;

public class URLData implements Serializable {
    private String URL;
    private boolean checkBoxState;
    private int URLNo;
    private String FileName;
    private boolean HaveSeen;

    public URLData(){
        HaveSeen = false;
    }

    public boolean getHaveSeen() {
        return HaveSeen;
    }

    public void setHaveSeen(boolean haveSeen) {
        HaveSeen = haveSeen;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public int getURLNo() {
        return URLNo;
    }

    public void setURLNo(int URLNo) {
        this.URLNo = URLNo;
    }

    public URLData(String URL){
        this.URL = URL;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setCheckBoxState(boolean checkBoxState){
        this.checkBoxState = checkBoxState;
    }

    public boolean getCheckBoxState(){
        return this.checkBoxState;
    }
}

