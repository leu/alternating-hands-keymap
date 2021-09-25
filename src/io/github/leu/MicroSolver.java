package io.github.leu;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;

public class MicroSolver {
    public static JSONArray pairFrequencies;
    public static JSONArray frequencies;
    public static ArrayList<Character> alphabetList = new ArrayList<>();
    public static PriorityQueue<KeyMap> bestKeymaps = new PriorityQueue<>(new KeyMapComparator());
    public static int NUM_OF_LETTERS = 9;
    public static int NUM_OF_FINGERS = 3;
    public static int[] KEY_DISTRIBUTION = new int[]{3, 3, 3};

    public static void main(String[] args) {
        findOptimalKeymap();
    }

    private static void findOptimalKeymap() {
        initialiseFrequencies();
        char[] arrayA = "tnsrldcmf".toCharArray(); //"aeghijkopquyz"  "bcdflmnrstvwx"  [a, e, g, h, i, j, k, o, p, q, u, y, z]
        for (char letter : arrayA) {
            alphabetList.add(letter);
        }
        recursiveLayoutCheck(new int[NUM_OF_LETTERS], 0, KEY_DISTRIBUTION);
        double[] fingerFrequencies = new double[NUM_OF_FINGERS];
        int numKeymaps = Math.min(20, bestKeymaps.size());
        for (int i = 0; i < numKeymaps; i++) {
            for (int j = 0; j < NUM_OF_FINGERS; j++) {
                fingerFrequencies[j] = 0;
            }
            KeyMap keymap = bestKeymaps.poll();
            assert keymap != null;
            System.out.println(keymap.layout.toString());
            for (int j = 0; j < NUM_OF_FINGERS; j++) {
                for (Character letter : keymap.layout.get(j)) {
                    for (int k = 0; k < frequencies.length(); k++) {
                        JSONArray letterFrequency = frequencies.getJSONArray(k);
                        if (letterFrequency.getString(0).equals(String.valueOf(letter))) {
                            System.out.print(letterFrequency.getDouble(1));
                            fingerFrequencies[j] += letterFrequency.getDouble(1);
                            System.out.print(" ");
                        }
                    }
                }
                System.out.print("   ");
            }
            System.out.println();
            DecimalFormat df = new DecimalFormat("##.##");
            for (int j = 0; j < NUM_OF_FINGERS; j++) {
                System.out.print(df.format(fingerFrequencies[j]) + " ");
            }
            System.out.println("\n" + keymap.score);
        }
    }

    private static void recursiveLayoutCheck(int[] positions, int n, int[] groupNumAvailable) {
        if (n < (NUM_OF_LETTERS - 1)) {
            for (int i = 0; i < NUM_OF_FINGERS; i++) {
                if (groupNumAvailable[i] > 0) {
                    int[] newPositions = positions.clone();
                    newPositions[n] = i;
                    int[] newGroupNumAvailable = groupNumAvailable.clone();
                    newGroupNumAvailable[i]--;
                    recursiveLayoutCheck(newPositions, n + 1, newGroupNumAvailable);
                }
            }
        } else {
            for (int i = 0; i < NUM_OF_FINGERS; i++) {
                if (groupNumAvailable[i] > 0) {
                    positions[n] = i;
                    evaluateScore(positions);
                }
            }
        }
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

    private static void evaluateScore(int[] positions) {
        long score = 0L;
        for (int i = 0; i < pairFrequencies.length(); i++) {
            JSONArray pairFrequency = pairFrequencies.getJSONArray(i);
            String lettersPair = pairFrequency.getString(0);
            char letterA = lettersPair.charAt(0);
            char letterB = lettersPair.charAt(1);
            int letterACategory, letterBCategory;
            if (alphabetList.contains(letterA) && alphabetList.contains(letterB)) {
                letterACategory = positions[alphabetList.indexOf(letterA)];
                letterBCategory = positions[alphabetList.indexOf(letterB)];
                if (letterACategory != letterBCategory) {
                    score += pairFrequency.getLong(1);
                }
            }
        }

        ArrayList<HashSet<Character>> layout = new ArrayList<>();
        for (int i = 0; i < NUM_OF_FINGERS; i++) {
            layout.add(new HashSet<>());
        }
        for (int i = 0; i < NUM_OF_LETTERS; i++) {
            layout.get(positions[i]).add(alphabetList.get(i));
        }

        double[] fingerFrequencies = new double[NUM_OF_FINGERS];
        for (int j = 0; j < NUM_OF_FINGERS; j++) {
            fingerFrequencies[j] = 0;
        }

        for (int j = 0; j < NUM_OF_FINGERS; j++) {
            for (Character letter : layout.get(j)) {
                for (int k = 0; k < frequencies.length(); k++) {
                    JSONArray letterFrequency = frequencies.getJSONArray(k);
                    if (letterFrequency.getString(0).equals(String.valueOf(letter))) {
                        fingerFrequencies[j] += letterFrequency.getDouble(1);
                    }
                }
            }
        }

        if (layout.get(0).contains('n') && layout.get(0).contains('r') && layout.get(1).contains('t') &&
                layout.get(1).contains('d') && layout.get(2).contains('s') && layout.get(2).contains('l')) {
            bestKeymaps.add(new KeyMap(layout, score));
            if (bestKeymaps.size() > 20) {
                bestKeymaps.poll();
            }
        }
    }
}

class KeyMap {
    ArrayList<HashSet<Character>> layout;
    long score;

    KeyMap(ArrayList<HashSet<Character>> layout, long score) {
        this.layout = layout;
        this.score = score;
    }
}

class KeyMapComparator implements Comparator<KeyMap> {
    public int compare(KeyMap o1, KeyMap o2) {
        if (o1.score < o2.score) {
            return -1;
        } else if (o1.score == o2.score) {
            return 0;
        } else {
            return 1;
        }
    }
}