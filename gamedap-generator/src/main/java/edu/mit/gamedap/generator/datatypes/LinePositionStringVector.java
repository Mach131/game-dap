package edu.mit.gamedap.generator.datatypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A vector implementation for short strings, accounting for how far into a line they appear in the original text.
 * Distance is defined by the hamming distance (the number of characters that differ between two strings) and heavily,
 * weighted by the positional element. Random elements are based on either the characters in the set string or a provided
 * character set.
 */
public class LinePositionStringVector implements Vector<LinePositionContext, Character> {

  private LinePositionContext positionContext;
  private String value;
  private final Set<Character> characterSet;
  private List<Character> characterList;

  /**
   * Initializes a new vector with a given position and value. The vector size will be fixed according
   * to this initial value, and the character set will be initialized as the characters it uses.
   * 
   * @param positionContext Context indicating the vector's line position
   * @param value The initial string value for the vector
   */
  public LinePositionStringVector(LinePositionContext positionContext, String value) {
    this.positionContext = positionContext;
    this.value = value;
    this.characterSet = new HashSet<>();
    this.addValueCharactersToSet();
  }

  /**
   * Initializes a new vector with a given position, value and character set. The vector size will be
   * fixed according to this initial value, and the character set will be extended with the characters
   * the value contains.
   * 
   * @param positionContext Context indicating the vector's line position
   * @param value The initial string value for the vector
   * @param characterSet The set of characters to use for randomization
   */
  public LinePositionStringVector(LinePositionContext positionContext, String value, Set<Character> characterSet) {
    this.positionContext = positionContext;
    this.value = value;
    this.characterSet = new HashSet<>(characterSet);
    this.addValueCharactersToSet();
  }

  /**
   * Initializes a new vector with a given max position, size and character set. The contents and context of the vector
   * will be randomized based on the provided characters.
   * 
   * @param maxPosition The maximum position to be used by the context for randomization
   * @param size The total size of the vector (excluding the context)
   * @param characterSet The set of characters to use for randomization
   */
  public LinePositionStringVector(long maxPosition, int size, Set<Character> characterSet) {
    this.positionContext = LinePositionContext.makeRandom(maxPosition);
    this.characterSet = new HashSet<>(characterSet);
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
  public LinePositionContext getContext() {
    return this.positionContext;
  }

  @Override
  public void randomize() {
    this.positionContext.randomize();
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
  public double distance(Vector<LinePositionContext, Character> other) {
    return this.distance(other, 1.0);
  }

  @Override
  public double distance(Vector<LinePositionContext, Character> other, double contextWeight) {
    assert(this.size() == other.size());

    double distance = 0;
    for (int i = 0; i < this.size(); i ++) {
      if (!this.get(i).equals(other.get(i))) {
        distance += 1;
      }
    }

    distance += this.getContext().contextDistance(other.getContext()) * contextWeight;

    return distance;
  }

  @Override
  public String toString() {
    return String.format("<LinePositionStringVector: %s, '%s'>", this.positionContext, this.value);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof LinePositionStringVector)) {
      return false;
    }
    LinePositionStringVector ov = (LinePositionStringVector) obj;
    return ov.positionContext.equals(this.positionContext) && ov.value.equals(this.value);
  }
}
