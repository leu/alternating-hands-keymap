package io.github.leu.strokes.types;

import java.util.HashMap;

public class KeymapScorePair {
  public HashMap<Character, KeyPosition> keyboardAssignment;
  public long score;

  public KeymapScorePair(HashMap<Character, KeyPosition> keyboardAssignment, long score) {
    this.keyboardAssignment = keyboardAssignment;
    this.score = score;
  }
}
