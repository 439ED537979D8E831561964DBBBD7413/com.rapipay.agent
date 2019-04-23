package com.rapipay.android.agent.Model;

public class ChannelHistoryPozo {
    String name;
    String account;
    String amount;
    String txnId;
    String date_id;
    String serviceProviderTXNID;
    String orgTxnid;
    String transferType;
    String serviceType;
    String txnRequestDate;

    public ChannelHistoryPozo(String name, String account, String amount, String txnId, String date_id, String serviceProviderTXNID, String transferType, String orgTxnid,String serviceType, String txnRequestDate) {
        this.name = name;
        this.account = account;
        this.amount = amount;
        this.txnId = txnId;
        this.date_id = date_id;;
        this.serviceProviderTXNID = serviceProviderTXNID;
        this.transferType = transferType;
        this.orgTxnid=orgTxnid;
        this.serviceType=serviceType;
        this.txnRequestDate = txnRequestDate;
    }

    public String getTxnRequestDate() {
        return txnRequestDate;
    }

    public void setTxnRequestDate(String txnRequestDate) {
        this.txnRequestDate = txnRequestDate;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceProviderTXNID() {
        return serviceProviderTXNID;
    }

    public void setServiceProviderTXNID(String serviceProviderTXNID) {
        this.serviceProviderTXNID = serviceProviderTXNID;
    }

    public String getTransferType() {
        return transferType;
    }

    public String getName() {
        return name;
    }

    public String getAccount() {
        return account;
    }

    public String getAmount() {
        return amount;
    }

    public String getTxnId() {
        return txnId;
    }

    public String getDate_id() {
        return date_id;
    }

    public String getOrgTxnid() {
        return orgTxnid;
    }
}
