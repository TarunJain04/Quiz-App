package com.quiz;

import java.util.*;
import java.util.stream.Stream;
import java.io.File;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class Question {
    protected static Scanner sc = new Scanner(System.in);
    public String question;
    public String category;
    public String difficulty;
    public String type;

    public void display() {
        System.out.printf("Category: %s | Difficulty: %s | Type: %s\n", category, difficulty, type);
    }

    public abstract boolean attempt();
}

class MCQ extends Question {
    public List<String> options;
    public int answer;

    @Override
    public void display() {
        super.display();
        System.out.println("Question: " + question);
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
    }

    public boolean attempt() {
        System.out.print("Enter your answer (1-" + options.size() + "): ");
        int userAnswer;
        try {
            userAnswer = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter a number.");
            sc.nextLine();
            return false;
        }
        sc.nextLine();
        boolean isCorrect = (userAnswer == answer);
        if (isCorrect) {
            System.out.println("Correct!");
        } else if (userAnswer < 1 || userAnswer > options.size()) {
            System.out.println("Invalid input! Please enter a number between 1 and " + options.size() + ".");
        } else {
            System.out.println("Incorrect! The correct answer is: " + answer);
        }
        return isCorrect;
    }
}

class OpenEnded extends Question {
    public String answer;

    @Override
    public void display() {
        super.display();
        System.out.println("Question: " + question);
    }

    public boolean attempt() {
        System.out.print("Enter your answer: ");
        String userAnswer = sc.nextLine().trim();
        boolean isCorrect = userAnswer.equalsIgnoreCase(answer);
        if (isCorrect) {
            System.out.println("Correct!");
        } else {
            System.out.println("Incorrect! The correct answer is: " + answer);
        }
        return isCorrect;
    }
}

class MSQ extends Question {
    public List<String> options;
    public Set<Integer> answers;

    @Override
    public void display() {
        super.display();
        System.out.println("Question: " + question);
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
        System.out.println("Select all correct options (space separated, e.g., 1 3): ");
    }

    @Override
    public boolean attempt() {
        boolean isCorrect = true;
        for (int i = 0; i < answers.size(); i++) {
            int userAnswer;
            try {
                userAnswer = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                isCorrect = false;
                break;
            }
            if (userAnswer < 1 || userAnswer > options.size()) {
                System.out.println("Invalid input! Please enter a number between 1 and " + options.size() + ".");
                isCorrect = false;
                break;
            } else if (!answers.contains(userAnswer)) {
                isCorrect = false;
            }
        }
        if (isCorrect) {
            System.out.println("Correct!");
        } else {
            System.out.println("Incorrect! The correct solution is: " + answers);
        }
        sc.nextLine();
        return isCorrect;
    }
}

class MTF extends Question {
    public List<String> left;
    public List<String> right;
    public List<Integer> matches;

    @Override
    public void display() {
        super.display();
        System.out.println("Question: " + question);
        System.out.println("Left:");
        for (int i = 0; i < left.size(); i++) {
            System.out.print((i + 1) + ". " + left.get(i) + "  ");
        }
        System.out.println();
        System.out.println("Right:");
        for (int i = 0; i < right.size(); i++) {
            System.out.print((i + 1) + ". " + right.get(i) + "  ");
        }
        System.out.println();
        System.out.println("Enter your matches (e.g., 1 2 3 4): ");
    }

    @Override
    public boolean attempt() {
        boolean isCorrect = true;
        for (int i = 0; i < matches.size(); i++) {
            int userAnswer;
            try {
                userAnswer = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                isCorrect = false;
                break;
            }
            if (userAnswer < 1 || userAnswer > right.size()) {
                System.out.println("Invalid input! Please enter a number between 1 and " + right.size() + ".");
                isCorrect = false;
                break;
            } else if (userAnswer != matches.get(i)) {
                isCorrect = false;
            }
        }
        if (isCorrect) {
            System.out.println("Correct!");
        } else {
            System.out.println("Incorrect! The correct matching is: " + matches);
        }
        sc.nextLine();
        return isCorrect;
    }
}

