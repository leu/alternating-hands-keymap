package io.github.leu;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

public class MicroSolver {
    public static JSONArray pairFrequencies;
    public static JSONArray frequencies;
    public static ArrayList<Character> bestList = new ArrayList<>();
    public static ArrayList<Character> alphabetList = new ArrayList<>();
    public static PriorityQueue<KeyMap> bestKeymaps = new PriorityQueue<>(new KeyMapComparator());

    public static void main(String[] args) {
        findOptimalKeymap();
    }

    private static void findOptimalKeymap() {
        initialiseFrequencies();
        char[] arrayA = "aeghijopquxyz".toCharArray(); //"aeghijopquxyz"  "bcdfklmnrstvw"  [a, e, g, h, i, j, o, p, q, u, x, y, z]
        for (char letter : arrayA) {
            alphabetList.add(letter);
        }
        for (int i = 0; i < 13; i++) {
            bestList.add('a');
        }
        recursiveLayoutCheck(new int[13], 0, new int[]{2, 3, 4, 4});
        double[] fingerFrequencies = new double[4];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 4; j++) {
                fingerFrequencies[j] = 0;
            }
            KeyMap keymap = bestKeymaps.poll();
            assert keymap != null;
            System.out.println(keymap.layout.toString());
            for (int j = 0; j < 4; j++) {
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
            for (int j = 0; j < 4; j++) {
                System.out.print(df.format(fingerFrequencies[j]) + " ");
            }
            System.out.println("\n" + Math.round(100.0 * (fingerFrequencies[3] - fingerFrequencies[2])) / 100.0 + "\n" + keymap.score + "\n");
        }
//        for (KeyMap keymap : bestKeymaps) {
//            System.out.println(keymap.layout.toString() + " " + keymap.score);
//        }
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

        path = FileSystems.getDefault().getPath("/home/leu/Documents/programming/java/scrawler-keymap-solver/lib/letter_count.json");
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
                letterACategory = setLetterPositionCategory(letterA, positions);
                letterBCategory = setLetterPositionCategory(letterB, positions);
                if (letterACategory != letterBCategory) {
                    score += pairFrequency.getLong(1);
                }
            }
        }

        ArrayList<HashSet<Character>> layout = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            layout.add(new HashSet<>());
        }
        for (int i = 0; i < 13; i++) {
            layout.get(positions[i]).add(alphabetList.get(i));
        }

        double[] fingerFrequencies = new double[4];
        for (int j = 0; j < 4; j++) {
            fingerFrequencies[j] = 0;
        }

        for (int j = 0; j < 4; j++) {
            for (Character letter : layout.get(j)) {
                for (int k = 0; k < frequencies.length(); k++) {
                    JSONArray letterFrequency = frequencies.getJSONArray(k);
                    if (letterFrequency.getString(0).equals(String.valueOf(letter))) {
                        //System.out.print(letterFrequency.getDouble(1));
                        fingerFrequencies[j] += letterFrequency.getDouble(1);
                        //System.out.print(" ");
                    }
                }
            }
            //System.out.print("   ");
        }
        //System.out.println();

        if (fingerFrequencies[0] < fingerFrequencies[1] && fingerFrequencies[1] < fingerFrequencies[2] && fingerFrequencies[1] < fingerFrequencies[3]
                && fingerFrequencies[3] < 25 && fingerFrequencies[3] > 15 && fingerFrequencies[2] > 15 && fingerFrequencies[2] < 25 && fingerFrequencies[0] < 5
                && (bestKeymaps.size() == 0 || bestKeymaps.stream().noneMatch(a -> layout.get(3).containsAll(a.layout.get(3))))) {
            bestKeymaps.add(new KeyMap(layout, score));
            if (bestKeymaps.size() > 20) {
                bestKeymaps.poll();
            }
        }
    }

    private static int setLetterPositionCategory(char letter, int[] positions) {
        if (positions[alphabetList.indexOf(letter)] < 3) {
            return 1;
        } else if (positions[alphabetList.indexOf(letter)] < 6 && positions[alphabetList.indexOf(letter)] >= 3) {
            return 2;
        } else if (positions[alphabetList.indexOf(letter)] < 9 && positions[alphabetList.indexOf(letter)] >= 6) {
            return 3;
        } else {
            return 4;
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