package com.rapipay.android.agent.utils;

public class WebConfig {
    // C:\Users\Dev_22\AppData\Local\Android\Sdk
    // C:\Program Files\Android\Android Studio\jre
    public static String WEBMain = "http://172.16.50.210:8080/";
   // public static String WEBMain = "https://rapipay.com/";
    public static String KYCACT = "http://rpt.rapipay.com/";
    public static String WEB = WEBMain + "RapiPayAPIHub/";
    public static String UAT = WEB + "HandsetRegistration";
    public static String WALLET = WEBMain + "DMTWalletService/";
//    public static String KYC_RAPIPAY_APP = KYCURL + "KYC_RAPIPAY_APP/";
    public static String KYC_RAPIPAY_APP = WEBMain + "KYC_RAPIPAY_APP/";
    public static String EKYC = KYC_RAPIPAY_APP+"EKYCProcess";
    public static String ProcessKYC = KYC_RAPIPAY_APP+"ProcessKyc";
    public static String EKYCFORWARD = KYC_RAPIPAY_APP+"kycforward";
    public static String BASIC_USERID = "1000000012";
    public static String BASIC_PASSWORD = "Test@321";
  //  public static String BASIC_PASSWORD = "RapiLDAP#2018";
    public static String RESPONSE_URL = WEB + "views/databack.jsp";
    public static String FUNDTRANSFER_URL = WEB + "BCServices";
    public static String NETWORKTRANSFER_URL = WEB + "GetServiceProvider";
    public static String RECHARGE_URL = WEB + "UBPService";
    public static String WALLETTRANSFER_URL = WALLET + "DMTService";
    public static String WALLETRECEIPTURL = WEB + "DMTService";
    public static String RECHARGENEW = WEBMain+"UBPAPP/UBPServices";
    public static String CASHOUT_URL =  WEBMain +"MposService/MposTxnServlet";
   // public static String CASHOUT_URL1 = WEBMain + "AEPS_GATEWAY/AepsServiceApi";
  //  public static String CASHOUT_URL1 = "http://172.16.50.73:8080/AEPS_GATEWAY/AepsServiceApi";
 //   public static String CASHOUT_URL1 = "http://172.16.50.73:8082/AEPS_GATEWAY_V2/AepsServiceApi";
    public static String CASHOUT_URL1 =  WEBMain + "AEPS_GATEWAY_V2/AepsServiceApi";
   // public static String CASHOUT_URL1 =  "https://uat.rapipay.com/MposService/AEPSCashout";
    public static String MPOSREG = WEBMain +"KYC_RAPIPAY_APP/EnrollmentFormService";
    public static String CRNF = WEBMain +"CRNFApp/CRNFService";
    public static String CREADITBANKLIST =  KYCACT +  "CRNFAPP_IN/CRNF_INService";
    public static String CommonReport = WEBMain+"TxnReportingApp/CommonReport";
    public static String COMMONAPI = WEBMain+"CommonApiApp/CommonApi";
    public static String COMMONAPIS = WEBMain+"CommonApiApp/SubAgentCommonService";
    public static String LOGIN_URL=WEBMain+"SecLogOnApp/SecLogOnService";
    public static String SCRATCH_URL=KYCACT+"ScratchCouponAPP/CouponServiceApi";
    public static String SUBAGENT=WEBMain+"SecLogOnApp/SubAgentService";
    public static String PMTSERVICE_DETAILS=WEBMain+"IndoNepalFTAPP/PMTService";
    public static String BCRemittanceApp = WEBMain + "BCRemittanceApp/BCService";
 //   public static String BC2RemittanceApp = WEBMain + "DMTBC2ALAPP/BC2ALService";
    public static String BC2RemittanceApp = "http://172.16.50.73:8080/DMTBC2ALAPP/BC2ALService";
    public static String BC6RemittanceApp = "http://172.16.50.98:8081/DMTBC6APP/BC6AService";
  //  public static String BC2DMTBC2Service = WEBMain + "DMTBC2ALAPP/DMTBC2Service";
    public static String BC2DMTBC2Service =  "http://172.16.50.73:8080/DMTBC2ALAPP/DMTBC2Service";
    public static String BC6DMTBC6Service =  "http://172.16.50.98:8081/DMTBC6APP/DMTBC6Service";
   // public static String BC2DMTBC2Service = WEBMain + "DMTBC2ALAPP/DMTBC2Service";
    public static String AEPSReg = "http://172.16.50.95:8080/DMTBC2ALAPP/DMTBC2Service";
    public static String CREDITREQUESTHIST = WEBMain + "ReportApp/CommonReport";
    public static String CREDITREQUESTHISTRPT = KYCACT + "ReportApp/CommonReport";
  //  public static String AEPS2_INIT = "http://172.16.50.73:8082/AEPS_GATEWAY_V2/AepsServiceApi"; //http://172.16.50.73:8080/MposService/MposTxnServlet
    public static String AEPS2_INIT = WEBMain + "AEPS_GATEWAY_V2/AepsServiceApi"; //http://172.16.50.73:8080/MposService/MposTxnServlet
    public WebConfig(String main){
         WEBMain = main;
     }
}
