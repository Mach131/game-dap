package edu.mit.gamedap.generator.parsers;

import java.util.ArrayList;
import java.util.Arrays;
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
import edu.mit.gamedap.generator.datatypes.LinePositionStringElt;
import edu.mit.gamedap.generator.datatypes.LinePositionStringVector;
import edu.mit.gamedap.generator.datatypes.StringVector;
import edu.mit.gamedap.generator.datatypes.Vector;
import edu.mit.gamedap.generator.datatypes.VectorCluster;
import edu.mit.gamedap.generator.learners.CompetitiveLearner;
import edu.mit.gamedap.generator.learners.FSCLPositionStringLearner;
import edu.mit.gamedap.generator.learners.PositionStringCompetitiveLearner;

/// TODO: extract different parts so that they can use the same parsing method and stuff

/**
 * Contains parsing methods inspired by https://www.cs.hmc.edu/~asampson/ap/technique.html
 */
public class PositionalSampsonParser {
  public static final int DEFAULT_NEURON_COUNT = 100;
  public static final double DEFAULT_LEARNING_RATE = 0.2;
  public static final int DEFAULT_TRAINING_EPOCHS = 100;
  public static final double DEFAULT_CLUSTER_STDDEV_THRESH = 0.001;

  public static final double LINE_POSITION_WEIGHTING = 40;

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

  public PositionalSampsonParser(int w) {
    assert(w > 0);
    this.w = w;
    this.neuronCount = DEFAULT_NEURON_COUNT;
    this.learningRate = DEFAULT_LEARNING_RATE;
    this.trainingEpochs = DEFAULT_TRAINING_EPOCHS;
    this.clusterStddevThresh = DEFAULT_CLUSTER_STDDEV_THRESH;
  }

