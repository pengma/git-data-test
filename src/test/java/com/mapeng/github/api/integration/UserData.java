package com.mapeng.github.api.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;


public class UserData {

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(new File("user.json"), Map.class);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static void readJson(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File("user.json"));
        for (JsonNode user : root.path("users")) {
            if (user.path("name").asText().equals("Alice")) {
                int age = user.path("age").asInt();
                System.out.println("Alice's age is " + age);
            }
        }
    }
}
