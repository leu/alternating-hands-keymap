package io.github.leu;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Solver {
    public static JSONArray pairFrequencies;
    public static int bestScore = 0;
    public static ArrayList<Character> bestListA = new ArrayList<>();
    public static ArrayList<Character> bestListB = new ArrayList<>();

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

        char[] alphabetArray = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    }

    private static void evaluateScore(ArrayList<Character> listA, ArrayList<Character> listB) {
        int score = 0;
        for (int i = 0; i < pairFrequencies.length(); i++) {
            JSONArray pairFrequency = pairFrequencies.getJSONArray(i);
            String lettersPair = pairFrequency.getString(0);
            if (listA.contains(lettersPair.charAt(0)) != listA.contains(lettersPair.charAt(1))) {
                score += pairFrequency.getInt(1);
            }
        }
        if (score > bestScore) {
            bestScore = score;
            bestListA = listA;
            bestListB = listB;
            System.out.println(bestListA + "\n");
            System.out.println(bestListB + "\n");
            System.out.println(bestScore);
        }
    }
}
