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

import edu.mit.gamedap.generator.Utils;
import edu.mit.gamedap.generator.datatypes.EmptyContext;
import edu.mit.gamedap.generator.datatypes.LinePositionContext;
import edu.mit.gamedap.generator.datatypes.MetaContext;
import edu.mit.gamedap.generator.datatypes.TextGroup;
import edu.mit.gamedap.generator.datatypes.Vector;
import edu.mit.gamedap.generator.datatypes.VectorCluster;
import edu.mit.gamedap.generator.datatypes.VectorContext;

/**
 * Contains parsing methods inspired by https://www.cs.hmc.edu/~asampson/ap/technique.html
 */
public class MetaSampsonParser extends SampsonParser<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> {
  private final int w;
  private final double clusterStddevThresh;
  private final double contextWeightSignificanceRatio;


  public MetaSampsonParser(int w, double clusterStddevThresh, double contextWeightSignificanceRatio) {
    super(w, clusterStddevThresh, contextWeightSignificanceRatio);
    assert(w > 0);
    this.w = w;
    this.clusterStddevThresh = clusterStddevThresh;
    this.contextWeightSignificanceRatio = contextWeightSignificanceRatio;
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
  public List<TextGroup> groupDelimiters(String text,
  List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> substringVectors,
  Map<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>, Double> popularities, double popularityThreshold) {
    return groupDelimitersHelper(text, substringVectors, popularities, true,
        popularityThreshold, new HashSet<>());
  }

  /**
   * Identifies possible delimiters by combining contiguous substring vectors fitting a given set of popularities.
   * @param text The input text
   * @param substringVectors A list of substring vectors from the text
   * @param popularities A mapping of substring vectors to popularities, including all provided substringVectors
   * @param targetPopularities The set of popularities to accept as delimiters
   * 
   * @return A list of the identified delimiter candidates, in the order they appear
   */
  public List<TextGroup> groupDelimiters(String text,
  List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> substringVectors,
  Map<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>, Double> popularities, Set<Double> targetPopularities) {
    return groupDelimitersHelper(text, substringVectors, popularities, false,
        -1, targetPopularities);
  }

  private List<TextGroup> groupDelimitersHelper(String text,
      List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> substringVectors,
      Map<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>, Double> popularities, boolean useThreshold,
      double popularityThreshold, Set<Double> targetPopularities) {
    int currentDelimiterStart = -1;
    int currentDelimiterEnd = -1;
    boolean buildingDelimiter = false;

    List<TextGroup> asTextGroups = new ArrayList<>();
    for (int i = 0; i < substringVectors.size(); i++) {
      Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> vector = substringVectors.get(i);
      boolean overThreshold = useThreshold ?
          popularities.get(vector) >= popularityThreshold :
          targetPopularities.contains(popularities.get(vector));
      if (buildingDelimiter) {
        if (overThreshold) {
          currentDelimiterEnd = i + w;
        } else if (i >= currentDelimiterEnd) {
          asTextGroups.add(TextGroup.asGroup(text.substring(currentDelimiterStart, currentDelimiterEnd)));
          buildingDelimiter = false;
          // current character handled in conditional below
        }
      }

      if (!buildingDelimiter) {
        if (overThreshold) {
          currentDelimiterStart = i;
          currentDelimiterEnd = i + w;
          buildingDelimiter = true;
        } else {
          asTextGroups.add(TextGroup.asGroup(text.substring(i, i+1)));
        }
      }
    }

    // handle final delimiter
    if (buildingDelimiter) {
      asTextGroups.add(TextGroup.asGroup(text.substring(currentDelimiterStart, substringVectors.size())));
    }

    return asTextGroups;
  }

  private ParseResults delimitersToResults(String text, List<String> delimiters) {
    // Identify records by common patterns of delimiters

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
      if (delimiters.get(i).equals(startingDelimiter)) { //omitted condition:  || i == (delimiters.size() - 1)
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

        System.out.println("---dtr---");
        System.out.println(recordDelimiters);
        System.out.println("---");
        System.out.println(comparisonRecord);
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
    return results;
  }
  
  @Override
  public ParseResults parse(String text, ParseLearningPrimer<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> gPrimer) {
    assert(gPrimer instanceof MetaLearningPrimer);
    MetaLearningPrimer primer = (MetaLearningPrimer) gPrimer;
    Set<Character> characterSet = buildCharacterSet(text);
    List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> substrings = primer.makeSubstringVectors(text, this.w, characterSet);
    List<VectorCluster<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> clusters = primer.assignVectorClusters(substrings, characterSet);

    // Build popularity histogram
    // for (VectorCluster<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> cluster : clusters) {
    //   System.out.println(cluster.info());
    // }
    System.out.println("---");
    Map<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>, Double> popularities = calculateVectorPopularities(clusters);
    
    Map<Double, Integer> popularityHistogram = Utils.makeHistogram(popularities.values().stream().filter(x -> x>0).toList());
    Map<Double, List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>>> popularityMap = new HashMap<>();
    for (double popularity : popularityHistogram.keySet()) {
      popularityMap.put(popularity, popularities.keySet().stream().filter(x -> popularities.get(x) == popularity).toList());
    }

    System.out.println(popularityHistogram);
    System.out.println("-----");
    //System.out.println(popularityMap);
    Map<Integer, Double> sizeDistMap = makeClusterSizeDistanceMap(clusters, primer.getContextWeight());
    System.out.println(sizeDistMap);

    
    System.out.println("-----");
    // double popularityThreshold = Utils.calculateMode(popularities.values().stream().filter(x -> x>0).toList());

    // TODO: figure out how to properly set this with meta vectors
    double targetAverageDist = this.w - 1 + (primer.getContextWeight() * this.contextWeightSignificanceRatio);
    int targetPopularity = sizeDistMap.keySet().stream().max(Integer::compare).get();

    Set<Double> popSet = new HashSet<>();
    for (int popularity : sizeDistMap.keySet()) {
      if (sizeDistMap.get(popularity) <= targetAverageDist) {
        popSet.add(popularity * 1.0);
        if (popularity < targetPopularity) {
          targetPopularity = popularity;
        }
      }
    }
    
    double popularityThreshold = targetPopularity * 1.0;

    // Identify possible delimiters
    List<String> delimiters = findDelimiters(text, substrings, popularities, popSet);
    System.out.println("----");
    System.out.println(delimiters);
    System.out.println("----");


    // experiment: recursion step
    System.out.println("----begin recursion----");
    List<TextGroup> groupedDelimiters = groupDelimiters(text, substrings, popularities, popSet);
    List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> rSubstrings =
      primer.makeSubstringVectors(groupedDelimiters, this.w, characterSet);
    List<VectorCluster<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> rClusters =
      primer.assignVectorClusters(rSubstrings, characterSet);

    // Build popularity histogram
    // for (VectorCluster<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> cluster : rClusters) {
    //   System.out.println(cluster.info());
    // }
    System.out.println("---");
    Map<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>, Double> rPopularities = calculateVectorPopularities(rClusters);
    
    Map<Double, Integer> rPopularityHistogram = Utils.makeHistogram(rPopularities.values().stream().filter(x -> x>0).toList());
    Map<Double, List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>>> rPopularityMap = new HashMap<>();
    for (double popularity : rPopularityHistogram.keySet()) {
      rPopularityMap.put(popularity, rPopularities.keySet().stream().filter(x -> rPopularities.get(x) == popularity).toList());
    }

    System.out.println(rPopularityHistogram);
    System.out.println("-----");
    //System.out.println(popularityMap);
    Map<Integer, Double> rSizeDistMap = makeClusterSizeDistanceMap(rClusters, primer.getContextWeight());
    System.out.println(rSizeDistMap);

    
    System.out.println("-----");
    // double popularityThreshold = Utils.calculateMode(popularities.values().stream().filter(x -> x>0).toList());

    // TODO: for recursion, use the above method; then if anything qualifies for threshold (higher threshold? maybe not) use it
    //   Need some way to indicate higher-level items for grammar (get some different thing instead of TOKEN#, which can be picked up by other thing)
    double rTargetAverageDist = this.w - 1 + (primer.getContextWeight() * this.contextWeightSignificanceRatio);
    int rTargetPopularity = rSizeDistMap.keySet().stream().max(Integer::compare).get();
    
    Set<Double> rPopSet = new HashSet<>();
    for (int popularity : rSizeDistMap.keySet()) {
      if (rSizeDistMap.get(popularity) <= rTargetAverageDist) {
        rPopSet.add(popularity * 1.0);
        if (popularity < rTargetPopularity) {
          rTargetPopularity = popularity;
        }
      }
    }
    
    double rPopularityThreshold = rTargetPopularity * 1.0;

    // Identify possible delimiters
    List<String> rDelimiters = findDelimiters(text, rSubstrings, rPopularities, rPopSet);
    System.out.println("----");
    System.out.println(rDelimiters);
    System.out.println("----");


    // end experiment

    
    ParseResults results = delimitersToResults(text, delimiters);
    if (rDelimiters.size() > 0 && rDelimiters.lastIndexOf(rDelimiters.get(0)) != 0) {
      ParseResults parentResults = delimitersToResults(text, rDelimiters);
      results.setParent(parentResults);
    }
    System.out.println("-------");
    return results;
  }
}
