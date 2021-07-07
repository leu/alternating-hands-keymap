package io.github.leu;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

public class Solver {
    public static JSONArray pairFrequencies;
    public static int bestScore = 0;
    public static HashSet<Character> bestSetA = new HashSet<>();
    public static HashSet<Character> bestSetB = new HashSet<>();

    public static void findOptimalKeymap() {
        Path path = FileSystems.getDefault().getPath("C:\\Users\\Hybri\\IdeaProjects\\scrawler-keymap-solver\\lib\\pairs.json");
        String content = "[]";
        try {
            content = Files.readString(path, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pairFrequencies = new JSONArray(content);

        HashSet<Character> alphabetSet = new HashSet<>();
        HashSet<Character> emptySet = new HashSet<>();
        char[] alphabetArray = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char letter : alphabetArray) {
            alphabetSet.add(letter);
        }
        findNLetters(13, alphabetSet, emptySet);

        System.out.println(bestSetA + "\n");
        System.out.println(bestSetB + "\n");
        System.out.println(bestScore);
    }

    private static void findNLetters(int n, HashSet<Character> availableLetters, HashSet<Character> usedLetters) {
        if (n > 0) {
            for (Character letter : availableLetters) {
                HashSet<Character> newAvailableLetters = new HashSet<>();
                for (Character letterCopy : availableLetters) {
                    if (!letterCopy.equals(letter)) {
                        newAvailableLetters.add(letterCopy);
                    }
                }
                HashSet<Character> newUsedLetters = new HashSet<>(usedLetters);
                newUsedLetters.add(letter);
                findNLetters(n-1, newAvailableLetters, newUsedLetters);
            }
        } else {
            evaluateScore(availableLetters, usedLetters);
        }
    }

    private static void evaluateScore(HashSet<Character> setA, HashSet<Character> setB) {
        int score = 0;
        for (int i = 0; i < pairFrequencies.length(); i++) {
            JSONArray pairFrequency = pairFrequencies.getJSONArray(i);
            String lettersPair = pairFrequency.getString(0);
            char letterA = lettersPair.charAt(0);
            char letterB = lettersPair.charAt(1);
            int frequency = pairFrequency.getInt(1);
            if (setA.contains(letterA) != setA.contains(letterB)) {
                score += frequency;
            }
        }
        if (score > bestScore) {
            bestScore = score;
            bestSetA = setA;
            bestSetB = setB;
            System.out.println(bestSetA + "\n");
            System.out.println(bestSetB + "\n");
            System.out.println(bestScore);
        }
    }
}
