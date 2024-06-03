package edu.mit.gamedap.generator.datatypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sound.sampled.Line;

/**
 * A vector implementation for short strings, accounting for how far into a line they appear in the original text.
 * Distance is defined by the hamming distance (the number of characters that differ between two strings) and heavily,
 * weighted by the positional element. Random elements are based on either the characters in the set string or a provided
 * character set.
 */
public class LinePositionStringVector implements Vector<LinePositionStringElt> {

  private final static float POSITION_RANDOM_VARIATION = 3;

  private double position;
  private String value;
  private final Set<Character> characterSet;
  private List<Character> characterList;

  /**
   * Initializes a new vector with a given position and value. The vector size will be fixed according
   * to this initial value, and the character set will be initialized as the characters it uses.
   * 
   * @param value The initial string value for the vector
   */
  public LinePositionStringVector(double position, String value) {
    this.position = position;
    this.value = value;
    this.characterSet = new HashSet<>();
    this.addValueCharactersToSet();
  }

  /**
   * Initializes a new vector with a given position, value and character set. The vector size will be
   * fixed according to this initial value, and the character set will be extended with the characters
   * the value contains.
   * 
   * @param value The initial string value for the vector
   * @param characterSet The set of characters to use for randomization
   */
  public LinePositionStringVector(double position, String value, Set<Character> characterSet) {
    this.position = position;
    this.value = value;
    this.characterSet = new HashSet<>(characterSet);
    this.addValueCharactersToSet();
  }

  /**
   * Initializes a new vector with a given max position, size and character set. The contents of the vector
   * will be randomized based on the provided characters.
   * 
   * @param size The total size of the vector (including the position as one element)
   * @param characterSet The set of characters to use for randomization
   */
  public LinePositionStringVector(double maxPosition, int size, Set<Character> characterSet) {
    Random random = new Random();
    this.position = random.nextDouble(maxPosition);
    this.characterSet = new HashSet<>(characterSet);
    this.characterList = new ArrayList<>(characterSet);
    this.value = this.makeRandomString(size - 1);
  }

  private void addValueCharactersToSet() {
    this.characterSet.addAll(
      this.value.chars()
      .mapToObj(c -> (char) c)
      .collect(Collectors.toSet()));
    this.characterList = new ArrayList<>(characterSet);
  }

  @Override
  public void randomize() {
    this.position = this.randomElement(0).getNumberValue();
    this.value = this.makeRandomString(this.size() - 1);
  }

  private String makeRandomString(int size) {
    char[] newCharacters = new char[size];
    for (int i = 0; i < size; i ++) {
      newCharacters[i] = this.randomElement(i+1).getCharacterValue();
    }
    return new String(newCharacters);
  }

  @Override
  public LinePositionStringElt randomElement() {
    Random random = new Random();
    return new LinePositionStringElt(this.characterList.get(random.nextInt(this.characterList.size())));
  }

  @Override
  public LinePositionStringElt randomElement(int i) {
    Random random = new Random();
    if (i > 0) {
      return new LinePositionStringElt(this.characterList.get(random.nextInt(this.characterList.size())));
    } else {
      double newPosition = random.nextDouble(
        Math.max(this.position - LinePositionStringVector.POSITION_RANDOM_VARIATION, 0),
        this.position + LinePositionStringVector.POSITION_RANDOM_VARIATION+1);
      return new LinePositionStringElt(newPosition);
    }
  }

  @Override
  public int size() {
    return this.value.length() + 1;
  }

  @Override
  public LinePositionStringElt get(int i) {
    if (i == 0) {
      return new LinePositionStringElt(this.position);
    } else {
      return new LinePositionStringElt(this.value.charAt(i-1));
    }
  }

  @Override
  public LinePositionStringElt set(int i, LinePositionStringElt v) {
    if (i == 0) {
      double oldPosition = this.position;
      this.position = v.getNumberValue();
      return new LinePositionStringElt(oldPosition);
    } else {
      char[] valueChars = this.value.toCharArray();
      char oldChar = valueChars[i-1];
      valueChars[i-1] = v.getCharacterValue();
      this.value = new String(valueChars);
      return new LinePositionStringElt(oldChar);
    }
  }

  @Override
  public double distance(Vector<LinePositionStringElt> other) {
    assert(this.size() == other.size());

    double distance = 0;
    for (int i = 0; i < this.size(); i ++) {
      LinePositionStringElt a = this.get(i);
      LinePositionStringElt b = other.get(i);
      if (!a.equals(b)) {
        if (a.isCharacter && b.isCharacter) {
          distance += 1;
        } else if (!a.isCharacter && !b.isCharacter) {
          distance += Math.abs(b.getNumberValue() - a.getNumberValue());
        } else {
          distance += 9999999;
        }
      }
    }
    return distance;
  }

  @Override
  public String toString() {
    return String.format("<LinePositionStringVector: position %s, '%s'>", this.position, this.value);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof LinePositionStringVector)) {
      return false;
    }
    LinePositionStringVector ov = (LinePositionStringVector) obj;
    return ov.position == this.position && ov.value.equals(this.value);
  }
}
