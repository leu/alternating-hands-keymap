package io.github.leu;

import java.util.ArrayList;
import java.util.List;

public class StrokeSolver {
  static ArrayList<HexKey> keyboard = new ArrayList<>();

  public static void main(String[] args) {
    initialiseKeyboard();
    for (HexKey key : keyboard) {
      System.out.print(key.letter);
      for (HexKey subkey : key.strokeKeys) {
        System.out.print(" " + subkey.letter);
      }
      System.out.println();
    }
  }

  static void addMutualKeys(int i, int j) {
    keyboard.get(i).strokeKeys.add(keyboard.get(j));
    keyboard.get(j).strokeKeys.add(keyboard.get(i));
  }

  static void initialiseKeyboard() {
    for (int i = 0; i < 13; i++) {
      keyboard.add(new HexKey((char) (i + 48), new ArrayList<>()));
    }
    for (int i = 0; i < 13; i++) {
      if (i != 4 && i != 9 && i != 12)
      addMutualKeys(i, i + 1);
    }
    addMutualKeys(0, 6);
    addMutualKeys(5, 10);
    addMutualKeys(1, 7);
    addMutualKeys(6, 11);
    for (int i : List.of(3, 4, 8, 9, 12)) {
      for (int j : List.of(2, 7, 11)) {
        addMutualKeys(i, j);
      }
    }
  }
}

class HexKey {
  char letter;
  ArrayList<HexKey> strokeKeys;

  HexKey(char letter, ArrayList<HexKey> strokeKeys) {
    this.letter = letter;
    this.strokeKeys = strokeKeys;
  }
}

/*     2 3 4
      1 7 8 9
     0 6 b c
      5 a
 */