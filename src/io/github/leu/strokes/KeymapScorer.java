package io.github.leu.strokes;

import io.github.leu.strokes.types.BigramFrequencyPair;
import io.github.leu.strokes.types.KeyPosition;
import io.github.leu.strokes.types.KeymapScorePair;

import java.util.HashMap;

import static io.github.leu.strokes.StrokeSolver.*;

public class KeymapScorer {
  static void scoreKeymap(HashMap<Character, KeyPosition> keyboardAssignment) {
    long score = 0;
    for (BigramFrequencyPair bigram : bigrams) {
      if (!ALL_AVAILABLE_LETTERS.contains(bigram.firstLetter) || !ALL_AVAILABLE_LETTERS.contains(bigram.secondLetter)) {
        continue; //skip bigram if it uses a letter not found on this half of the layout
      }
      if (keyboardAssignment.get(bigram.firstLetter).x < keyboardAssignment.get(bigram.secondLetter).x &&
              keyboardAssignment.get(bigram.firstLetter).y - keyboardAssignment.get(bigram.secondLetter).y <= 2 &&
              keyboardAssignment.get(bigram.firstLetter).y - keyboardAssignment.get(bigram.secondLetter).y >= -2) {
        score += bigram.frequency;
      }
    }

    KeymapScorePair keymapScorePair = new KeymapScorePair(keyboardAssignment, score);
    printKeymapScore(keymapScorePair); //print current keymap and its score
    if (bestKeymaps.size() == 0 || keymapScorePair.score > bestKeymaps.peek().score) {
      bestKeymaps.add(keymapScorePair); //add keymap to bestKeymaps only if better than worst one
      if (bestKeymaps.size() > 20) { //limit to 20
        bestKeymaps.poll();
      }
    }
  }
}
