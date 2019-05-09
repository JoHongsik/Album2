package com.example.myapplication;

import java.io.Serializable;

public class URLData implements Serializable {
    private String URL;
    private int URLNumber;
    private boolean checkBoxState;

    public URLData(String URL, int URLNumber){
        this.URL = URL;
        this.URLNumber = URLNumber;

    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getURLNumber(){
        return URLNumber;
    }

    public void setURLNumber(int URLNumber){
        this.URLNumber = URLNumber;
    }

    public void setCheckBoxState(boolean checkBoxState){
        this.checkBoxState = checkBoxState;
    }

    public boolean getCheckBoxState(){
        return this.checkBoxState;
    }
}

