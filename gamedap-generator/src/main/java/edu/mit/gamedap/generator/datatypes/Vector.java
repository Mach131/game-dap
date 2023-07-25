package edu.mit.gamedap.generator.datatypes;

/**
 * Represents a sequence of values, used as a vector for competitive learning.
 * A wrapping for ease of changing implementation. Currently mutable; may change later.
 */
public interface Vector<T> {

  /**
   * Randomly re-initializes the values in the vector.
   */
  public void randomize();

  /**
   * Provides a random element of the vector type.
   * @return a random value of the type T
   */
  public T randomElement();

  /**
   * Returns the current size of the vector.
   * @return the number of elements the vector contains.
   */
  public int size();

  /**
   * Gets an element in the vector.
   * @param i index
   * @return the value at index i
   */
  public T get(int i);

  /**
   * Sets an element in the vector.
   * @param i index
   * @param v new value
   * @return the value previously at index i
   */
  public T set(int i, T v);

  /**
   * Calculates the distance between two vectors by soome metric.
   * @param other the "destination" vector
   * @return the distance between this vector and the other
   */
  public double distance(Vector<T> other);
}
