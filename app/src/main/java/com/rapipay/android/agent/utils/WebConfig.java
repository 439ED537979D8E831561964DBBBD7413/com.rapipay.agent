package com.rapipay.android.agent.utils;

public class WebConfig {
    public static String WEBMain = "http://192.168.1.101:8085/";
    public static String WEB = WEBMain + "RapiPayAPIHub/";
    public static String UAT = WEB + "HandsetRegistration";
    public static String KYC_RAPIPAY_APP = "KYC_RAPIPAY_APP/";
    public static String EKYC = KYC_RAPIPAY_APP+"EKYCProcess";
    public static String EKYCFORWARD = KYC_RAPIPAY_APP+"kycforward";
    public static String EKYC_FORWARD = WEBMain + EKYC;
    public static String EKYC_FORWARD_POST = WEBMain + EKYCFORWARD;
    public static String BASIC_USERID = "1000000012";
    public static String BASIC_PASSWORD = "Test@321";
    public static String RESPONSE_URL = WEB + "views/databack.jsp";
    public static String FUNDTRANSFER_URL = WEB + "BCServices";
    public static String NETWORKTRANSFER_URL = WEB + "GetServiceProvider";
    public static String RECHARGE_URL = WEB + "UBPService";
    public static String WALLETTRANSFER_URL = WEB + "DMTService";
    public static String PASSBOOK_URL = WEB + "CommonReport";
    public static String CASHOUT_URL = WEBMain+"MposService/MposTxnServlet";
    public static String MPOSREG = WEBMain +"KYC_RAPIPAY_APP/EnrollmentFormService";
    public static String CRNF = WEBMain +"CRNFApp/CRNFService";
    public static String CommonReport = WEBMain+"TxnReportingApp/CommonReport";
    public static String COMMONAPI = WEBMain+"CommonApiApp/CommonApi";
    public static String LOGIN_URL=WEBMain+"SecLogOnApp/SecLogOnService";
     public WebConfig(String main){
         WEBMain = main;
     }
}
