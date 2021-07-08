package io.github.leu;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;

public class MicroSolver {
    public static JSONArray pairFrequencies;
    public static long bestScore = 0;
    public static ArrayList<Character> bestListA = new ArrayList<>();
    public static ArrayList<Character> listA = new ArrayList<>();

    public static void main(String[] args) {
        findOptimalKeymap();
    }

    private static void findOptimalKeymap() {
        initialiseFrequencies();
        char[] arrayA = "aeghijkoquxyz".toCharArray(); //"aeghijkoquxyz" "bcdflmnprstvw"
        for (char letter : arrayA) {
            listA.add(letter);
        }
        for (int i = 0; i < 13; i++) {
            bestListA.add('a');
        }
        recursiveLayoutCheck(new int[13], 0, new int[]{3, 3, 3, 4});
    }

    private static void recursiveLayoutCheck(int[] positions, int n, int[] groupNumAvailable) {
        if (n < 12) {
            for (int i = 0; i < 4; i++) {
                if (groupNumAvailable[i] > 0) {
                    int[] newPositions = positions.clone();
                    newPositions[n] = i;
                    int[] newGroupNumAvailable = groupNumAvailable.clone();
                    newGroupNumAvailable[i]--;
                    recursiveLayoutCheck(newPositions, n + 1, newGroupNumAvailable);
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                if (groupNumAvailable[i] > 0) {
                    positions[n] = i;
//                    StringBuilder groups = new StringBuilder();
//                    for (int group : positions) {
//                        groups.append(group);
//                    }
//                    System.out.println(groups);
                    evaluateScore(positions);
                }
            }
        }
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

    private static void evaluateScore(int[] positions) {
        long score = 0L;
        for (int i = 0; i < pairFrequencies.length(); i++) {
            JSONArray pairFrequency = pairFrequencies.getJSONArray(i);
            String lettersPair = pairFrequency.getString(0);
            char letterA = lettersPair.charAt(0);
            char letterB = lettersPair.charAt(1);
            int letterACategory, letterBCategory;
            if (listA.contains(letterA) && listA.contains(letterB)) {
                letterACategory = setLetterPositionCategory(letterA, positions);
                letterBCategory = setLetterPositionCategory(letterB, positions);
                if (letterACategory != letterBCategory) {
                    score += pairFrequency.getLong(1);
                }
            }
        }
        if (score > bestScore) {
            bestScore = score;
            ArrayList<HashSet<Character>> bestLayout = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                bestLayout.add(new HashSet<>());
            }
            for (int i = 0; i < 13; i++) {
                bestLayout.get(positions[i]).add(listA.get(i));
            }
            System.out.println(bestLayout);
            System.out.println(bestScore);
        }
    }

    private static int setLetterPositionCategory(char letter, int[] positions) {
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
}