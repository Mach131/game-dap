package edu.mit.gamedap.generator.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.mit.gamedap.generator.datatypes.StringVector;
import edu.mit.gamedap.generator.datatypes.Vector;
import edu.mit.gamedap.generator.datatypes.VectorCluster;
import edu.mit.gamedap.generator.learners.CompetitiveLearner;
import edu.mit.gamedap.generator.learners.StringCompetitiveLearner;

/**
 * Contains parsing methods inspired by https://www.cs.hmc.edu/~asampson/ap/technique.html
 */
public class SampsonParser {
  private static final int DEFAULT_NEURON_COUNT = 100;
  public static final double DEFAULT_LEARNING_RATE = 0.2;
  private static final int DEFAULT_TRAINING_EPOCHS = 100;

  private final int w;
  private final int neuronCount;
  private final double learningRate;
  private final int trainingEpochs;

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
    this.neuronCount = DEFAULT_NEURON_COUNT;
    this.learningRate = DEFAULT_LEARNING_RATE;
    this.trainingEpochs = DEFAULT_TRAINING_EPOCHS;
  }

  public SampsonParser(int w, int neuronCount, int learningRate, int trainingEpochs) {
    assert(w > 0);
    this.w = w;
    this.neuronCount = neuronCount;
    this.learningRate = learningRate;
    this.trainingEpochs = trainingEpochs;
  }

  /**
   * Finds the set of characters used in an input text.
   * 
   * @param text The input text
   * @return A set containing every character that appears in the input
   */
  Set<Character> buildCharacterSet(String text) {
    return text.chars()
      .mapToObj(c -> (char) c)
      .collect(Collectors.toSet());
  }

  /**
   * Create substrings of the input, based on the parameter w with which this parser was initialized.
   * Assumes that w is at least 1 and is less than the length of the text.
   * 
   * @param text The input text
   * @param characterSet The set of all characters contained in the text
   * @return A list of size w string vectors, where the ith element contains the substring beginning
   * at the ith character of text.
   */
  List<Vector<Character>> makeSubstringVectors(String text, Set<Character> characterSet) {
    assert(this.w <= text.length());

    List<Vector<Character>> result = new ArrayList<>();
    for (int i = 0; i <= text.length() - this.w; i++) {
      result.add(new StringVector(
        text.substring(i, i + this.w), characterSet));
    }

    return result;
  }

  /**
   * @see SampsonParser#makeSubstringVectors(String, Set)
   * 
   * @param text The input text
   * @return A list of size w string vectors, where the ith element contains the substring beginning
   * at the ith character of text.
   */
  List<Vector<Character>> makeSubstringVectors(String text) {
    Set<Character> characterSet = this.buildCharacterSet(text);
    return this.makeSubstringVectors(text, characterSet);
  }

  /**
   * Assigns substrings to clusters based on the Competitive Learning + Vector Quantization algorithm.
   * 
   * @param inputVectors A list of string vectors, where each vector is expected to be of length w
   * @param characterSet The set of all characters contained in the substrings
   * @return A list of vector clusters, where each input string's vector is assigned to exactly one cluster
   */
  List<VectorCluster<Character>> assignVectorClusters(List<Vector<Character>> substrings, Set<Character> characterSet) {
    CompetitiveLearner<Character> cl = new StringCompetitiveLearner(learningRate, characterSet);
    cl.train(trainingEpochs);
    return cl.cluster();
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
  Map<Vector<Character>, Double> calculateVectorPopularities(List<VectorCluster<Character>> vectorClusters) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  /**
   * Helper function that calculates popularities within a single cluster, assuming it hasn't been dropped.
   * 
   * @see SampsonParser#calculateVectorPopularities(List)
   */
  private Map<Vector<Character>, Double> calculateSingleClusterPopularities(VectorCluster<Character> vectorCluster) {
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