class Ordering extends Question {
    public List<String> options;
    public List<Integer> correctOrder;

    @Override
    public void display() {
        super.display();
        System.out.println("Question: " + question);
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
        System.out.println("Enter the correct order of options (e.g., 3 1 4 2): ");
    }

    @Override
    public boolean attempt() {
        boolean isCorrect = true;
        for (int i = 0; i < correctOrder.size(); i++) {
            int userAnswer;
            try {
                userAnswer = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                isCorrect = false;
                break;
            }
            if (userAnswer < 1 || userAnswer > options.size()) {
                System.out.println("Invalid input! Please enter a number between 1 and " + options.size() + ".");
                isCorrect = false;
                break;
            } else if (userAnswer != correctOrder.get(i)) {
                isCorrect = false;
            }
        }
        if (isCorrect) {
            System.out.println("Correct!");
        } else {
            System.out.println("Incorrect! The correct order is: " + correctOrder);
        }
        sc.nextLine();
        return isCorrect;
    }
}

class Quiz {
    private int MAX_QUESTIONS;
    private int EASY_COUNT;
    private int MEDIUM_COUNT;
    private int HARD_COUNT;
    private List<Question> questions;
    private int score;
    private static final Map<String, Integer> difficultyOrder = Map.of(
            "easy", 1,
            "medium", 2,
            "hard", 3);

    public Quiz(int n) {
        this.MAX_QUESTIONS = n;
        this.EASY_COUNT = (int) Math.round(MAX_QUESTIONS * 0.4);
        this.MEDIUM_COUNT = (int) Math.round(MAX_QUESTIONS * 0.3);
        this.HARD_COUNT = MAX_QUESTIONS - EASY_COUNT - MEDIUM_COUNT;
    }

