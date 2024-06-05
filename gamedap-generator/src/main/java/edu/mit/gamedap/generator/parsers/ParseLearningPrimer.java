package edu.mit.gamedap.generator.parsers;

import java.util.List;
import java.util.Set;
import edu.mit.gamedap.generator.datatypes.Vector;
import edu.mit.gamedap.generator.datatypes.VectorCluster;
import edu.mit.gamedap.generator.datatypes.VectorContext;

/**
 * Contains parsing methods inspired by https://www.cs.hmc.edu/~asampson/ap/technique.html
 */
public interface ParseLearningPrimer<C extends VectorContext, T> {
  /**
   * Create substrings of the input, based on the parsing parameter w.
   * Assumes that w is at least 1 and is less than the length of the text.
   * 
   * @param text The input text
   * @param w The width of the string associated with every vector
   * @param characterSet The set of all characters contained in the text
   * @return A list of size w string vectors, where the ith element contains the substring beginning
   * at the ith character of text.
   */
  public List<Vector<C, T>> makeSubstringVectors(String text, int w, Set<Character> characterSet);

  /**
   * Assigns substrings to clusters based on the Competitive Learning + Vector Quantization algorithm.
   * 
   * @param inputVectors A list of string vectors, where each vector is expected to be of the same length length w
   * @param characterSet The set of all characters contained in the substrings
   * @return A list of vector clusters, where each input string's vector is assigned to exactly one cluster
   */
  public List<VectorCluster<C, T>> assignVectorClusters(List<Vector<C, T>> inputVectors, Set<Character> characterSet);
}
