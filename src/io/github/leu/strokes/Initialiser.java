package io.github.leu.strokes;

import io.github.leu.strokes.types.BigramFrequencyPair;
import io.github.leu.strokes.types.KeyPosition;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.leu.strokes.StrokeSolver.*;

public class Initialiser {
  static void initialise() {
    for (char c : ALL_AVAILABLE_LETTERS_STRING.toCharArray()) {
      ALL_AVAILABLE_LETTERS.add(c);
    }
    //makes usable letters iterable

    for (int i = 1; i < 4; i++) {
      for (int j = 0; j < 3; j++) {
        keyLayout.add(new KeyPosition(i, j * 2 + i % 2));
      }
    }
    for (int i = 0; i < 2; i++) {
      for (int j = 1; j < 3; j++) {
        keyLayout.add(new KeyPosition(i * 4, j * 2));
      }
    }
    //initialises all possible positions of keys
    //allHexKeys.forEach(hexKey -> System.out.println(hexKey.x + " " + hexKey.y));

    Path path = FileSystems.getDefault().getPath("/home/leu/Documents/programming/java/tif_keymap/lib/bigrams.json");
    String content = "[]";
    try {
      content = Files.readString(path, StandardCharsets.US_ASCII);
    } catch (IOException e) {
      e.printStackTrace();
    }
    JSONArray JSONBigrams = new JSONArray(content); //reads json file into JSONBigrams
    for (int i = 0; i < JSONBigrams.length(); i++) {
      bigrams[i] = new BigramFrequencyPair(JSONBigrams.getJSONArray(i).getString(0).charAt(0),
              JSONBigrams.getJSONArray(i).getString(0).charAt(1),
              JSONBigrams.getJSONArray(i).getLong(1));
    }
    //parses JSON string into array of BigramFrequencyPair objects
  }
}
