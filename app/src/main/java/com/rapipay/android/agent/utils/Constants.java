package com.rapipay.android.agent.utils;

public class Constants
{
    /*QC FINO*/public static final String VERSION = "1000";// API VERSION(Common)
    /*QC FINO*/public static final String CLIENT_HEADER_ENCRYPTION_KEY = "982b0d01-b262-4ece-a2a2-45be82212ba1";//This key is use for HeaderData Encryption(Common)
    /*QC FINO*/ public static final String RETURN_URL = "http://10.15.20.141:8022/PaymentBankBCAPI/uiservice.svc/ClientValidationResponse";// This URL is provided by Client to send Response

    //QC Rapipay
    public static final String MERCHANT_ID = "8446808997";//
    public static final String CLIENTID = "49";//
    public static final String AUTHKEY = "333-444-555";//
    public static final String CLIENT_REQUEST_ENCRYPTION_KEY = "2e8c1753-a1ce-46f4-8dc1-fa6e35a0e2b0";

    //SERVICE_ID for AEPS Transaction
    public static final String SERVICE_AEPS_CW = "151";//AEPS_CashWithdrow
    public static final String SERVICE_AEPS_BE = "152";//AEPS_BalanceEnquiry
    public static final String SERVICE_AEPS_TS = "154";//AEPS_TransactionStatus

    //SERVICE_ID for MICRO ATM Transaction
    public static final String SERVICE_MICRO_CW = "156";//MicroATM_CashWithdraw
    public static final String SERVICE_MICRO_BE = "157";//MicroATM_BalanceEnquiry
    public static final String SERVICE_MICRO_TS = "158";//MicroATM_TransactionStatus
}
