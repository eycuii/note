## Java 访问 HTTPS 忽略证书

```java
import java.io.ByteArrayOutputStream;
import java.io.IOException;  
import java.io.InputStream;
import java.io.UnsupportedEncodingException;  
import java.net.HttpURLConnection;  
import java.net.MalformedURLException;  
import java.net.URL;  

import javax.net.ssl.TrustManager;  
import javax.net.ssl.X509TrustManager;  

import java.security.SecureRandom;
import java.security.cert.X509Certificate;  

import javax.net.ssl.SSLContext;  
import javax.net.ssl.HttpsURLConnection;  
import javax.net.ssl.HostnameVerifier;  
import javax.net.ssl.SSLSession;  

public class HttpDemo {  

	private static final String HTTPS_URL = "https://xxx?aa=bb";  
	
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {  
		public boolean verify(String hostname, SSLSession session) {  
			return true;  
		}  
	};  

    public static void httpGet() {  
    	HttpURLConnection conn = null;  
    	try {  
    		// Create a trust manager that does not validate certificate chains  
            trustAllHosts();  
      
            URL url = new URL(HTTPS_URL);  
              
            HttpsURLConnection https = (HttpsURLConnection)url.openConnection();  
            if (url.getProtocol().toLowerCase().equals("https")) {  
                https.setHostnameVerifier(DO_NOT_VERIFY);  
                conn = https;  
            } else {  
                conn = (HttpURLConnection)url.openConnection();  
            }  
    		conn.setDoOutput(true);
            conn.connect();  
            System.out.println(conn.getResponseCode() + " " + conn.getResponseMessage());  
            
            // 输出内容
    		InputStream is = conn.getInputStream();
    		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    		int len = -1;
    		byte[] buffer = new byte[1024];
    		while ((len = is.read(buffer)) != -1) {
    			outputStream.write(buffer, 0, len);
    		}
    		byte[] data = outputStream.toByteArray();
    		if (is != null) {
    			is.close();
    		}
    		
            System.out.println(new String(data, "utf-8"));  
            
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
      
    /** 
	 * Trust every server - dont check for any certificate 
	 */  
	private static void trustAllHosts() {  
	    // Create a trust manager that does not validate certificate chains  
	    TrustManager[] trustAllCerts = new TrustManager[] {
	    		new X509TrustManager() {
			        public X509Certificate[] getAcceptedIssuers() {  
			            return new java.security.cert.X509Certificate[] {};  
			        }
			        public void checkClientTrusted(X509Certificate[] chain, String authType)  {  
			        }
			        public void checkServerTrusted(X509Certificate[] chain, String authType) {  
			        }
			    }
	    };
	  
	    // Install the all-trusting trust manager  
	    try {
	        SSLContext sc = SSLContext.getInstance("TLS");  
	        sc.init(null, trustAllCerts, new SecureRandom());  
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
	}  

    public static void main(String[] args) {  
        httpGet();  
    }  
}
```

