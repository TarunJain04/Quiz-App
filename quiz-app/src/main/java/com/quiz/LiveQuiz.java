package com.quiz;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LiveQuiz {

    public static void main(String[] args) {

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://opentdb.com/api.php?amount=10&category=18&type=multiple&encode=base64"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("API request failed");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());

            int responseCode = root.get("response_code").asInt();
            if (responseCode != 0) {
                System.out.println("API error code: " + responseCode);
                return;
            }

            JsonNode results = root.get("results");
            if (results == null || !results.isArray() || results.size() == 0) {
                System.out.println("No questions received");
                return;
            }

            int qNo = 0;
            int score = 0;
            decodeAll(results);
            for (JsonNode q : results) {

                System.out.println("\nQ" + ++qNo + ": " + q.get("question").asText());

                JsonNode incorrect = q.get("incorrect_answers");
                String correct = q.get("correct_answer").asText();
                List<String> options = new ArrayList<>();
                incorrect.forEach(ans -> options.add(ans.asText()));
                options.add(correct);
                Collections.shuffle(options);
                char correctOption = (char) ('A' + options.indexOf(correct));
                for (int i = 0; i < options.size(); i++) {
                    System.out.println((char) ('A' + i) + ": " + options.get(i));
                }
                System.out.print("Your answer: ");
                Scanner sc = new Scanner(System.in);
                String userAns = sc.nextLine().trim().toUpperCase();
                if (userAns.length() == 1 && userAns.charAt(0) >= 'A' && userAns.charAt(0) < 'A' + options.size()) {
                    if (userAns.charAt(0) == correctOption) {
                        System.out.println("Correct!");
                        score++;
                    } else {
                        System.out.println("Wrong! Correct answer was: " + correctOption);
                    }
                } else {
                    System.out.println("Invalid input. Correct answer was: " + correctOption);
                }

            }
            System.out.println("\nYour final score: " + score + "/" + qNo);
            saveToFile(root, mapper);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String decode(String text) {
        return new String(Base64.getDecoder().decode(text));
    }

    private static void saveToFile(JsonNode data, ObjectMapper mapper) {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File("src/main/resources/live-quiz-buffer.json"), data);

            System.out.println("\nQuestions saved to src/main/resources/live-quiz-buffer.json");
        } catch (Exception e) {
            System.out.println("Failed to save questions");
        }
        java.util.TreeMap<String, Object> map;
    }

    private static void decodeAll(JsonNode node) {

        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            obj.fields().forEachRemaining(entry -> {
                JsonNode value = entry.getValue();
                if (value.isTextual()) {
                    obj.put(entry.getKey(), decode(value.asText()));
                } else {
                    decodeAll(value); // recursive for nested objects/arrays
                }
            });
        } else if (node.isArray()) {
            ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                JsonNode element = arr.get(i);
                if (element.isTextual()) {
                    arr.set(i, TextNode.valueOf(decode(element.asText())));
                } else {
                    decodeAll(element); // recursive for nested objects/arrays
                }
            }
        }
        // ValueNode (numbers, booleans, null) are ignored
    }
}