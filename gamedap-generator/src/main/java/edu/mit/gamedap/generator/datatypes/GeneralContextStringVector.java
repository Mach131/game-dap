package edu.mit.gamedap.generator.datatypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A generalized version of LinePositionStringVector, behaving similarly but accepting any context type.
 */
public class GeneralContextStringVector<C extends VectorContext> implements Vector<C, Character> {

  private C context;
  private String value;
  private final Set<Character> characterSet;
  private List<Character> characterList;

  /**
   * Initializes a new vector with a given context and value. The vector size will be fixed according
   * to this initial value, and the character set will be initialized as the characters it uses.
   * 
   * @param context context object
   * @param value The initial string value for the vector
   */
  public GeneralContextStringVector(C context, String value) {
    this.context = context;
    this.value = value;
    this.characterSet = new HashSet<>();
    this.addValueCharactersToSet();
  }

  /**
   * Initializes a new vector with a given context, value and character set. The vector size will be
   * fixed according to this initial value, and the character set will be extended with the characters
   * the value contains.
   * 
   * @param context context object
   * @param value The initial string value for the vector
   * @param characterSet The set of characters to use for randomization
   */
  public GeneralContextStringVector(C context, String value, Set<Character> characterSet) {
    this.context = context;
    this.value = value;
    this.characterSet = new HashSet<>(characterSet);
    this.addValueCharactersToSet();
  }

  /**
   * Initializes a new vector with a given context object, size and character set. The contents of the vector
   * will be randomized based on the provided characters, and the context's randomize method can be called seperately.
   * 
   * @param context Context object
   * @param size The total size of the vector (excluding the context)
   * @param characterSet The set of characters to use for randomization
   * @param rerandomizeContext Whether or not to call the context's randomize method
   */
  public GeneralContextStringVector(C context, int size, Set<Character> characterSet, boolean rerandomizeContext) {
    this.context = context;
    if (rerandomizeContext) {
      this.context.randomize();
    }

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
  public C getContext() {
    return this.context;
  }

  @Override
  public void randomize() {
    this.context.randomize();
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
  public double distance(Vector<C, Character> other) {
    return this.distance(other, 1.0);
  }

  @Override
  public double distance(Vector<C, Character> other, double contextWeight) {
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
    return String.format("<LinePositionStringVector: %s, '%s'>", this.context, this.value);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof LinePositionStringVector)) {
      return false;
    }
    @SuppressWarnings("unchecked")
    GeneralContextStringVector<C> ov = (GeneralContextStringVector<C>) obj;
    return ov.context.equals(this.context) && ov.value.equals(this.value);
  }
}
