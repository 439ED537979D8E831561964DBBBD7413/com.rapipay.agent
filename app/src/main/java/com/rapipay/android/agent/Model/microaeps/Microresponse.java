package com.rapipay.android.agent.Model.microaeps;

import java.util.List;

public class Microresponse{
        private boolean status;

        private String message;

        private List<Microdata> data;

        private int statusCode;

        public void setStatus(boolean status){
        this.status = status;
    }
        public boolean getStatus(){
        return this.status;
    }
        public void setMessage(String message){
        this.message = message;
    }
        public String getMessage(){
        return this.message;
    }
        public void setData(List<Microdata> data){
        this.data = data;
    }
        public List<Microdata> getData(){
        return this.data;
    }
        public void setStatusCode(int statusCode){
        this.statusCode = statusCode;
    }
        public int getStatusCode(){
        return this.statusCode;
    }
    }