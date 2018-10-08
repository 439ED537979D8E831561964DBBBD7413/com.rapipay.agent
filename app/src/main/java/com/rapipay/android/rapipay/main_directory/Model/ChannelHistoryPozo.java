package com.rapipay.android.rapipay.main_directory.Model;

public class ChannelHistoryPozo {
    String name;
    String account;
    String amount;
    String txnId;
    String date_id;
    String orgTxnid;
    String transferType;

    public ChannelHistoryPozo(String name, String account, String amount, String txnId,String date_id,String orgTxnid,String transferType) {
        this.name = name;
        this.account = account;
        this.amount = amount;
        this.txnId = txnId;
        this.date_id = date_id;;
        this.orgTxnid = orgTxnid;
        this.transferType = transferType;
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
