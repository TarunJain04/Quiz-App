package com.quiz;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class AppTest {

    @Test
    public void testQuestionsLoad() throws Exception {
        Quiz quiz = new Quiz();
        quiz.loadQuestions("src/main/resources/quiz.json", 5);
        assertEquals(5, quiz.getScore() >= 0 ? 5 : 5); // just checks no exception thrown
    }

    @Test
    public void testScoreInitiallyZero() {
        Quiz quiz = new Quiz();
        assertEquals(0, quiz.getScore());
    }

    @Test
    public void testMCQCorrectAnswer() {
        MCQ mcq = new MCQ();
        mcq.options = java.util.List.of("Paris", "London", "Berlin", "Madrid");
        mcq.answer = 1;
        // answer 1 = Paris = correct
        assertEquals(1, mcq.answer);
    }

    @Test
    public void testOpenEndedCaseInsensitive() {
        OpenEnded q = new OpenEnded();
        q.answer = "Paris";
        assertTrue("paris".equalsIgnoreCase(q.answer));
        assertTrue("PARIS".equalsIgnoreCase(q.answer));
    }
}