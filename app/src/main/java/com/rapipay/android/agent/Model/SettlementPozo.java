package com.rapipay.android.agent.Model;

public class SettlementPozo {
    String agentID;
    String agentName;
    String agentBankName;
    String agentBankIFSC;
    String agentAccountNO;
    String accountStatus;
    String accountType;
    String remark;
    String agentAddress;
    String agentBankId;

    public SettlementPozo(String agentID, String agentName, String agentBankName, String agentBankIFSC, String agentAccountNO, String accountStatus, String accountType, String remark, String agentAddress, String agentBankId) {
        this.agentID = agentID;
        this.agentName = agentName;
        this.agentBankName = agentBankName;
        this.agentBankIFSC = agentBankIFSC;
        this.agentAccountNO = agentAccountNO;
        this.accountStatus = accountStatus;
        this.accountType = accountType;
        this.remark = remark;
        this.agentAddress = agentAddress;
        this.agentBankId = agentBankId;
    }

    public String getAgentID() {
        return agentID;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentBankName() {
        return agentBankName;
    }

    public void setAgentBankName(String agentBankName) {
        this.agentBankName = agentBankName;
    }

    public String getAgentBankIFSC() {
        return agentBankIFSC;
    }

    public void setAgentBankIFSC(String agentBankIFSC) {
        this.agentBankIFSC = agentBankIFSC;
    }

    public String getAgentAccountNO() {
        return agentAccountNO;
    }

    public void setAgentAccountNO(String agentAccountNO) {
        this.agentAccountNO = agentAccountNO;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getAgentBankId() {
        return agentBankId;
    }

    public void setAgentBankId(String agentBankId) {
        this.agentBankId = agentBankId;
    }
}
