package io.github.leu.strokes.types;

public class BigramFrequencyPair {
  public char firstLetter;
  public char secondLetter;
  public long frequency;

  public BigramFrequencyPair(char firstLetter, char secondLetter, long frequency) {
    this.firstLetter = firstLetter;
    this.secondLetter = secondLetter;
    this.frequency = frequency;
  }
}
