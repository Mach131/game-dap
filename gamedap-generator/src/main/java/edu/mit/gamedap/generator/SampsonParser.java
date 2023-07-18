package edu.mit.gamedap.generator;

import java.util.List;

/**
 * Contains parsing methods inspired by https://www.cs.hmc.edu/~asampson/ap/technique.html
 */
public class SampsonParser {
  private final int w;

  public SampsonParser(int w) {
    this.w = w;
  }

  /**
   * Create length w substrings of the input, where the ith element of the output begins at
   * the ith character in the input.
   */
  Vector<String> makeSubstringVectors(String text) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  /**
   * Create a fixed-length one-hot encoding of a character, where exactly one element of the output
   * is equal to 1, every other element is equal to 0, and each character produces a unique output.
   * 
   * Could initially be based off of ASCII values, but could also consider building a dictionary of
   * all used characters by scanning through the input - would probably want to include the input
   * text in a constructor
   */
  Vector<Integer> oneHotEncoding(char c) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  /**
   * Converts {@link SampsonParser#makeSubstringVectors(String) substring vectors} into concatenations of
   * their characters' {@link SampsonParser#oneHotEncoding(char) one-hot encodings}.
   */
  List<Vector<Integer>> convertSubstringVectors(Vector<String> substringVectors) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  // TODO: vector quantization for clustering; vectors should note their distance from the cluster definition point
  //   will probably want details in a separate class; reference https://github.com/sampsyo/ap/blob/master/code/cl/__init__.py
  /**
   * Assigns one-hot vectors to clusters based on the Competitive Learning + Vector Quantization algorithm.
   * 
   * @param inputVectors A list of vectors, expected to be concatenated
   * {@link SampsonParser#oneHotEncoding(char) one-hot encodings}
   * @return A list of vector clusters, where each input vector is assigned to exactly one cluster
   */
  List<VectorCluster<Integer>> assignVectorClusters(List<Vector<Integer>> inputVectors) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  // TODO: Refine clusters, use clusters to identify records

  /**
   * Gives the euclidean distance between two vectors. Assumes that the inputs are equal in length.
   * TODO: may move to VectorCluster class
   * 
   * @param a vector
   * @param b vector
   * @return Euclidean distance between a and b (sqrt of the sum of squared differences for each dimension)
   */
  double euclideanDistance(List<Integer> a, List<Integer> b) {
    throw new UnsupportedOperationException("not yet implemented");
  }
}
