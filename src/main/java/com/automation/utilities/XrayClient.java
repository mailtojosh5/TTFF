package com.automation.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.automation.config.Config;
import com.google.gson.Gson;

public class XrayClient {

    private static final Gson gson = new Gson();

    private static String authToken = null;

    private static synchronized String authenticate() {
        try {
            String base = Config.getProperty("xray.api.baseurl");
            String clientId = Config.getProperty("xray.client.id");
            String clientSecret = Config.getProperty("xray.client.secret");
            if (base == null || base.isEmpty() || clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
                System.out.println("⚠️ Xray credentials/base URL not configured. Skipping Xray update.");
                return null;
            }

            URL url = new URL(base.endsWith("/") ? base + "api/v1/authenticate" : base + "/api/v1/authenticate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            Map<String, String> payload = new HashMap<>();
            payload.put("client_id", clientId);
            payload.put("client_secret", clientSecret);
            String body = gson.toJson(payload);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    code >= 200 && code < 400 ? conn.getInputStream() : conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                String resp = sb.toString().trim();
                if (code >= 200 && code < 300) {
                    // token may be returned as a JSON string or plain token
                    if (resp.startsWith("\"")) {
                        authToken = gson.fromJson(resp, String.class);
                    } else if (resp.startsWith("{") && resp.contains("token")) {
                        Map<?,?> map = gson.fromJson(resp, Map.class);
                        Object tok = map.get("token");
                        authToken = tok != null ? tok.toString() : null;
                    } else {
                        authToken = resp;
                    }
                    return authToken;
                } else {
                    System.out.println("❌ Xray authentication failed: " + resp);
                    return null;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Xray authentication exception: " + e.getMessage());
            return null;
        }
    }

    /**
     * Update a single test case status in Xray. The `status` value should follow Xray expected statuses (e.g. PASSED/FAILED).
     */
    public static void updateTestCaseStatus(String testKey, String status) {
        try {
            String base = Config.getProperty("xray.api.baseurl");
            if (base == null || base.isEmpty()) {
                System.out.println("⚠️ Xray base URL not configured. Skipping update for " + testKey);
                return;
            }

            String token = authToken != null ? authToken : authenticate();
            if (token == null || token.isEmpty()) return;

            URL url = new URL(base.endsWith("/") ? base + "api/v1/import/execution" : base + "/api/v1/import/execution");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setDoOutput(true);

            Map<String, Object> bodyMap = new HashMap<>();
            // minimal payload: provide tests array with status
            Map<String, String> testObj = new HashMap<>();
            testObj.put("testKey", testKey);
            testObj.put("status", status);
            bodyMap.put("tests", new Object[] { testObj });

            String body = gson.toJson(bodyMap);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    code >= 200 && code < 400 ? conn.getInputStream() : conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                String resp = sb.toString().trim();
                if (code >= 200 && code < 300) {
                    System.out.println("✅ Xray updated " + testKey + " -> " + status + ". Response: " + resp);
                } else {
                    System.out.println("❌ Xray update failed for " + testKey + ": " + resp);
                    // invalidate token so next call re-authenticates
                    authToken = null;
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Xray update exception for " + testKey + ": " + e.getMessage());
            authToken = null;
        }
    }
}
