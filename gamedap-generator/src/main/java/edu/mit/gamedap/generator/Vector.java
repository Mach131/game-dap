package edu.mit.gamedap.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a sequence of values, used as a vector for competitive learning.
 * A wrapping for ease of changing implementation. Currently mutable; may change later.
 */
public class Vector<T> {
  private final List<T> values;

  /**
   * Initializes a new empty vector.
   */
  public Vector() {
    this.values = new ArrayList<>();
  }

  /**
   * Initializes a new vector.
   * @param c Initial elements
   */
  public Vector(Collection<T> c) {
    this.values = new ArrayList<>(c);
  }

  /**
   * Returns the current size of the vector.
   * @return the number of elements the vector contains.
   */
  public int size() {
    return this.values.size();
  }

  /**
   * Gets an element in the vector.
   * @param i index
   * @return the value at index i
   */
  public T get(int i) {
    return this.values.get(i);
  }

  /**
   * Sets an element in the vector.
   * @param i index
   * @param v new value
   * @return the value previously at index i
   */
  public T set(int i, T v) {
    return this.values.set(i, v);
  }

  /**
   * Creates a new vector by concatenating this with another.
   * @param other the vector to concatenate
   * @return a new vector containing the elements in this followed by the elements in other
   */
  public Vector<T> concat(Vector<T> other) {
    List<T> newValues = Stream.concat(this.values.stream(), other.values.stream())
      .collect(Collectors.toList());
    return new Vector<>(newValues);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Vector)) {
      return false;
    }
    Vector<T> ov = (Vector<T>) obj;
    return ov.values.equals(this.values);
  }
}
