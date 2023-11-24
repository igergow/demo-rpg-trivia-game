import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class TriviaGame implements IGame {
    private List<Question> questions;
    private Hero hero;
    private Scanner scanner;
    private final GameServer gameServer;
    private static final String TRIVIA_URL = "https://opentdb.com/api.php?amount=10&type=multiple&category=18&difficulty=easy";

    public TriviaGame(Hero hero, GameServer gameServer) {
        this.hero = hero;
        this.gameServer = gameServer;
        scanner = new Scanner(System.in);
        loadQuestions();
    }

    private void loadQuestions() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TRIVIA_URL))
                .build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            parseTriviaResponse(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseTriviaResponse(String responseString) throws JSONException {
        JSONObject response = new JSONObject(responseString);
        JSONArray questionsArray = response.getJSONArray("results");
        questions = new ArrayList<>();

        for (int i = 0; i < questionsArray.length(); i++) {
            JSONObject questionObject = questionsArray.getJSONObject(i);
            String questionText = questionObject.getString("question");
            String correctAnswer = questionObject.getString("correct_answer");
            JSONArray incorrectAnswers = questionObject.getJSONArray("incorrect_answers");

            questions.add(new Question(questionText, correctAnswer, incorrectAnswers.getString(0), incorrectAnswers.getString(1), incorrectAnswers.getString(2)));
        }
    }

    public void start() {
        int questionNumber = 1;
        for (Question question : questions) {
            System.out.println("Въпрос " + questionNumber++ + ":");
            System.out.println("За да отговорите, въведете номера на отговора");
            System.out.println(question.getQuestion());
            System.out.println(question.getAnswers());
            System.out.println();
            System.out.println("\t9) За да подобрите героя си");
            System.out.println("\t0) За да спрете играта");
            int answer = scanner.nextInt();
            if (answer == 0) {
                System.out.println("Играта приключи.");
                break;
            }
            if (answer == 9) {
                System.out.println("Използвайте точките за подобрения.");
                System.out.println("Точки: " + hero.points);
                for(int i = 0; i < hero.points; i++)
                {
                    System.out.println("Изберете атрибут за подобрение: strength, dexterity, vitality, energy");
                    String attribute = scanner.next();
                    while (!attribute.equals("strength") && !attribute.equals("dexterity") && !attribute.equals("vitality") && !attribute.equals("energy") && !attribute.equals("exit")) {
                        System.out.println("Невалиден атрибут. Опитайте отново.");
                        attribute = scanner.next();
                    }
                    if (attribute.equals("exit")) {
                        break;
                    }
                    hero.improveAttribute(attribute);
                    hero.printStats();
                }
                continue;
            }
            if (question.isCorrect(answer)) {
                hero.points++;
                System.out.println("Правилно! Точки: " + hero.points);
            } else {
                System.out.println("Грешка!");
            }
        }

        hero.improveAttribute("strength");
    }

    @Override
    public Hero getHero() {
        return hero;
    }
}