    /*
     * // Dynamically load questions from JSON file
     * public void loadQuestions(String filename) throws Exception {
     * ObjectMapper mapper = new ObjectMapper();
     * JsonNode root = mapper.readTree(new File(filename));
     * questions = new ArrayList<>();
     * HashSet<Integer> qIds = new HashSet<>();
     * Random random = new Random();
     * MAX_QUESTIONS = Math.min(MAX_QUESTIONS, root.size());
     * while (qIds.size() < MAX_QUESTIONS) {
     * int index = random.nextInt(root.size());
     * if (qIds.contains(index)) {
     * continue;
     * }
     * qIds.add(index);
     * JsonNode questionNode = root.get(index);
     * String type = questionNode.get("type").asText();
     * Question question = null;
     * switch (type.toLowerCase()) {
     * case "mcq":
     * case "tfq":
     * question = mapper.treeToValue(questionNode, MCQ.class);
     * break;
     * case "open_ended":
     * question = mapper.treeToValue(questionNode, OpenEnded.class);
     * break;
     * }
     * questions.add(question);
     * }
     * questions.sort((a, b) ->
     * difficultyOrder.getOrDefault(a.difficulty.toLowerCase(), 0) -
     * difficultyOrder.getOrDefault(b.difficulty.toLowerCase(), 0));
     * }
     */
    public void loadQuestions(String filename) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filename));

        // separate by difficulty
        List<JsonNode> easy = new ArrayList<>();
        List<JsonNode> medium = new ArrayList<>();
        List<JsonNode> hard = new ArrayList<>();

        for (JsonNode node : root) {
            switch (node.get("difficulty").asText().toLowerCase()) {
                case "easy" -> easy.add(node);
                case "medium" -> medium.add(node);
                case "hard" -> hard.add(node);
            }
        }
        // shuffle each group
        Collections.shuffle(easy);
        Collections.shuffle(medium);
        Collections.shuffle(hard);

        // pick proportionate counts
        int easyCount = Math.min(EASY_COUNT, easy.size());
        int mediumCount = Math.min(MEDIUM_COUNT, medium.size());
        int hardCount = Math.min(HARD_COUNT, hard.size());
        questions = new ArrayList<>();

        Stream.of(
                easy.subList(0, easyCount),
                medium.subList(0, mediumCount),
                hard.subList(0, hardCount)).flatMap(List::stream)
                .map(node -> parseQuestion(node, mapper))
                .filter(q -> q != null)
                .sorted((a, b) -> difficultyOrder.getOrDefault(a.difficulty.toLowerCase(), 0) -
                        difficultyOrder.getOrDefault(b.difficulty.toLowerCase(), 0))
                .forEach(questions::add);
    }

    private Question parseQuestion(JsonNode node, ObjectMapper mapper) {
        try {
            return switch (node.get("type").asText().toLowerCase()) {
                case "mcq", "tfq" -> mapper.treeToValue(node, MCQ.class);
                case "open_ended", "fill", "guess the output" -> mapper.treeToValue(node, OpenEnded.class);
                case "msq" -> mapper.treeToValue(node, MSQ.class);
                case "mtf" -> mapper.treeToValue(node, MTF.class);
                case "ordering" -> mapper.treeToValue(node, Ordering.class);
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public void conductQuiz() {
        int easyAnswered = 0, mediumAnswered = 0, hardAnswered = 0;
        if (questions == null || questions.isEmpty()) {
            System.out.println("No questions loaded.");
            return;
        }

        score = 0;
        System.out.println("=".repeat(100));
        for (Question q : questions) {
            q.display();
            if (q.attempt()) {
                score += difficultyOrder.getOrDefault(q.difficulty.toLowerCase(), 0);
                switch (q.difficulty.toLowerCase()) {
                    case "easy" -> easyAnswered++;
                    case "medium" -> mediumAnswered++;
                    case "hard" -> hardAnswered++;
                }
                System.out.println("+" + difficultyOrder.getOrDefault(q.difficulty.toLowerCase(), 0)
                        + ". Current Score: " + score);
            }
            System.out.println("=".repeat(100));
        }
        int maxScore = questions.stream().mapToInt(q -> difficultyOrder.getOrDefault(q.difficulty.toLowerCase(), 0))
                .sum();
        System.out.println("Your final score is: " + score + "/" + maxScore);
        System.out.println("Total Answered: " + (easyAnswered + mediumAnswered + hardAnswered));
        System.out.println("Easy Answered: " + easyAnswered);
        System.out.println("Medium Answered: " + mediumAnswered);
        System.out.println("Hard Answered: " + hardAnswered);
    }

    public int getScore() {
        return score;
    }
}

public class MyQuiz {
    public static void main(String[] args) throws Exception {
        Quiz quiz = new Quiz(10);
        quiz.loadQuestions("src/resources/tech-quiz.json");
        quiz.conductQuiz();
    }
}
/*
 * public static <T> List<T> readRandomFromArray(
 * File file,
 * int totalElements, // e.g., 10000 (must be known)
 * int howMany, // e.g., 179
 * Class<T> clazz // e.g., MCQ.class
 * ) throws IOException {
 * 
 * ObjectMapper mapper = new ObjectMapper();
 * JsonFactory factory = mapper.getFactory();
 * 
 * Set<Integer> indexes = new HashSet<>();
 * Random rand = new Random();
 * while (indexes.size() < howMany) {
 * indexes.add(rand.nextInt(totalElements));
 * }
 * 
 * List<T> selected = new ArrayList<>(howMany);
 * 
 * try (JsonParser parser = factory.createParser(file)) {
 * 
 * parser.nextToken(); // START_ARRAY
 * 
 * int i = 0;
 * 
 * while (parser.nextToken() != JsonToken.END_ARRAY) {
 * 
 * if (indexes.contains(i)) {
 * T obj = mapper.readValue(parser, clazz);
 * selected.add(obj);
 * } else {
 * parser.skipChildren();
 * }
 * 
 * i++;
 * }
 * }
 * 
 * return selected;
 * }
 */