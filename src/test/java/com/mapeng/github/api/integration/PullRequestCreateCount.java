package com.mapeng.github.api.integration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;

public class PullRequestCreateCount {

    /**
     * 在上面的示例中，我们定义了一个GithubApiExample类，其中包含了一个main方法和三个辅助方法。
     * 在main方法中，我们首先定义了一个包含多个用户名的字符串数组，并循环遍历每个用户名，
     * 分别调用countPullRequests方法来查询该用户在过去一个月内提交的Pull Requests数量。
     * 在countPullRequests方法中，我们使用Github API的search/issues endpoint来查询该用户
     * 在所有repo中在过去一个月内提交的Pull Requests数量。最后，我们调用getPullRequestCount方法
     * 来解析API返回的JSON数据，获取该用户在所有repo中在过去一个月内提交的Pull Requests数量。
     *
     * 需要注意的是，示例中需要将YOUR_ACCESS_TOKEN替换成实际的Github Access Token。同时，
     * 示例中使用了Java 8中的LocalDate库来获取过去一个月的时间戳，并使用了Github API的search/issues endpoint
     * 来查询所有repo中的Pull Requests数量。
     */

    private static final String BASE_URL = "https://api.github.com/";

    public static void main(String[] args) {
        String[] users = {"user1", "user2", "user3"};

        for (String user : users) {
            int pullRequestCount = countPullRequests(user);
            System.out.println(user + " submitted " + pullRequestCount + " pull requests in the last month");
        }
    }

    private static int countPullRequests(String user) {
        String prJson = getJsonFromApi("search/issues?q=is:pr+author:" + user + "+updated:>" + LocalDate.now().minusMonths(1));
        return getPullRequestCount(prJson);
    }

    private static String getJsonFromApi(String path) {
        try {
            URL url = new URL(BASE_URL + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "token YOUR_ACCESS_TOKEN");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestMethod("GET");

            return getResponseContent(connection);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getResponseContent(HttpURLConnection connection) throws IOException {
        Scanner scanner = new Scanner(connection.getInputStream());
        scanner.useDelimiter("\\A");

        StringBuilder response = new StringBuilder();
        while (scanner.hasNextLine()) {
            response.append(scanner.nextLine());
        }
        scanner.close();
        return response.toString();
    }

    private static int getPullRequestCount(String prJson) {
        return Integer.parseInt(prJson.split("\"total_count\":")[1].split(",")[0]);
    }

}

