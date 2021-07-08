package io.github.leu;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MicroSolver {
    public static JSONArray pairFrequencies;
    public static long bestScore = 0;
    public static ArrayList<Character> bestListA = new ArrayList<>();
    public static ArrayList<Character> listA = new ArrayList<>();
    public static int[] positions = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

    public static void main(String[] args) {
        findOptimalKeymap();
    }

    private static void findOptimalKeymap() {
        initialiseFrequencies();
        char[] arrayA = "aeghijkoquxyz".toCharArray();
        for (char letter : arrayA) {
            listA.add(letter);
        }
        for (int i = 0; i < 13; i++) {
            bestListA.add('a');
        }

        int[] endPositions = {12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        while (!Arrays.equals(positions, endPositions)) {
            //System.out.println(Arrays.toString(positions));
            evaluateScore();
            stepPosition(12, new PriorityQueue<>());
        }
        System.out.println(bestListA);
        System.out.println(bestScore);
    }

    private static void initialiseFrequencies() {
        Path path = FileSystems.getDefault().getPath("/home/leu/Documents/programming/java/scrawler-keymap-solver/lib/pairs.json");
        String content = "[]";
        try {
            content = Files.readString(path, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pairFrequencies = new JSONArray(content);
    }

    private static void evaluateScore() {
        long score = 0L;
        for (int i = 0; i < pairFrequencies.length(); i++) {
            JSONArray pairFrequency = pairFrequencies.getJSONArray(i);
            String lettersPair = pairFrequency.getString(0);
            char letterA = lettersPair.charAt(0);
            char letterB = lettersPair.charAt(1);
            int letterACategory = -1, letterBCategory = -1;
            if (listA.contains(letterA) && listA.contains(letterB)) {
                letterACategory = setLetterPositionCategory(letterA);
                letterBCategory = setLetterPositionCategory(letterB);
                if (letterACategory != letterBCategory) {
                    score += pairFrequency.getLong(1);
                }
            }
        }
        if (score > bestScore) {
            bestScore = score;
            for (int i = 0; i < 13; i++) {
                bestListA.set(positions[i], listA.get(i));
            }
            System.out.println(bestListA + "\n");
            System.out.println(bestScore);
        }
    }

    private static int setLetterPositionCategory(char letter) {
        if (positions[listA.indexOf(letter)] < 3) {
            return 1;
        } else if (positions[listA.indexOf(letter)] < 6 && positions[listA.indexOf(letter)] >= 3) {
            return 2;
        } else if (positions[listA.indexOf(letter)] < 9 && positions[listA.indexOf(letter)] >= 6) {
            return 3;
        } else {
            return 4;
        }
    }

    private static void stepPosition(int i, PriorityQueue<Integer> availableNumbers) {
        AtomicInteger firstGreaterNumber = new AtomicInteger(-1);
        getNextGreaterPosition(availableNumbers, firstGreaterNumber, i);
        if (firstGreaterNumber.get() == -1) {
            availableNumbers.add(positions[i]);
            stepPosition(i-1, availableNumbers);
            assert(!availableNumbers.isEmpty());
            positions[i] = availableNumbers.poll();
        } else {
            availableNumbers.add(positions[i]);
            availableNumbers.remove(firstGreaterNumber.get());
            positions[i] = firstGreaterNumber.get();
        }
    }

    private static void getNextGreaterPosition(PriorityQueue<Integer> availableNumbers, AtomicInteger firstGreaterNumber, int i) {
        availableNumbers.forEach( a -> {
            if (firstGreaterNumber.get() == -1 && a > positions[i]) {
                firstGreaterNumber.set(a);
            }
        } );
    }
}