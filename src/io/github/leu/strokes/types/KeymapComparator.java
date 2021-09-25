package io.github.leu.strokes.types;

import java.util.Comparator;

public class KeymapComparator implements Comparator<KeymapScorePair> {
  public int compare(KeymapScorePair o1, KeymapScorePair o2) {
    if (o1.score < o2.score) {
      return -1;
    } else if (o1.score == o2.score) {
      return 0;
    } else {
      return 1;
    }
  }
}
