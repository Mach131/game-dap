package edu.mit.gamedap.generator.datatypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A vector implementation for short strings. Distance is defined by the hamming distance
 * (the number of characters that differ between two strings), and random elements are based
 * on either the characters in the set string or a provided character set.
 */
public class StringVector implements Vector<Character> {

  private String value;
  private Set<Character> characterSet;

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

  private void addValueCharactersToSet() {
    this.characterSet.addAll(
      this.value.chars()
      .mapToObj(c -> (char) c)
      .collect(Collectors.toSet()));
  }

  @Override
  public void randomize() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'randomize'");
  }

  @Override
  public Character randomElement() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'randomElement'");
  }

  @Override
  public int size() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'size'");
  }

  @Override
  public Character get(int i) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'get'");
  }

  @Override
  public Character set(int i, Character v) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'set'");
  }

  @Override
  public double distance(Vector<Character> other) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'distance'");
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
