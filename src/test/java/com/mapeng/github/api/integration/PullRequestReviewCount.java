package com.mapeng.github.api.integration;

import okhttp3.*;
import org.json.*;

import java.io.IOException;
import java.time.LocalDate;

public class PullRequestReviewCount {

    public static void main(String[] args) throws IOException {
        String[] users = {"user1", "user2", "user3"}; // 指定用户列表
        String authToken = "YOUR_AUTH_TOKEN"; // 替换为自己的OAuth2 Token

        for (String user : users) {
            int reviewCount = getReviewCountForUser(user, authToken); // 查询该用户过去一个月内Review PR的次数
            System.out.println(user + " reviewed " + reviewCount + " PRs in the last month.");
        }
    }

    private static int getReviewCountForUser(String user, String authToken) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // 构造API请求
        String url = String.format("https://api.github.com/search/issues?q=is:pr+is:closed+reviewed-by:%s+created:>=%s",
                user, LocalDate.now().minusMonths(1));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("Authorization", "Bearer " + authToken) // 加入Authorization信息
                .build();

        // 发送API请求并解析返回的JSON数据
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject json = new JSONObject(responseBody);

        return json.getInt("total_count");
    }

}

