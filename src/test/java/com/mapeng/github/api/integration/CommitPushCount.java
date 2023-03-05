package com.mapeng.github.api.integration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


public class CommitPushCount {
    public static void main(String[] args) throws Exception {
        // Github API endpoint for "List user events"
        String endpoint = "https://api.github.com/users/%s/events?per_page=100";

        // Github API access token
        String token = "YOUR_GITHUB_API_TOKEN";

        // List of Github usernames to query
        String[] usernames = {"USER1", "USER2", "USER3"};

        // Date range (last 1 month)
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusMonths(1);

        // HTTP request headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        headers.put("Accept", "application/vnd.github.v3+json");

        // Loop through the list of usernames and query the Github API
        for (String username : usernames) {
            String urlStr = String.format(endpoint, username);
            int pageCount = 1;
            int commitCount = 0;
            while (true) {
                // Build the HTTP request URL
                String queryParams = String.format("&page=%d&since=%s&until=%s",
                        pageCount, fromDate.format(DateTimeFormatter.ISO_DATE),
                        toDate.format(DateTimeFormatter.ISO_DATE));
                URL url = new URL(urlStr + queryParams);

                // Send the HTTP request and read the response
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                for (String key : headers.keySet()) {
                    conn.setRequestProperty(key, headers.get(key));
                }
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new Exception("Failed to fetch user events: " + responseCode);
                }
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                conn.disconnect();

                // Parse the JSON response and count the number of "PushEvent" events
                JSONArray events = new JSONArray(response.toString());
                if (events.length() == 0) {
                    break;
                }
                for (int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    String eventType = event.getString("type");
                    if ("PushEvent".equals(eventType)) {
                        commitCount += event.getJSONObject("payload").getJSONArray("commits").length();
                    }
                }

                // Move to the next page of events
                pageCount++;
            }

            // Output the results for this user
            System.out.printf("%s: %d\n", username, commitCount);
        }
    }
}
