package edu.mit.gamedap.generator;

import java.util.List;
import java.util.Map;

/**
 * Contains parsing methods inspired by https://www.cs.hmc.edu/~asampson/ap/technique.html
 */
public class SampsonParser {
  private final int w;

  /**
   * Contains the results of running the SampsonParser.
   */
  public class ParseResults {
    private final String recordFormat;
    private final List<List<String>> recordFields;
    
    public ParseResults(String recordFormat, List<List<String>> recordFields) {
      this.recordFormat = recordFormat;
      this.recordFields = recordFields;
    }

    /**
     * Provides a string indicating the structure of a single record, in regex form.
     * 
     * @return The record format
     */
    public String getRecordFormat() {
      return recordFormat;
    }

    /**
     * Provides a list of objects representing records. Each record is a list which contains
     * the text interpreted as its "fields", i.e. the contiguous unpopular vectors found during
     * parsing.
     * 
     * @return A list of identified record fields
     */
    public List<List<String>> getRecordFields() {
      return recordFields;
    }
  }

  public SampsonParser(int w) {
    assert(w > 0);
    this.w = w;
  }

  /**
   * Create substrings of the input, based on the parameter w with which this parser was initialized.
   * Assumes that w is at least 1 and is less than the length of the text.
   * 
   * @param text The input text
   * @return A list of length w substrings, where the ith element begins at the ith character of text.
   */
  List<String> makeSubstringVectors(String text) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  /**
   * Finds the set of characters used in an input text.
   * 
   * @param text The input text
   * @return A mapping between characters and unique indices, such that every character in the input
   * text appears in the output and the largest index is no greater than the size of the map.
   */
  Map<Character, Integer> buildCharacterSet(String text) {
    throw new UnsupportedOperationException("not yet implemented");
  }

   /**
    * Create a one-hot encoding of a character.
    *
    * @param c The character to encode
    * @param characterSet {@link SampsonParser#buildCharacterSet(String) A mapping between characters and
    * indices} that includes c
    * @return A vector of the same size as characterSet, where the index corresponding to c is
    * equal to 1 and every other element is equal to 0.
    */
  Vector<Integer> oneHotEncoding(char c, Map<Character, Integer> characterSet) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  /**
   * Converts {@link SampsonParser#makeSubstringVectors(String) substring vectors} into concatenations of
   * their characters' {@link SampsonParser#oneHotEncoding(char) one-hot encodings}.
   * 
   * @param substringVectors A list of length w substrings
   * @param characterSet {@link SampsonParser#buildCharacterSet(String) A mapping between characters and
    * indices} that includes all characters in the substringVectors
   * @return A list of vectors, such that every vector is a concatenation of the corresponding substrings'
   * one-hot encodings (length = w * characterSet.size()).
   */
  List<Vector<Integer>> convertSubstringVectors(List<String> substringVectors, Map<Character, Integer> characterSet) {
    throw new UnsupportedOperationException("not yet implemented");
  }

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

  /**
   * Calculates the popularity of all vectors within a set of vector clusters. This is originally set
   * to the size of the cluster the vector belongs to; vectors far away from their cluster's center
   * and clusters with low correlation between their vectors and center are "dropped" (popularity set to 0),
   * and the remaining vectors may be weighted based by how close their popularity is to a global mode.
   * 
   * @param vectorClusters A list of vector clusters
   * @return A mapping between every vector included in vectorClusters and their calculated popularity
   */
  Map<Vector<Integer>, Integer> calculateVectorPopularity(List<VectorCluster<Integer>> vectorClusters) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  /**
   * Runs the full parsing algorithm on the input text, identifying popular vectors and interpreting those
   * as being part of delimiters.
   * 
   * @param text The input text
   * @return The results of parsing through Competitive Learning and Vector Quantization
   * @see ParseResults
   */
  public ParseResults parse(String text) {
    throw new UnsupportedOperationException("not yet implemented");
  }
}
