package com.mapeng.github.api.integration;

import okhttp3.*;
import org.json.*;

import java.io.IOException;
import java.time.LocalDate;

public class GenerateFormToConfluence {

    /**
     * 需要注意的是，这段代码中的 `YOUR_AUTH_TOKEN` 需要替换为自己的Confluence OAuth2 Token，用于进行API认证。
     * 另外，也需要替换 `YOUR_SPACE_KEY` 为要创建页面的空间键，以及 `pageTitle` 和 `tableTitle` 分别为要创建
     * 页面的标题和表格标题。
     * 在这个例子中，我们使用了 `okhttp3` 库来发送HTTP请求，并使用了 `org.json` 库来解析返回的JSON数据。
     * 首先，我们调用 `generateTableContent` 方法来生成包含用户数据的HTML表格。该方法会循环遍历指定的用户列表，
     * 调用 `getReviewCountForUser` 方法来获取每个用户在过去一个月内Review PR的次数，并将其写入表格中。
     * `getReviewCountForUser` 方法则是调用GitHub API来查询指定用户在过去一个月内Review PR的次数。
     * 这里我们使用了GitHub的搜索API来查询所有已关闭的PR，并筛选出被指定用户Review过的PR。通过解析API返回的JSON数据，
     * 我们可以获取到指定用户在过去一个月内Review PR的次数。
     *
     * 最后，我们调用 `createPage` 方法来创建Confluence页面，并将生成的表格写入到页面中。该方法会构造一个API请求，
     * 并发送到Confluence服务器上，以创建新页面。在该请求中，我们使用了包含标题和表格内容的JSON字符串作为请求体，
     * 并指定了Content-Type为application/json。
     *
     * 一旦页面创建成功，我们可以从API返回的JSON数据中获取到页面ID，以便后续的页面操作。

     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String[] users = {"user1", "user2", "user3"}; // 指定用户列表
        String authToken = "YOUR_AUTH_TOKEN"; // 替换为自己的OAuth2 Token
        String spaceKey = "YOUR_SPACE_KEY"; // 替换为要创建页面的空间键
        String pageTitle = "My Page"; // 替换为要创建页面的标题
        String tableTitle = "User Review Counts"; // 表格标题

        // 获取用户数据并生成表格
        String tableContent = generateTableContent(users, authToken);

        // 创建Confluence页面
        createPage(spaceKey, pageTitle, tableTitle, tableContent, authToken);
    }

    private static String generateTableContent(String[] users, String authToken) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr><th>User</th><th>Review Count</th></tr>");

        for (String user : users) {
            int reviewCount = getReviewCountForUser(user, authToken);
            sb.append("<tr><td>").append(user).append("</td><td>").append(reviewCount).append("</td></tr>");
        }

        sb.append("</table>");
        return sb.toString();
    }

    private static int getReviewCountForUser(String user, String authToken) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // 构造API请求
        String url = String.format("https://api.github.com/search/issues?q=is:pr+is:closed+reviewed-by:%s+created:>=%s",
                user, LocalDate.now().minusMonths(1));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

        // 发送API请求并解析返回的JSON数据
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject json = new JSONObject(responseBody);
        int totalCount = json.getInt("total_count");

        return totalCount;
    }

    private static void createPage(String spaceKey, String pageTitle, String tableTitle,
                                   String tableContent, String authToken) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // 构造API请求
        String url = "https://your-confluence-site.com/rest/api/content";
        JSONObject requestBody = new JSONObject()
                .put("type", "page")
                .put("title", pageTitle)
                .put("space", new JSONObject().put("key", spaceKey))
                .put("body", new JSONObject()
                        .put("storage", new JSONObject()
                                .put("value", "<h1>" + tableTitle + "</h1>" + tableContent)
                                .put("representation", "storage")));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + authToken)
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        // 发送API请求并解析返回的JSON数据
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject json = new JSONObject(responseBody);
        String pageId = json.getString("id");

        System.out.println("Page created successfully with ID: " + pageId);
    }
}

