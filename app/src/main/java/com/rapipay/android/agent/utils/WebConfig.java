package com.rapipay.android.agent.utils;

public class WebConfig {

    public static String WEB = "https://rapipay.com/RapiPayAPIHub/";
    public static String UAT = WEB + "HandsetRegistration";
    public static String EKYC = WEB + "EKYCProcess";
    public static String EKYC_FORWARD = WEB + "kycforward";
    public static String BASIC_USERID = "1000000012";
    public static String BASIC_PASSWORD = "Test@321";
    public static String RESPONSE_URL = WEB + "views/databack.jsp";
    public static String FUNDTRANSFER_URL = WEB + "BCServices";
    public static String NETWORKTRANSFER_URL = WEB + "GetServiceProvider";
    public static String RECHARGE_URL = WEB + "UBPService";
    public static String WALLETTRANSFER_URL = WEB + "DMTService";
    public static String PASSBOOK_URL = WEB + "CommonReport";
    public static String CASHOUT_URL = "http://192.168.1.101:8085/MposService/MposTxnServlet";
//    public static String CommonReport = "http://192.168.1.110:8080/TxnReportingApp/CommonReport";
//     public static String LOGIN_URL="http://192.168.1.110:8080/SecLogOnApp/SecLogOn";
}
