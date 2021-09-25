package io.github.leu.combo_factor;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.PriorityQueue;

public class LetterBigramScorer {
  public static JSONArray pairFrequencies;
  public static JSONArray frequencies;
  public static char[] alphabetArray = "abcdefghijklmnopqrstuvwxyz".toCharArray();
  public static PriorityQueue<LetterScore> letterScores = new PriorityQueue<>(new LetterScoreComparator());

  public static void main(String[] args) {
    initialiseFrequencies();
    printLetterComboScores();
  }

  private static void printLetterComboScores() {
    for (char letter : alphabetArray) {
      long score = 0L;
      for (int i = 0; i < pairFrequencies.length(); i++) {
        JSONArray pairFrequency = pairFrequencies.getJSONArray(i);
        if (pairFrequency.getString(0).charAt(0) == letter || pairFrequency.getString(0).charAt(1) == letter) {
          score += pairFrequency.getLong(1);
        }
      }
      letterScores.add(new LetterScore(letter, score));
    }
    for (int i = 0; i < 26; i++) {
      LetterScore letter = letterScores.poll();
      assert letter != null;
      System.out.println(letter.letter + " " + letter.score);
    }
  }

  private static void initialiseFrequencies() {
    Path path = FileSystems.getDefault().getPath("/home/leu/Documents/programming/java/tif-keymap-solver/lib/pairs.json");
    String content = "[]";
    try {
      content = Files.readString(path, StandardCharsets.US_ASCII);
    } catch (IOException e) {
      e.printStackTrace();
    }
    pairFrequencies = new JSONArray(content);

    path = FileSystems.getDefault().getPath("/home/leu/Documents/programming/java/tif-keymap-solver/lib/letter_count.json");
    content = "[]";
    try {
      content = Files.readString(path, StandardCharsets.US_ASCII);
    } catch (IOException e) {
      e.printStackTrace();
    }
    frequencies = new JSONArray(content);
  }
}

class LetterScore {
  char letter;
  double score;
  LetterScore(char letter, double score) {
    this.letter = letter;
    this.score = score;
  }
}

class LetterScoreComparator implements Comparator<LetterScore> {
  public int compare(LetterScore o1, LetterScore o2) {
    if (o1.score < o2.score) {
      return -1;
    } else if (o1.score == o2.score) {
      return 0;
    } else {
      return 1;
    }
  }
}