package com.automation.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.automation.config.Config;
import com.google.gson.Gson;

public class XrayClient {

    private static final Gson gson = new Gson();

    private static String authToken = null;
    // Pending test results to be batched when no explicit execution key is provided
    private static final List<Map<String, String>> pendingTests = new ArrayList<>();

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
        updateTestCaseStatus(testKey, status, "");
    }

    /**
     * Update a single test case status in Xray. If `executionKey` is provided it will be used to update that Test Execution, otherwise projectKey (config) will be used to create an execution.
     */
    public static void updateTestCaseStatus(String testKey, String status, String executionKey) {
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
            // Build test execution info
            Map<String, String> testExecution = new HashMap<>();
            if (executionKey != null && !executionKey.isEmpty()) {
                testExecution.put("key", executionKey);
                bodyMap.put("testExecution", testExecution);
            } else {
                String projectKey = Config.getProperty("xray.project.key");
                if (projectKey != null && !projectKey.isEmpty()) {
                    testExecution.put("projectKey", projectKey);
                    testExecution.put("summary", "Automated execution from CI");
                    bodyMap.put("testExecution", testExecution);
                }
            }

            // tests array with status
            Map<String, String> testObj = new HashMap<>();
            testObj.put("testKey", testKey);
            testObj.put("status", status);
            bodyMap.put("tests", new Object[] { testObj });

            // If executionKey provided, try the exact schema supplied by the Xray docs first
            if (executionKey != null && !executionKey.isEmpty()) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("testExecutionKey", executionKey);

                Map<String, Object> infoBlock = new HashMap<>();
                infoBlock.put("summary", "Execution of automated tests for run");
                infoBlock.put("description", "Automated import from test run");
                infoBlock.put("startDate", OffsetDateTime.now().toString());
                infoBlock.put("finishDate", OffsetDateTime.now().toString());
                payload.put("info", infoBlock);

                Map<String, Object> testEntry = new HashMap<>();
                testEntry.put("testKey", testKey);
                testEntry.put("start", OffsetDateTime.now().toString());
                testEntry.put("finish", OffsetDateTime.now().toString());
                testEntry.put("comment", "Automated execution result");
                testEntry.put("status", status);

                payload.put("tests", new Object[] { testEntry });

                String exactJson = gson.toJson(payload);
                System.out.println("🔎 Trying Xray exact-schema payload: " + exactJson);
                HttpURLConnection connExec = (HttpURLConnection) url.openConnection();
                connExec.setRequestMethod("POST");
                connExec.setRequestProperty("Content-Type", "application/json");
                connExec.setRequestProperty("Authorization", "Bearer " + token);
                connExec.setDoOutput(true);
                if (trySendPayload(connExec, exactJson)) {
                    return;
                }
                connExec.disconnect();
            }

            // Prepare candidate payloads to try common Xray schemas
            List<String> candidates = new ArrayList<>();

            // 1) current schema: { tests:[{testKey, status}], testExecution:{key:...} }
            String json1 = gson.toJson(bodyMap);
            candidates.add(json1);

            // 2) alternative: top-level testExecutionKey and tests (use List to ensure proper JSON)
            Map<String, Object> alt2 = new HashMap<>();
            alt2.put("testExecutionKey", executionKey != null && !executionKey.isEmpty() ? executionKey : Config.getProperty("xray.project.key"));
            List<Map<String, String>> alt2Tests = new ArrayList<>();
            Map<String, String> tmap = new HashMap<>();
            tmap.put("testKey", testKey);
            tmap.put("status", status);
            alt2Tests.add(tmap);
            alt2.put("tests", alt2Tests);
            candidates.add(gson.toJson(alt2));

            // 3) use statusName instead of status
            Map<String, Object> alt3 = new HashMap<>();
            if (executionKey != null && !executionKey.isEmpty()) {
                Map<String, String> execRef = new HashMap<>();
                execRef.put("key", executionKey);
                alt3.put("testExecution", execRef);
            }
            List<Map<String, String>> alt3Tests = new ArrayList<>();
            Map<String, String> tmap3 = new HashMap<>();
            tmap3.put("testKey", testKey);
            tmap3.put("statusName", status);
            alt3Tests.add(tmap3);
            alt3.put("tests", alt3Tests);
            candidates.add(gson.toJson(alt3));

            // 4) wrap with info + tests
            Map<String, Object> alt4 = new HashMap<>();
            Map<String, String> info4 = new HashMap<>();
            info4.put("summary", "Automated run");
            alt4.put("info", info4);
            List<Map<String, String>> alt4Tests = new ArrayList<>();
            Map<String, String> tmap4 = new HashMap<>();
            tmap4.put("testKey", testKey);
            tmap4.put("status", status);
            alt4Tests.add(tmap4);
            alt4.put("tests", alt4Tests);
            candidates.add(gson.toJson(alt4));

            boolean sent = false;
            for (String candidate : candidates) {
                System.out.println("🔎 Trying Xray payload variant: " + candidate);
                if (trySendPayload(conn, candidate)) {
                    sent = true;
                    break;
                }
                // reset connection for next attempt
                conn.disconnect();
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);
            }
            if (!sent) {
                System.out.println("❌ All payload variants failed for " + testKey);
            }

        } catch (Exception e) {
            System.out.println("❌ Xray update exception for " + testKey + ": " + e.getMessage());
            authToken = null;
        }
    }

    /**
     * Try sending a JSON payload on the provided connection. Returns true on 2xx.
     */
    private static boolean trySendPayload(HttpURLConnection conn, String json) {
        try {
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            int code = conn.getResponseCode();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    code >= 200 && code < 400 ? conn.getInputStream() : conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                String resp = sb.toString().trim();
                if (code >= 200 && code < 300) {
                    System.out.println("✅ Xray accepted payload. Response: " + resp);
                    return true;
                } else {
                    System.out.println("❌ Xray rejected payload: " + resp);
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Exception sending/reading Xray payload: " + e.getMessage());
            return false;
        }
    }

    /**
     * Queue a test result to be sent later in a single batch when no execution key is provided.
     */
    public static synchronized void queueTestResult(String testKey, String status) {
        Map<String, String> entry = new HashMap<>();
        entry.put("testKey", testKey);
        entry.put("status", status);
        pendingTests.add(entry);
    }

    /**
     * Flush the queued test results in a single `/import/execution` call. Uses `xray.project.key` if configured to create the execution.
     */
    public static synchronized void flushBatch() {
        if (pendingTests.isEmpty()) return;

        try {
            String base = Config.getProperty("xray.api.baseurl");
            if (base == null || base.isEmpty()) {
                System.out.println("⚠️ Xray base URL not configured. Skipping batch update.");
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
            // include project key if present so Xray will create a Test Execution under that project
            String projectKey = Config.getProperty("xray.project.key");
            if (projectKey != null && !projectKey.isEmpty()) {
                Map<String, String> testExecution = new HashMap<>();
                testExecution.put("projectKey", projectKey);
                testExecution.put("summary", "Automated execution from CI (batched)");
                bodyMap.put("testExecution", testExecution);
            }

            // build tests array
            Object[] testsArray = pendingTests.toArray(new Object[0]);
            bodyMap.put("tests", testsArray);

            // Prepare several candidate batch payloads to maximize compatibility with different Xray API variants
            List<String> candidates = new ArrayList<>();
            // 1) current schema
            candidates.add(gson.toJson(bodyMap));

            // 2) top-level testExecutionKey using List
            Map<String, Object> b2 = new HashMap<>();
            b2.put("testExecutionKey", Config.getProperty("xray.project.key"));
            b2.put("tests", new ArrayList<>(pendingTests));
            candidates.add(gson.toJson(b2));

            // 3) info + tests
            Map<String, Object> b3 = new HashMap<>();
            Map<String, String> info3 = new HashMap<>();
            info3.put("summary", "Automated batched run");
            b3.put("info", info3);
            b3.put("tests", new ArrayList<>(pendingTests));
            candidates.add(gson.toJson(b3));

            boolean sent = false;
            for (String candidate : candidates) {
                System.out.println("🔎 Trying Xray batched payload variant: " + candidate);
                if (trySendPayload(conn, candidate)) {
                    sent = true;
                    break;
                }
                conn.disconnect();
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);
            }
            if (sent) pendingTests.clear();

        } catch (Exception e) {
            System.out.println("❌ Xray batched update exception: " + e.getMessage());
            authToken = null;
        }
    }
}
