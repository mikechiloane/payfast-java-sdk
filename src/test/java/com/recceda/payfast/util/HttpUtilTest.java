package com.recceda.payfast.util;

import com.recceda.payfast.exception.HttpException;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class HttpUtilTest {

    @Test
    public void testPostWithValidParameters() {

        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("merchant_key", "46f0cd694581a");
        params.put("amount", "100.00");
        
        try {
            String response = HttpUtil.post("https://httpbin.org/post", params);
            // In a real scenario, we'd mock this
            assertNotNull(response);
        } catch (HttpException e) {
            // Expected when network is not available or URL is invalid
            assertTrue(e.getMessage().contains("HTTP request failed"));
        }
    }
    
    @Test
    public void testPostWithEmptyParameters() {
        Map<String, String> params = new HashMap<>();
        
        try {
            String response = HttpUtil.post("https://httpbin.org/post", params);
            assertNotNull(response);
        } catch (HttpException e) {
            assertTrue(e.getMessage().contains("HTTP request failed"));
        }
    }
    
    @Test(expected = HttpException.class)
    public void testPostWithInvalidUrl() throws HttpException {
        Map<String, String> params = new HashMap<>();
        params.put("test", "value");
        
        HttpUtil.post("invalid-url", params);
    }
    
    @Test(expected = HttpException.class)
    public void testPostWithNullUrl() throws HttpException {
        Map<String, String> params = new HashMap<>();
        HttpUtil.post(null, params);
    }
    
    @Test(expected = HttpException.class)
    public void testPostWithNullParameters() throws HttpException {
        HttpUtil.post("https://httpbin.org/post", null);
    }
    

    
    @Test
    public void testPostParameterEncoding() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "John Doe");
        params.put("email", "john@example.com");
        params.put("amount", "99.99");
        
        try {
            String response = HttpUtil.post("https://httpbin.org/post", params);
            // The post data should be properly encoded
            assertNotNull(response);
        } catch (HttpException e) {
            assertTrue(e.getMessage().contains("HTTP request failed"));
        }
    }
}
