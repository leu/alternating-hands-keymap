package io.github.leu;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MacroSolver {
    public static JSONArray pairFrequencies;
    public static JSONArray frequencies;
    public static PriorityQueue<KeySet> bestKeymaps = new PriorityQueue<>(new KeySetComparator());
    public static char[] alphabetArray = "bcdefghijklmnopqrstuvwxyz".toCharArray();

    public static void main(String[] args) {
        findOptimalKeymap();
    }

    public static void findOptimalKeymap() {
        initialiseFrequencies();

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
        }

        for (int i = 0; i < 30; i++) {
            KeySet keyset = bestKeymaps.poll();
            assert keyset != null;
            System.out.println(keyset.leftSideKeys.toString() + " " + keyset.score + " " + Math.round(100.0*keyset.getTotalFrequency())/100.0);
        }
        long score = 0L;
        for (int i = 0; i < pairFrequencies.length(); i++) {
            JSONArray pairFrequency = pairFrequencies.getJSONArray(i);
            score += pairFrequency.getLong(1);
        }
        System.out.println(score);
    }

    private static void initialiseFrequencies() {
        Path path = FileSystems.getDefault().getPath("/home/leu/Documents/programming/java/tif_keymap/lib/pairs.json");
        String content = "[]";
        try {
            content = Files.readString(path, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pairFrequencies = new JSONArray(content);

        path = FileSystems.getDefault().getPath("/home/leu/Documents/programming/java/tif_keymap/lib/letter_count.json");
        content = "[]";
        try {
            content = Files.readString(path, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
        }
        frequencies = new JSONArray(content);
    }

    private static void stepPosition(int[] letterPositions, int i) {
        if (letterPositions[i] == 24 || i < 11 && letterPositions[i] + 1 == letterPositions[i+1]) {
            stepPosition(letterPositions, i - 1);
            letterPositions[i] = letterPositions[i-1] + 1;
        } else {
            letterPositions[i]++;
        }
    }

    private static void evaluateScore(ArrayList<Character> keymapList) {
        long score = 0L;
        for (int i = 0; i < pairFrequencies.length(); i++) {
            JSONArray pairFrequency = pairFrequencies.getJSONArray(i);
            String lettersPair = pairFrequency.getString(0);
            if (keymapList.contains(lettersPair.charAt(0)) == keymapList.contains(lettersPair.charAt(1))) {
                score += pairFrequency.getLong(1);
            }
        }
        bestKeymaps.add(new KeySet(keymapList, score));
        if (bestKeymaps.size() > 30) {
            bestKeymaps.poll();
        }
    }
}

class KeySet {
    ArrayList<Character> leftSideKeys;
    long score;

    KeySet(ArrayList<Character> leftSideKeys, long score) {
        this.leftSideKeys = leftSideKeys;
        this.score = score;
    }

    double getTotalFrequency() {
        double totalFrequency = 0.0;
        for (int i = 0; i < MacroSolver.frequencies.length(); i++) {
            JSONArray letterFrequency = MacroSolver.frequencies.getJSONArray(i);
            for (Character letter : leftSideKeys) {
                if (letter.toString().equals(letterFrequency.getString(0))) {
                    totalFrequency += letterFrequency.getDouble(1);
                }
            }
        }
        return totalFrequency;
    }
}

class KeySetComparator implements Comparator<KeySet> {
    public int compare(KeySet o1, KeySet o2) {
        if (o1.score < o2.score) {
            return -1;
        } else if (o1.score == o2.score) {
            return 0;
        } else {
            return 1;
        }
    }
}
