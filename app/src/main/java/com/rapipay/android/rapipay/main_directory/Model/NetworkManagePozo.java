package com.rapipay.android.rapipay.main_directory.Model;

public class NetworkManagePozo {
    String cuurentNo;
    String backMaintain;

    public NetworkManagePozo(String cuurentNo, String backMaintain) {
        this.cuurentNo = cuurentNo;
        this.backMaintain = backMaintain;
    }

    public String getCuurentNo() {
        return cuurentNo;
    }

    public void setCuurentNo(String cuurentNo) {
        this.cuurentNo = cuurentNo;
    }

    public String getBackMaintain() {
        return backMaintain;
    }

    public void setBackMaintain(String backMaintain) {
        this.backMaintain = backMaintain;
    }
}
