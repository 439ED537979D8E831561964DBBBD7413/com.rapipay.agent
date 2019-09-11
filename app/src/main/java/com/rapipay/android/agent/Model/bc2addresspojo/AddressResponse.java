package com.rapipay.android.agent.Model.bc2addresspojo;

import java.util.List;

public class AddressResponse {
    private String Message;

    private String Status;

    private List<PostOffice> PostOffice;

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getMessage() {
        return this.Message;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getStatus() {
        return this.Status;
    }

    public void setPostOffice(List<PostOffice> PostOffice) {
        this.PostOffice = PostOffice;
    }

    public List<PostOffice> getPostOffice() {
        return this.PostOffice;
    }
}