  public PositionalSampsonParser(int w, int neuronCount, double learningRate, int trainingEpochs, double clusterStddevThresh) {
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
   * Gets the maximum position of a given set of positional vectors.
   * 
   * @param vecs the input vectors
   * @return The maximum position of the input vectors
   */
  double getMaxPosition(List<Vector<LinePositionStringElt>> vecs) {
    return vecs.stream()
      .map((vec) -> vec.get(0).getNumberValue())
      .reduce(0.0, (a, b) -> Math.max(a, b));
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
  List<Vector<LinePositionStringElt>> makeSubstringVectors(String text, Set<Character> characterSet) {
    assert(this.w <= text.length());

    List<Vector<LinePositionStringElt>> result = new ArrayList<>();
    int linePosition = 0;
    for (int i = 0; i <= text.length() - this.w; i++) {
      result.add(new LinePositionStringVector(this._linePositionConverter(linePosition), text.substring(i, i + this.w), characterSet));
      linePosition += 1;
      if (text.substring(i, i+1).matches("[\r\n]")) {
        linePosition = 0;
      }
    }

    return result;
  }

  /**
   * A function on line position integers to emphasize substrings closer to the beginning of a line.
   * 
   * @param linePosition The position of a substring within a line.
   * @return A value such that lower linePositions are closer together, and higher linePositions are further apart.
   */
  double _linePositionConverter(int linePosition) {
    return Math.exp((linePosition / LINE_POSITION_WEIGHTING) - 1);
  }

  /**
   * @see SampsonParser#makeSubstringVectors(String, Set)
   * 
   * @param text The input text
   * @return A list of size w string vectors, where the ith element contains the substring beginning
   * at the ith character of text.
   */
  List<Vector<LinePositionStringElt>> makeSubstringVectors(String text) {
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
  List<VectorCluster<LinePositionStringElt>> assignVectorClusters(List<Vector<LinePositionStringElt>> substrings, Set<Character> characterSet) {
    double maxPosition = this.getMaxPosition(substrings);
    CompetitiveLearner<LinePositionStringElt> cl = new FSCLPositionStringLearner(learningRate, maxPosition, characterSet);
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
  Map<Vector<LinePositionStringElt>, Double> calculateVectorPopularities(List<VectorCluster<LinePositionStringElt>> vectorClusters) {
    Map<Vector<LinePositionStringElt>, Double> result = new HashMap<>();
    for (VectorCluster<LinePositionStringElt> cluster : vectorClusters) {
      if (cluster.getDistanceStdDev() < clusterStddevThresh) {
        result.putAll(calculateSingleClusterPopularities(cluster));
      } else {
        for (Vector<LinePositionStringElt> vector : cluster.getVectors()) {
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
  private Map<Vector<LinePositionStringElt>, Double> calculateSingleClusterPopularities(VectorCluster<LinePositionStringElt> vectorCluster) {
    double size = vectorCluster.getVectors().size();
    return vectorCluster.getVectors().stream()
      .collect(Collectors.toMap(Function.identity(), x -> size));
  }

  /**
   * Identifies possible delimiters by combining contiguous substring vectors exceeding a given popularity
   * threshold.
   * @param text The input text
   * @param substringVectors A list of substring vectors from the text
   * @param popularities A mapping of substring vectors to popularities, including all provided substringVectors
   * @param popularityThreshold The threshold used to identify delimiter substring vectors
   * 
   * @return A list of the identified delimiter candidates, in the order they appear
   */
  private List<String> findDelimiters(String text, List<Vector<LinePositionStringElt>> substringVectors,
      Map<Vector<LinePositionStringElt>, Double> popularities, double popularityThreshold) {
    List<Integer> delimiterStarts = new ArrayList<>();
    List<Integer> delimiterEnds = new ArrayList<>();
    int lastDelimiterEnd = -1;
    int delimitersFound = 0;

    // Identify regions of text with delimiters
    for (int i = 1; i < substringVectors.size(); i++) {
      Vector<LinePositionStringElt> vector = substringVectors.get(i);
      if (popularities.get(vector) >= popularityThreshold) {
        // Check if this can be merged with the previous delimiter
        if (i <= lastDelimiterEnd) {
          lastDelimiterEnd = i + w;
          delimiterEnds.set(delimitersFound - 1, lastDelimiterEnd);
        } else {
          delimiterStarts.add(i);
          lastDelimiterEnd = i + w;
          delimiterEnds.add(lastDelimiterEnd);
          delimitersFound ++;
        }
      }
    }

    // Map regions to text
    List<String> delimiterText = new ArrayList<>();
    for (int i = 0; i < delimitersFound; i++) {
      int start = delimiterStarts.get(i);
      int end = Math.min(delimiterEnds.get(i), text.length());
      String delimiter = text.substring(start, end);
      delimiterText.add(delimiter);
    }

    return delimiterText;
  }

  /**
   * Given a list of delimiter candidates, identifies one which is most probably the beginning of a
   * recurring "record" pattern.
   * 
   * A bit of an open problem.
   * 
   * @param delimiters A non-empty list of ordered delimiter candidates, as from
   * {@link SampsonParser#findDelimiters(String, List, Map, double) findDelimiters}.
   * @return The index of a selected starting delimiter in the delimiters list.
   */
  private int selectStartingDelimiterIndex(List<String> delimiters) {
    Map<String, Integer> delimiterHistogram = Utils.makeHistogram(delimiters);

    // TODO: account for substrings (basic idea may be to find an initial candidate and test?)

    // Concept is that delimiters that are part of the "record" should appear around the same number of times
    //   Should note that some fields are optional, might not appear in every record; should handle this
    //    when forming records (make this part of a larger function? would want to form histogram there)

    // For now, select the first delimiter whose appearance count is within a standard deviation
    //    of the mean
    double delimiterMean = Utils.calculateIntegerMean(delimiterHistogram.values());
    double delimiterSD = Utils.calculateIntegerStdDev(delimiterHistogram.values());

    int startingIndex = 0;
    for (int i = 0; i < delimiters.size(); i++) {
      if (Math.abs(delimiterHistogram.get(delimiters.get(i)) - delimiterMean) <= delimiterSD) {
        startingIndex = i;
        break;
      }
    }

    return startingIndex;
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
    List<Vector<LinePositionStringElt>> substrings = makeSubstringVectors(text, characterSet);
    List<VectorCluster<LinePositionStringElt>> clusters = assignVectorClusters(substrings, characterSet);

    // TODO: break into helper methods

    // Build popularity histogram
    for (VectorCluster<LinePositionStringElt> cluster : clusters) {
      System.out.println(cluster.info());
    }
    System.out.println("---");
    Map<Vector<LinePositionStringElt>, Double> popularities = calculateVectorPopularities(clusters);
    Double maxPopularity = 0.0;
    List<Vector<LinePositionStringElt>> maxVectors = new ArrayList<>();
    for (Vector<LinePositionStringElt> vec : popularities.keySet()) {
      double pop = popularities.get(vec);
      if (pop > maxPopularity) {
        maxVectors.clear();
        maxPopularity = pop;
      } if (pop == maxPopularity) {
        maxVectors.add(vec);
      }
    }
    System.out.println(maxVectors);
    
    System.out.println(Utils.makeHistogram(popularities.values().stream().filter(x -> x>0).toList()));
    double globalMode = Utils.calculateMode(popularities.values().stream().filter(x -> x>0).toList());
    System.out.println("-----");

    // Identify possible delimiters
    List<String> delimiters = findDelimiters(text, substrings, popularities, globalMode);
    System.out.println("----");
    System.out.println(delimiters);
    System.out.println("----");

    // Identify records by common patterns of delimiters
    // TODO: test and replace with selectStartingDelimiterIndex

    /**start of replaced piece */
    // Map<String, Integer> delimiterHistogram = Utils.makeHistogram(delimiters);
    // System.out.println(delimiterHistogram);
    // int delimiterMode = delimiterHistogram.values().stream().reduce(0, (a, b) -> Math.max(a, b));
    // System.out.println(delimiterMode);

    // int startingIndex = 0;
    // // Tricky: choosing an appropriate "start of record", might be optimal to compare several possibilities
    // // Current approach: if mode is within a stddev of mean, use 1 stddev below mean; otherwise use mode
    // double delimiterMean = Utils.calculateIntegerMean(delimiterHistogram.values());
    // double delimiterSD = Utils.calculateIntegerStdDev(delimiterHistogram.values());
    // double threshold = delimiterMode;
    // if (Math.abs(delimiterMode - delimiterMean) <= delimiterSD) {
    //   threshold = delimiterMean - delimiterSD;
    // }
    // for ( ; startingIndex < delimiters.size(); startingIndex++) {
    //   if (delimiterHistogram.get(delimiters.get(startingIndex)) >= threshold) {
    //     break;
    //   }
    // }
    int startingIndex = this.selectStartingDelimiterIndex(delimiters);
    String startingDelimiter = delimiters.get(startingIndex);
    System.out.println(startingDelimiter);
    /** end of replaced piece */


    //TODO: replace this, especially once the substring issue is handled
    // instead of requiring strict matching, may want to collect a list of how all of the sub-lists
    //   between the starter, comparing them to find patterns that are "close enough" for some definition
    // will require more examples to fully analyze, but might be able to do some form of statistical analysis
    //   of how often a given token is in the right position
    
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
