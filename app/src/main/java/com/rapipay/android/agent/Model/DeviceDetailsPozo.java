package com.rapipay.android.agent.Model;

public class DeviceDetailsPozo {
    private String deviceID;
    private String deviceType;
    private String bluetoothID;

    public DeviceDetailsPozo(String deviceID, String deviceType, String bluetoothID) {
        this.deviceID = deviceID;
        this.deviceType = deviceType;
        this.bluetoothID = bluetoothID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getBluetoothID() {
        return bluetoothID;
    }

    public void setBluetoothID(String bluetoothID) {
        this.bluetoothID = bluetoothID;
    }
}
