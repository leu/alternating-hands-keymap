package io.github.leu.strokes;

import io.github.leu.strokes.types.BigramFrequencyPair;
import io.github.leu.strokes.types.KeyPosition;
import io.github.leu.strokes.types.KeymapScorePair;
import io.github.leu.strokes.types.KeymapComparator;

import java.util.*;

public class StrokeSolver {
  public static final int NUM_OF_BIGRAMS = 669;
  static final String ALL_AVAILABLE_LETTERS_STRING = "etaoinsrhldcu";
  //all letters used in this half of the layout
  public static final ArrayList<Character> ALL_AVAILABLE_LETTERS = new ArrayList<>();
  //iterable list of ALL_AVAILABLE_LETTERS_STRING
  static ArrayList<KeyPosition> keyLayout = new ArrayList<>();
  //list of physical key positions to be used in coordinates
  static PriorityQueue<KeymapScorePair> bestKeymaps = new PriorityQueue<>(new KeymapComparator());
  //list of highest scoring keymaps
  static BigramFrequencyPair[] bigrams = new BigramFrequencyPair[NUM_OF_BIGRAMS];
  //list of all bigrams (order matters) and their frequency

  public static void main(String[] args) {
    Initialiser.initialise();
    AssignKeys.recursiveKeyAssign(new HashMap<>(), 0, ALL_AVAILABLE_LETTERS);
    for (KeymapScorePair keymapScorePair : bestKeymaps) {
      printKeymapScore(keymapScorePair);
    }
  }

  static void printKeymapScore(KeymapScorePair keymap) {
    keymap.keyboardAssignment.forEach( (letter, hexkey ) ->
            System.out.print(letter + ":(" + hexkey.x + ", " + hexkey.y + ") "));
    System.out.println("\n" + keymap.score);
  }
}

/*
the physical layout somewhat
o's for home row

          x
       x     x
    x     o     x
       o     o
    o     x     x
       x     x

index finger move freely
*/