package com.recceda.payfast.util;

import com.recceda.payfast.exception.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
    
    public static String post(String url, Map<String, String> params) throws HttpException {
        if (url == null || url.trim().isEmpty()) {
            throw new HttpException("URL cannot be null or empty");
        }
        if (params == null) {
            throw new HttpException("Parameters cannot be null");
        }
        
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (postData.length() > 0) postData.append("&");
                postData.append(entry.getKey()).append("=").append(entry.getValue());
            }
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.toString().getBytes());
            }
            
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            
            return response.toString();
        } catch (Exception e) {
            log.error("HTTP request failed for URL: {}", url, e);
            throw new HttpException("HTTP request failed", e);
        }
    }
}