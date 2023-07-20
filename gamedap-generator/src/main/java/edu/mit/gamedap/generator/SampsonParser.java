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
