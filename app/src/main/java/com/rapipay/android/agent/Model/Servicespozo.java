package com.rapipay.android.agent.Model;

public class Servicespozo {
    private String lookUpId;
    private String serviceName;
    private String status;
    private boolean checked = false;

    public Servicespozo(String lookUpId, String serviceName, String status, boolean checked) {
        this.lookUpId = lookUpId;
        this.serviceName = serviceName;
        this.status = status;
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public String getLookUpId() {
        return lookUpId;
    }

    public void setLookUpId(String lookUpId) {
        this.lookUpId = lookUpId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
