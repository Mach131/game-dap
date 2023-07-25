package edu.mit.gamedap.generator.datatypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A vector implementation for short strings. Distance is defined by the hamming distance
 * (the number of characters that differ between two strings), and random elements are based
 * on either the characters in the set string or a provided character set.
 */
public class StringVector implements Vector<Character> {

  private String value;
  private final Set<Character> characterSet;
  private List<Character> characterList;

  /**
   * Initializes a new string vector with a given value. The vector size will be fixed according
   * to this initial value, and the character set will be initialized as the characters it uses.
   * 
   * @param value The initial string value for the vector
   */
  public StringVector(String value) {
    this.value = value;
    this.characterSet = new HashSet<>();
    this.addValueCharactersToSet();
  }

  /**
   * Initializes a new string vector with a given value and character set. The vector size will be
   * fixed according to this initial value, and the character set will be extended with the characters
   * the value contains.
   * 
   * @param value The initial string value for the vector
   * @param characterSet The set of characters to use for randomization
   */
  public StringVector(String value, Set<Character> characterSet) {
    this.value = value;
    this.characterSet = characterSet;
    this.addValueCharactersToSet();
  }

  /**
   * Initializes a new string vector with a given size and character set. The contents of the vector
   * will be randomized based on the provided characters.
   * 
   * @param size The size of the vector
   * @param characterSet The set of characters to use for randomization
   */
  public StringVector(int size, Set<Character> characterSet) {
    this.characterSet = characterSet;
    this.characterList = new ArrayList<>(characterSet);
    this.value = this.makeRandomString(size);
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
    this.value = this.makeRandomString(this.size());
  }

  private String makeRandomString(int size) {
    char[] newCharacters = new char[size];
    for (int i = 0; i < size; i ++) {
      newCharacters[i] = this.randomElement();
    }
    return new String(newCharacters);
  }

  @Override
  public Character randomElement() {
    Random random = new Random();
    return this.characterList.get(random.nextInt(this.characterList.size()));
  }

  @Override
  public int size() {
    return this.value.length();
  }

  @Override
  public Character get(int i) {
    return this.value.charAt(i);
  }

  @Override
  public Character set(int i, Character v) {
    char[] valueChars = this.value.toCharArray();
    char oldChar = valueChars[i];
    valueChars[i] = v;
    this.value = new String(valueChars);
    return oldChar;
  }

  @Override
  public double distance(Vector<Character> other) {
    assert(this.size() == other.size());

    double distance = 0;
    for (int i = 0; i < this.size(); i ++) {
      if (this.get(i) != other.get(i)) {
        distance += 1;
      }
    }
    return distance;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof StringVector)) {
      return false;
    }
    StringVector ov = (StringVector) obj;
    return ov.value.equals(this.value);
  }
}
