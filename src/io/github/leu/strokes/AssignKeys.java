package io.github.leu.strokes;

import io.github.leu.strokes.types.KeyPosition;

import java.util.ArrayList;
import java.util.HashMap;

import static io.github.leu.strokes.StrokeSolver.keyLayout;

public class AssignKeys {
  static void recursiveKeyAssign(HashMap<Character, KeyPosition> keyboardAssignment, int keyN,
                                 ArrayList<Character> availableLetters) {
    if (keyN < 13) {
      for (int i = 0; i < availableLetters.size(); i++) {
        HashMap<Character, KeyPosition> newKeyboardAssignment = new HashMap<>(keyboardAssignment);
        newKeyboardAssignment.put(availableLetters.get(i), keyLayout.get(keyN));
        ArrayList<Character> newAvailableLetters = new ArrayList<>(availableLetters);
        newAvailableLetters.remove(i);
        recursiveKeyAssign(newKeyboardAssignment, keyN + 1, newAvailableLetters);
      }
    } else {
      KeymapScorer.scoreKeymap(keyboardAssignment);
    }
  }
}
