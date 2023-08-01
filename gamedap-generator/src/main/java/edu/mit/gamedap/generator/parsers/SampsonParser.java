package edu.mit.gamedap.generator.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.mit.gamedap.generator.Utils;
import edu.mit.gamedap.generator.datatypes.StringVector;
import edu.mit.gamedap.generator.datatypes.Vector;
import edu.mit.gamedap.generator.datatypes.VectorCluster;
import edu.mit.gamedap.generator.learners.CompetitiveLearner;
import edu.mit.gamedap.generator.learners.FSCLStringLearner;
import edu.mit.gamedap.generator.learners.StringCompetitiveLearner;

/**
 * Contains parsing methods inspired by https://www.cs.hmc.edu/~asampson/ap/technique.html
 */
public class SampsonParser {
  public static final int DEFAULT_NEURON_COUNT = 100;
  public static final double DEFAULT_LEARNING_RATE = 0.2;
  public static final int DEFAULT_TRAINING_EPOCHS = 100;
  public static final double DEFAULT_CLUSTER_STDDEV_THRESH = 0.001;

  private final int w;
  private final int neuronCount;
  private final double learningRate;
  private final int trainingEpochs;
  private final double clusterStddevThresh;

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

    @Override
    public String toString() {
      return String.format("<ParseResults: RecordFormat=%s, RecordFields=%s", getRecordFormat(), getRecordFields());
    }
  }

  public SampsonParser(int w) {
    assert(w > 0);
    this.w = w;
    this.neuronCount = DEFAULT_NEURON_COUNT;
    this.learningRate = DEFAULT_LEARNING_RATE;
    this.trainingEpochs = DEFAULT_TRAINING_EPOCHS;
    this.clusterStddevThresh = DEFAULT_CLUSTER_STDDEV_THRESH;
  }

  public SampsonParser(int w, int neuronCount, double learningRate, int trainingEpochs, double clusterStddevThresh) {
    assert(w > 0);
    this.w = w;
    this.neuronCount = neuronCount;
    this.learningRate = learningRate;
    this.trainingEpochs = trainingEpochs;
    this.clusterStddevThresh = clusterStddevThresh;
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
    CompetitiveLearner<Character> cl = new FSCLStringLearner(learningRate, characterSet);
    cl.initialize(neuronCount, substrings);
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
    Map<Vector<Character>, Double> result = new HashMap<>();
    for (VectorCluster<Character> cluster : vectorClusters) {
      if (cluster.getDistanceStdDev() < clusterStddevThresh) {
        result.putAll(calculateSingleClusterPopularities(cluster));
      } else {
        for (Vector<Character> vector : cluster.getVectors()) {
          result.put(vector, 0.0);
        }
      }
    }
    return result;
  }

  /**
   * Helper function that calculates popularities within a single cluster, assuming it hasn't been dropped.
   * 
   * @see SampsonParser#calculateVectorPopularities(List)
   */
  private Map<Vector<Character>, Double> calculateSingleClusterPopularities(VectorCluster<Character> vectorCluster) {
    double size = vectorCluster.getVectors().size();
    return vectorCluster.getVectors().stream()
      .collect(Collectors.toMap(Function.identity(), x -> size));
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
    Set<Character> characterSet = buildCharacterSet(text);
    List<Vector<Character>> substrings = makeSubstringVectors(text, characterSet);
    List<VectorCluster<Character>> clusters = assignVectorClusters(substrings, characterSet);

    // TODO: break into helper methods

    // Build popularity histogram
    for (VectorCluster<Character> cluster : clusters) {
      System.out.println(cluster.info());
    }
    System.out.println("---");
    Map<Vector<Character>, Double> popularities = calculateVectorPopularities(clusters);
    Map<Double, Integer> popularityHistogram = new HashMap<>();
    for (Vector<Character> vector : substrings) {
      double p = popularities.get(vector);
      System.out.println(String.format("%s -> %.3f", vector, p));
      if (p > 0) {
        popularityHistogram.put(p, popularityHistogram.getOrDefault(p, 0) + 1);
      }
    }
    System.out.println(popularityHistogram);
    double globalMode = popularityHistogram.keySet().stream()
      .max(Comparator.comparingInt(p -> popularityHistogram.get(p))).get();
    System.out.println(globalMode);
    System.out.println("-----");


    // Identify possible delimiters
    boolean inDelimiter = false;
    int delimiterStart = 0;
    List<String> foundDelimiters = new ArrayList<>();
    List<Integer> delimiterIndices = new ArrayList<>();
    for (int i = 0; i < substrings.size(); i++) {
      Vector<Character> substringVector = substrings.get(i);
      if (!inDelimiter) {
        if (popularities.get(substringVector) >= globalMode) {
          delimiterStart = i;
          inDelimiter = true;
        }
      } else {
        if (popularities.get(substringVector) < globalMode) {
          int delimiterEnd = Math.min(i + w - 1, text.length());
          String delimiter = text.substring(delimiterStart, delimiterEnd);
          System.out.println(String.format("[index %d] %s", delimiterStart, delimiter));

          foundDelimiters.add(delimiter);
          delimiterIndices.add(delimiterStart);
          inDelimiter = false;
        }
      }
    }
    if (inDelimiter) {
      int delimiterEnd = text.length();
      String delimiter = text.substring(delimiterStart, delimiterEnd);
      System.out.println(String.format("[index %d] %s", delimiterStart, delimiter));

      foundDelimiters.add(delimiter);
      delimiterIndices.add(delimiterStart);
    }
    // merge/"smooth out" delimiters, eliminating overlaps
    List<String> delimiters = new ArrayList<>();
    boolean merging = false;
    int di = 0;
    for (int i = 0; i < foundDelimiters.size() - 1; i++) {
      if (delimiterIndices.get(i) + foundDelimiters.get(i).length() >= delimiterIndices.get(i+1)) {
        int offset = foundDelimiters.get(i).length() - (delimiterIndices.get(i+1) - delimiterIndices.get(i));
        String toAppend = foundDelimiters.get(i+1).substring(offset);
        if (!merging) {
          delimiters.add(foundDelimiters.get(i));
        }
        delimiters.add(di, delimiters.get(di) + toAppend);
        merging = true;
      } else {
        delimiters.add(foundDelimiters.get(i));
        merging = false;
        di++;
      }
    }
    System.out.println("----");
    System.out.println(foundDelimiters);
    System.out.println("after merge:");
    System.out.println(delimiters);
    System.out.println("----");

    // Identify records by common patterns of delimiters
    Map<String, Integer> delimiterHistogram = new HashMap<>();
    for (String delimiter : delimiters) {
      delimiterHistogram.put(delimiter, delimiterHistogram.getOrDefault(delimiter, 0) + 1);
    }
    System.out.println(delimiterHistogram);
    int delimiterMode = delimiterHistogram.values().stream().reduce(0, (a, b) -> Math.max(a, b));
    System.out.println(delimiterMode);

    int startingIndex = 0;
    // Tricky: choosing an appropriate "start of record", might be optimal to compare several possibilities
    // Current approach: if mode is within a stddev of mean, use 1 stddev below mean; otherwise use mode
    double delimiterMean = Utils.calculateIntegerMean(delimiterHistogram.values());
    double delimiterSD = Utils.calculateIntegerStdDev(delimiterHistogram.values());
    double threshold = delimiterMode;
    if (Math.abs(delimiterMode - delimiterMean) <= delimiterSD) {
      threshold = delimiterMean - delimiterSD;
    }
    for ( ; startingIndex < delimiters.size(); startingIndex++) {
      if (delimiterHistogram.get(delimiters.get(startingIndex)) >= threshold) {
        break;
      }
    }
    String startingDelimiter = delimiters.get(startingIndex);
    System.out.println(startingDelimiter);
    
    boolean buildingRecord = true;
    List<String> recordDelimiters = new ArrayList<>();
    List<String> comparisonRecord = new ArrayList<>();
    recordDelimiters.add(startingDelimiter);
    for (int i = startingIndex + 1; i < delimiters.size(); i++) {
      if (delimiters.get(i).equals(startingDelimiter) || i == (delimiters.size() - 1)) {
        if (!buildingRecord) {
          // dynamic programming-ish thing to find common elements
          List<Integer> removedIndices = new ArrayList<>();
          int comparisonOffset = 0;
          for (int d = 0; d < recordDelimiters.size(); d++) {
            List<String> comparisonSublist = comparisonRecord.subList(comparisonOffset, comparisonRecord.size());
            int delimiterIndex = comparisonSublist.indexOf(recordDelimiters.get(d));
            comparisonOffset += delimiterIndex + 1;
            if (delimiterIndex == -1) { // not found
              removedIndices.add(d);
            }
          }
          Collections.reverse(removedIndices);
          for (int r : removedIndices) {
            recordDelimiters.remove(r);
          }
        }

        // System.out.println("-----");
        // System.out.println(recordDelimiters);
        // System.out.println("---");
        // System.out.println(comparisonRecord);
        buildingRecord = false;
        comparisonRecord.clear();
      }

      if (buildingRecord) {
        recordDelimiters.add(delimiters.get(i));
      } else {
        comparisonRecord.add(delimiters.get(i));
      }
    }
    System.out.println("-----");
    System.out.println(recordDelimiters);
    // System.out.println("---");
    // System.out.println(comparisonRecord);

    List<List<String>> parsedFields = new ArrayList<>();
    String currentText = text;
    int lastDelimiterIndex = 0;
    int currentDelimiterIndex = currentText.indexOf(recordDelimiters.get(0));
    while (currentDelimiterIndex >= 0) {
      List<String> currentSublist = new ArrayList<>();
      lastDelimiterIndex = currentDelimiterIndex;
      for (int i = 0; i < recordDelimiters.size(); i ++) {
        int nextDelimiter = Math.floorMod(i + 1, recordDelimiters.size());
        lastDelimiterIndex = currentDelimiterIndex + recordDelimiters.get(i).length();
        currentDelimiterIndex = currentText.indexOf(recordDelimiters.get(nextDelimiter), lastDelimiterIndex);

        if (currentDelimiterIndex < 0) {
          break;
        }
        String fieldValue = text.substring(lastDelimiterIndex, currentDelimiterIndex);
        currentSublist.add(fieldValue);
      }
      parsedFields.add(currentSublist);
    }

    String recordFormat = recordDelimiters.stream()
      .reduce("", (a, b) -> a + ".*" + b);
    ParseResults results = new ParseResults(recordFormat, parsedFields);
    System.out.println("-------");
    return results;
  }
}
