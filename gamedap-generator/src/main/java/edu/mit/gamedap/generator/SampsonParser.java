package edu.mit.gamedap.generator;

import java.util.ArrayList;
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
  List<String> makeSubstringVectors(String text) {
    List<String> output = new ArrayList<>();
    return output;
  }

  /**
   * Create a fixed-length one-hot encoding of a character, where exactly one element of the output
   * is equal to 1, every other element is equal to 0, and each character produces a unique output.
   * 
   * Could initially be based off of ASCII values, but could also consider building a dictionary of
   * all used characters by scanning through the input - would probably want to include the input
   * text in a constructor
   */
  List<Integer> oneHotEncoding(char c) {
    List<Integer> output = new ArrayList<>();
    return output;
  }

  /**
   * Converts {@link SampsonParser#makeSubstringVectors(String) substring vectors} into concatenations of
   * their characters' {@link SampsonParser#oneHotEncoding(char) one-hot encodings}.
   */
  List<List<Integer>> convertSubstringVectors(List<String> substringVectors) {
    List<List<Integer>> output = new ArrayList<>();
    return output;
  }

  // TODO: vector quantization for clustering; vectors should note their distance from the cluster definition point

  /**
   * Gives the euclidean distance between two vectors. Assumes that the inputs are equal in length.
   * 
   * @param a vector
   * @param b vector
   * @return Euclidean distance between a and b (sqrt of the sum of squared differences for each dimension)
   */
  double euclideanDistance(List<Integer> a, List<Integer> b) {
    return 0;
  }
}
