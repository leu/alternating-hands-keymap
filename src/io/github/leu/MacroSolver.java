package io.github.leu;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class MacroSolver {
    public static JSONArray pairFrequencies;
    public static long bestScore = 0;
    public static ArrayList<Character> bestListA = new ArrayList<>();
    public static char[] alphabetArray = "bcdefghijklmnopqrstuvwxyz".toCharArray();

    public static void main(String[] args) {
        findOptimalKeymap();
    }

    public static void findOptimalKeymap() {
        Path path = FileSystems.getDefault().getPath("/home/leu/Documents/programming/java/scrawler-keymap-solver/lib/pairs.json");
        String content = "[]";
        try {
            content = Files.readString(path, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pairFrequencies = new JSONArray(content);

        int[] letterPositions = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        char[] charList = new char[13];
        charList[0] = 'a';
        while (letterPositions[0] != 13) {
            for (int i = 0; i < 12; i++) {
                charList[i+1] = alphabetArray[letterPositions[i]];
            }
            ArrayList<Character> charArray = new ArrayList<>();
            for (char letter : charList) {
                charArray.add(letter);
            }
            evaluateScore(charArray);
            stepPosition(letterPositions, 11);
            for (int letter : letterPositions) {
                System.out.print(letter + " ");
            }
            System.out.println("\n");
        }
        System.out.println(bestListA + "\n");
        ArrayList<Character> bestListB = new ArrayList<>();
        for (char letter : alphabetArray) {
            bestListB.add(letter);
        }
        for (Character letter : bestListA) {
            bestListB.remove(letter);
        }
        System.out.println(bestListB);
        System.out.println(bestScore);
    }

    private static void stepPosition(int[] letterPositions, int i) {
        if (letterPositions[i] == 24 || i < 11 && letterPositions[i] + 1 == letterPositions[i+1]) {
            stepPosition(letterPositions, i - 1);
            letterPositions[i] = letterPositions[i-1] + 1;
        } else {
            letterPositions[i]++;
        }
    }

    private static void evaluateScore(ArrayList<Character> listA) {
        long score = 0L;
        for (int i = 0; i < pairFrequencies.length(); i++) {
            JSONArray pairFrequency = pairFrequencies.getJSONArray(i);
            String lettersPair = pairFrequency.getString(0);
            if (listA.contains(lettersPair.charAt(0)) != listA.contains(lettersPair.charAt(1))) {
                score += pairFrequency.getLong(1);
            }
        }
        if (score > bestScore) {
            bestScore = score;
            bestListA = listA;
            System.out.println(bestListA + "\n");
            System.out.println(bestScore);
        }
    }
}
