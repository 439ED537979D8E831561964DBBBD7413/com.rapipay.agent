package com.rapipay.android.agent.Model.microaeps;

import java.util.List;

public class Microresponse1 {
    private boolean status;

    private String message;

    private List<Microdata1> data;

    private int statusCode;

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return this.status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setData(List<Microdata1> data) {
        this.data = data;
    }

    public List<Microdata1> getData() {
        return this.data;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}