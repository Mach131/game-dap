package edu.mit.gamedap.generator.datatypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A vector implementation for combinations of other vectors, accounting for their position in the original text as well
 * as whether component vectors have been formed by recursive token identification. The component vectors may vary in size;
 * distance is defined by the sum of hamming distances between corresponding components, but recursively-formed vectors
 * are weighted more heavily.
 */
public class MetaVector<C extends VectorContext, D extends VectorContext> implements Vector<C, Vector<D, Character>> {
  private static final double DEFAULT_SUB_CONTEXT_WEIGHT = 1.0;
  private static final int DEFAULT_SUB_VECTOR_LENGTH_MISMATCH_DISTANCE = 1;

  private C context;
  private List<Vector<D, Character>> vectors;
  private final double subContextWeight;
  private final int subVectorLengthMismatchDistance;

  private Random random = new Random();

  /**
   * Initializes a new vector with a given position and value. The vector size will be fixed according
   * to this initial value, and the character set will be initialized as the characters it uses.
   * 
   * @param positionContext Context indicating the vector's line position
   * @param value The initial string value for the vector
   */
  public MetaVector(C context, List<Vector<D, Character>> vectors) {
    this.context = context;
    this.vectors = new ArrayList<>(vectors);

    this.subContextWeight = DEFAULT_SUB_CONTEXT_WEIGHT;
    this.subVectorLengthMismatchDistance = DEFAULT_SUB_VECTOR_LENGTH_MISMATCH_DISTANCE;
  }

  /**
   * Allows modifying parameters related to distance calculation.
   * 
   * @param positionContext Context indicating the vector's line position
   * @param value The initial string value for the vector
   * @param subContextWeight The weight given to subvector contexts during distance calculation
   * @param subVectorLengthMismatchDistance The assumed distance between two vectors (excluding their contexts)
   *    if their lengths do not match; should be greater than 0. Influences the impact the context has in these cases
   */
  public MetaVector(C context, List<Vector<D, Character>> vectors, double subContextWeight, int subVectorLengthMismatchDistance) {
    this.context = context;
    this.vectors = new ArrayList<>(vectors);
    this.subContextWeight = subContextWeight;
    this.subVectorLengthMismatchDistance = subVectorLengthMismatchDistance;
  }

  @Override
  public C getContext() {
    return this.context;
  }

  @Override
  public void randomize() {
    this.context.randomize();
    for(Vector<D, Character> vector : vectors) {
      vector.randomize();
    }
  }

  @Override
  public Vector<D, Character> randomElement() {
    return this.vectors.get(random.nextInt(this.vectors.size()));
  }

  @Override
  public int size() {
    return this.vectors.size();
  }

  @Override
  public Vector<D, Character> get(int i) {
    return this.vectors.get(i);
  }

  @Override
  public Vector<D, Character> set(int i, Vector<D, Character> v) {
    return this.vectors.set(i, v);
  }

  @Override
  public double distance(Vector<C, Vector<D, Character>> other) {
    return this.distance(other, 1.0);
  }

  @Override
  public double distance(Vector<C, Vector<D, Character>> other, double contextWeight) {
    assert(this.size() == other.size());

    double distance = 0;
    for (int i = 0; i < this.size(); i ++) {
      Vector<D, Character> ownVector = this.get(i);
      Vector<D, Character> otherVector = other.get(i);
      if (ownVector.size() != otherVector.size()) {
        double subContextDistance = ownVector.getContext().contextDistance(otherVector.getContext());
        double effectiveVectorDistance = this.subVectorLengthMismatchDistance + subContextDistance;
        distance += effectiveVectorDistance / (this.subVectorLengthMismatchDistance + ownVector.getContext().getContextSize());
      } else {
        int totalSize = ownVector.size() + ownVector.getContext().getContextSize();
        distance += ownVector.distance(otherVector, this.subContextWeight) / totalSize;
      }
    }

    distance += this.getContext().contextDistance(other.getContext()) * contextWeight;

    return distance;
  }

  @Override
  public String toString() {
    return String.format("<MetaVector: %s, '%s'>", this.context, this.vectors);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MetaVector)) {
      return false;
    }
    @SuppressWarnings("unchecked")
    MetaVector<C, D> ov = (MetaVector<C, D>) obj;
    if (this.size() != ov.size()) {
      return false;
    }
    for (int i = 0; i < this.size(); i ++) {
      if (!this.get(i).equals(ov.get(i))) {
        return false;
      }
    }
    return ov.context.equals(this.context);
  }
}
