package dev.xframe.admin.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import dev.xframe.utils.XLogger;
import dev.xframe.utils.XStrings;


/**
 * 
 * Java http methods util
 * Thread safe
 * @author luzj
 * 
 */
public class HttpClient {
    
    public static class Header {
        public final String key;
        public final String value;
        public Header(String key, String value) {
            this.key = key;
            this.value = value;
        }
        @Override
        public String toString() {
            return "Header [key=" + key + ", value=" + value + "]";
        }
    }
    
    public static class Resp {
        /**
         * 状态码
         * http status code
         */
        public final int code;//http response code
        /**
         * http status message
         */
        public final String message;
        /**
         * code 为200时正常返回的请求内容
         */
        public final String content;
        
        public Resp(int code, String message, String content) {
            this.code = code;
            this.message = message;
            this.content = content;
        }
        @Override
        public String toString() {
            return "Response [code=" + code + ", message=" + message + ", content=" + content + "]";
        }
    }
    
    
    public static final int timeout = 3000;// 默认3秒超时
    
    //http post, charset utf8
	public static Resp post(String url, String data, Header... headers){
        return post(url, data, StandardCharsets.UTF_8, headers);
    }
	//http post
    public static Resp post(String url, String data, Charset charset, Header... headers){
        return execute(url, data, "POST", charset, headers);
    }
    
    public static Resp post(String url, byte[] data, Charset charset, Header... headers) {
        return execute(url, data, "POST", charset, headers);
    }

    //http put, charset utf8
    public static Resp put(String url, String data, Header... reqs){
        return put(url, data, StandardCharsets.UTF_8, reqs);
    }
    //http put
    public static Resp put(String url, String data, Charset charset, Header... headers){
        return execute(url, data, "PUT", charset, headers);
    }
    
    //http delete, charset utf8
    public static Resp delete(String url, String data, Header... headers){
        return delete(url, data, StandardCharsets.UTF_8, headers);
    }
    //http delete
    public static Resp delete(String url, String data, Charset charset, Header... headers){
        return execute(url, data, "DELETE", charset, headers);
    }
    
    //http options, charset utf8
    public static Resp options(String url, String data, Header... headers){
        return options(url, data, StandardCharsets.UTF_8, headers);
    }
    //http options
    public static Resp options(String url, String data, Charset charset, Header... headers){
        return execute(url, data, "OPTIONS", charset, headers);
    }
    
    //http get, charset utf8
    public static Resp get(String url, Header... headers){
        return get(url, StandardCharsets.UTF_8, headers);
    }
    //http get
    public static Resp get(String url, Charset charset, Header... headers){
        return execute(url, "", "GET", charset, headers);
    }
    
    private static Resp execute(String url, String data, String method, Charset charset, Header... headers){
        byte[] rdata = XStrings.isEmpty(data) ? null : data.getBytes(charset);
        return execute(url, rdata, method, charset, headers);
    }

    private static Resp execute(String url, byte[] data, String method, Charset charset, Header... headers){
        HttpURLConnection conn = null;
        try {
            boolean doOutput = data != null;
            conn = buildConn(url, method, doOutput);

            addHeaders(conn, headers);

            if(doOutput) writeReqData(conn, data);
            
            int code = conn.getResponseCode();
            if(code == 200) {
                return new Resp(code, conn.getResponseMessage(), readRespContent(conn, charset));
            } else {
                return new Resp(code, conn.getResponseMessage(), readRespMessage(conn, charset));
            }
        } catch (Exception ex) {
            //ingore
            XLogger.warn("Http call [{}] error cause: {}", url, ex.getMessage());
            return new Resp(-1, ex.getMessage(), "");
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
    }

    private static String readRespContent(HttpURLConnection conn, Charset charset) throws IOException {
        InputStream input = null;
        try {
            input = conn.getInputStream();
            return new String(readFrom(input), charset);
        } finally {
            if(input != null) {
                input.close();
            }
        }
    }
    
    private static byte[] readFrom(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while((b = in.read()) != -1) {
            out.write(b);
        }
        return out.toByteArray();
    }
    
    private static String readRespMessage(HttpURLConnection conn, Charset charset) throws IOException {
        InputStream input = null;
        try {
            input = conn.getErrorStream();
            return new String(readFrom(input), charset);
        } finally {
            if(input != null) {
                input.close();
            }
        }
    }

    private static void writeReqData(HttpURLConnection conn, byte[] postData) throws IOException {
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        dos.write(postData);
        dos.flush();
        dos.close();
    }

    private static void addHeaders(HttpURLConnection conn, Header... headers) {
        if(headers == null){
            return;
        }
        for (Header header : headers) {
            if(header == null){
                continue;
            }
            conn.addRequestProperty(header.key, header.value);
        }
    }
    
    private static boolean httpsNotConfigured = true;
    private static HttpURLConnection buildConn(String url, String method, boolean doOutput) throws Exception, IOException {
        URL netURL = new URL(url);
        if(httpsNotConfigured && "https".equals(netURL.getProtocol())) {
            configureHttpsVerifierAndCertificates();
            httpsNotConfigured = false;
        }
        return buildHttpConn(netURL, method, doOutput);
    }
    
    private static HttpURLConnection buildHttpConn(URL url, String method, boolean doOutput) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(timeout);
        conn.setRequestMethod(method);
        conn.setDoOutput(doOutput);
        conn.setDoInput(true);
        return conn;
    }
    
    private static void configureHttpsVerifierAndCertificates() throws Exception {
        configureHttpsVerifier();
        configureHttpsCertificates();
    }

    private static void configureHttpsVerifier() {
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
    
    private static void configureHttpsCertificates() throws Exception {
        //  Create a trust manager that does not validate certificate chains:
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        
        javax.net.ssl.TrustManager tm = new MiTM();

        trustAllCerts[0] = tm;

        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");

        sc.init(null, trustAllCerts, null);

        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    
    public static class MiTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }
        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) throws
                java.security.cert.CertificateException {
            return;
        }
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType) throws
                java.security.cert.CertificateException {
            return;
        }
    }

}
