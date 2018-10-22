package com.rapipay.android.agent.utils;

import android.content.Context;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.rapipay.android.agent.R;

public class HttpConnector {
    private static HttpConnector instance = null;
    private static InputStream serverCert;
    private static SSLContext context;
    private static HostnameVerifier hostnameVerifier;
    String result = null;

    public static HttpConnector getInstance() {
        if (instance == null)
            instance = new HttpConnector();
        return instance;
    }

    public static void setServerCert(Context cert) {
//        serverCert = serverCrt;
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream in = cert.getResources().openRawResource(R.raw.server);
            InputStream caInput = new BufferedInputStream(in);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            // Create an HostnameVerifier that hardwires the expected hostname.
            // Note that is different than the URL's hostname:
            // example.com versus example.org
            hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private HttpConnector() {

    }

    public String postData(final String url, final String xmlData, String strHeaderData) throws Exception {

        URL urlToRequest = new URL(url);

        HttpURLConnection urlConnection = null;


        try {
            boolean isHttps = url.startsWith("https");
            if (isHttps) {
                SSLSocketFactory socketFactory = null;
                try {
//                    if (context == null)
//                    {
//                    		context = SSLContext.getInstance("TLS");
//                    		context.init(null, new X509TrustManager[]{new X509TrustManager(){
//                            public void checkClientTrusted(X509Certificate[] chain,
//                                                           String authType) throws CertificateException {}
//                            public void checkServerTrusted(X509Certificate[] chain,
//                                                           String authType) throws CertificateException {}
//                            public X509Certificate[] getAcceptedIssuers() {
//                                return new X509Certificate[0];
//                            }}}, new SecureRandom());
//
//                    }
                    socketFactory = context.getSocketFactory();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                urlConnection = (HttpsURLConnection) urlToRequest.openConnection();

                if (socketFactory != null)
                    ((HttpsURLConnection) urlConnection).setSSLSocketFactory(socketFactory);

            } else {
                urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            }
            urlConnection.setRequestProperty("Authorization", "Basic " + new String(Base64.encode(strHeaderData.getBytes(), Base64.DEFAULT)));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setConnectTimeout(90000);
            urlConnection.setReadTimeout(90000);
            urlConnection.setRequestProperty("Content-Length", Integer.toString(xmlData.length()));
            urlConnection.setFixedLengthStreamingMode(xmlData.length());
            urlConnection.setUseCaches(false);
            urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:221.0) Gecko/20100101 Firefox/31.0");

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(xmlData);
            out.close();

            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("Error reading response from server!. Response Code: " + statusCode);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseOutput.append(line);
            }
            br.close();

            return responseOutput.toString();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

//    protected String serverUpdate(String[] params,Context _context) {
//        try {
//            URL url= new URL(params[0]);
//            transfertype =params[1];
//            _SFunction=params[2];
//            _Windoestype=params[3];
//            System.out.println("Error in http connection " + url.toString());
//
//            if ( isConnectingToInternet(_context)==true) {
//                InputStream inputStream = null;
//                if (params[1].equals("POST")) {
//                    transfertype = "POST";
//                    String _json="";
//                    _json=params[4];
//                    try {
//                        HttpClient httpclient = new DefaultHttpClient();
//                        HttpPost httppost = new HttpPost(params[0]);
//                        System.setProperty("http.keepAlive", "false");
//                        StringEntity se = new StringEntity(_json);
//                        httppost.setEntity(se);
//                        httppost.setHeader("Accept", "application/json");
//                        httppost.setHeader("Content-type", "application/json");
//                        if(_token==true){
//                            httppost.setHeader("Token", GlobalClass.getToKen());
//                            httppost.setHeader("Mobile", GlobalClass.getMobileNo());
//                            httppost.setHeader("MG", GlobalClass.getMedsaveGuest());
//                            System.out.println("Json Post Data  " + _json);
//                        }
//                        HttpResponse response = httpclient.execute(httppost);
//                        inputStream = response.getEntity().getContent();
//                        if(inputStream != null)
//                            result = convertInputStreamToString(inputStream);
//                        else
//                            result = "Did not work!";
//                    } catch (Exception e) {
//                        System.out.println("Error in http connection " + e.toString());
//                    }
//                } else if (params[1].equals("GET")) {
//                    try {
//                        transfertype = "GET";
//                        DefaultHttpClient httpclient = new DefaultHttpClient();
//                        String host = url.toString();
//                        host = host.replace(" ", "%20");
//                        HttpGet request = new HttpGet(host);
//                        System.out.println("host url :    " + host);
//
//                        // request = new HttpGet(host.trim()+URLEncoder.encode(params[3].toString(),"UTF-8"));
//                        request.setHeader("Accept", "application/json");
//                        request.setHeader("Content-type", "application/json");
//                        HttpResponse response = httpclient.execute(request);
//                        String res = response.toString();
//                        System.out.println("response:    " + res);
//                        res = res.replaceAll("\\s+", "");
//                        if (res != null) {
//                            HttpEntity entity = response.getEntity();
//                            result = EntityUtils.toString(entity);
//                            int index = result.indexOf(":");
//                            index = index + 1;
//                            System.out.println("Get Response : " + result);
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(_context, "ERROR " + e.getMessage(),
//                                Toast.LENGTH_LONG).show();
//                        System.out.println("Error in http connection " + e.toString());
//                    }
//                }
//            }
//            else
//            {
//                Toast.makeText(_context, R.string.prompt_Internet, Toast.LENGTH_LONG).show();
//                return "";
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//            Log.v("WCF 2", e.getMessage());
//        }
//        //result=result.replace( "{","" ).replace( "}","" );
//        //result=result.substring(2);
//        //result=removeLastChar(result);
//        //result=convertStandardJSONString(result);
//        return result;
//    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
