package edu.mit.gamedap.generator.datatypes;

/**
 * Represents a sequence of values, used as a vector for competitive learning.
 * Also may include context related to the vector's usage in the original text.
 * A wrapping for ease of changing implementation. Currently mutable; may change later.
 */
public interface Vector<C extends VectorContext, T> {

  /**
   * Returns the context object associated with this vector.
   * 
   * @return The context object
   */
  public C getContext();

  /**
   * Randomly re-initializes the values in the vector and context. Does not change the size.
   * 
   * @see Vector#randomElement()
   */
  public void randomize();

  /**
   * Provides a random element of the vector type.
   * 
   * @return a random value of the type T
   */
  public T randomElement();

  /**
   * Returns the current size of the vector (excluding its context).
   * 
   * @return the number of elements the vector contains.
   */
  public int size();

  /**
   * Gets an element in the vector.
   * 
   * @param i index
   * @return the value at index i
   */
  public T get(int i);

  /**
   * Sets an element in the vector.
   * 
   * @param i index
   * @param v new value
   * @return the value previously at index i
   */
  public T set(int i, T v);

  /**
   * Calculates the distance between two vectors and their contexts by some metric. Expects that
   * the two vectors and their contexts have the same size; the maximum distance is the sum
   * of these sizes.
   * 
   * @param other the "destination" vector
   * @return the distance between this vector and the other
   */
  public double distance(Vector<C, T> other);

  /**
   * Similar to the normal distance method, but increases the weight of the context's distance, such
   * that the maximum distance is the vector size + (context weight * context size).
   * 
   * @param other the "destination" vector
   * @param contextWeight the amount by which to multiply the context distance
   * @return the weighted distance between this vector and the other
   */
  public double distance(Vector<C, T> other, double contextWeight);
}
