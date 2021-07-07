package io.github.leu;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;

public class Solver {
    public static JSONArray pairFrequencies;
    public static int bestScore = 0;
    public static HashSet<Character> bestSetA = new HashSet<>();
    public static HashSet<Character> bestSetB = new HashSet<>();
    private static HashSet<HashSet<Character>> checkedKeymaps = new HashSet<>();

    public static void main(String[] args) {
        findOptimalKeymap();
    }

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
        HashSet<Character> startingSet = new HashSet<>(Collections.singletonList('h'));
        char[] alphabetArray = "abcdefgijklmnopqrstuvwxyz".toCharArray();
        for (char letter : alphabetArray) {
            alphabetSet.add(letter);
        }
        findNLetters(12, alphabetSet, startingSet);

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
            checkedKeymaps.add(availableLetters);
            checkedKeymaps.add(usedLetters);
        }
    }

    private static void evaluateScore(HashSet<Character> setA, HashSet<Character> setB) {
        if (checkedKeymaps.contains(setA) || checkedKeymaps.contains(setB)) {
            return;
        }
        int score = 0;
        for (int i = 0; i < pairFrequencies.length(); i++) {
            JSONArray pairFrequency = pairFrequencies.getJSONArray(i);
            String lettersPair = pairFrequency.getString(0);
            if (setA.contains(lettersPair.charAt(0)) != setA.contains(lettersPair.charAt(1))) {
                score += pairFrequency.getInt(1);
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
