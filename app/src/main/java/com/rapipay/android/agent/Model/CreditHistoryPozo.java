package com.rapipay.android.agent.Model;public class CreditHistoryPozo {    String requestId;    String requesttype;    String amount;    String remark;    String status;    String agentid;    int header;    String bankTxnId;    public CreditHistoryPozo(String requestId, String requesttype, String amount, String remark, String status) {        this.requestId = requestId;        this.requesttype = requesttype;        this.amount = amount;        this.remark = remark;        this.status = status;    }  public CreditHistoryPozo(String requestId,  String amount,  String bankTxnId, String requesttype, String agentid, String remark, String status, int header) {        this.requestId = requestId;        this.requesttype = requesttype;        this.amount = amount;        this.remark = remark;        this.status = status;        this.agentid = agentid;        this.header = header;        this.bankTxnId = bankTxnId;    }    public int getHeader() {        return header;    }    public void setHeader(int header) {        this.header = header;    }    public String getRequestId() {        return requestId;    }    public String getrequesttype() {        return requesttype;    }    public String getBankTxnId() {        return bankTxnId;    }    public void setBankTxnId(String bankTxnId) {        this.bankTxnId = bankTxnId;    }    public String getAmount() {        return amount;    }    public String getAgentid() {        return agentid;    }    public void setAgentid(String agentid) {        this.agentid = agentid;    }    public String getRemark() {        return remark;    }    public String getStatus() {        return status;    }}