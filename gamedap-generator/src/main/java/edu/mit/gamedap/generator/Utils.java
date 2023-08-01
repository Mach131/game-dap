package edu.mit.gamedap.generator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Miscellaneous common functions
 */
public class Utils {
  public static double calculateDoubleMean(Collection<Double> values) {
    return values.stream()
      .reduce(0.0, Double::sum) / values.size();
  }

  public static double calculateIntegerMean(Collection<Integer> values) {
    return values.stream()
      .reduce(0, Integer::sum) / values.size();
  }

  public static Map<Object, Integer> makeHistogram(Collection<Object> values) {
    Map<Object, Integer> result = new HashMap<>();
    for (Object x : values) {
      result.put(x, result.getOrDefault(x, 0) + 1);
    }
    return result;
  }

  public static Object calculateMode(Collection<Object> values) {
    Map<Object, Integer> histogram = makeHistogram(values);
    return histogram.keySet().stream()
      .max(Comparator.comparingInt(p -> histogram.get(p))).get();
  }

  public static double calculateDoubleStdDev(Collection<Double> values) {
    double mean = calculateDoubleMean(values);

    double result = 0;
    for (double x : values) {
      result += Math.pow(x - mean, 2);
    }
    return Math.sqrt(result / values.size());
  }

  public static double calculateIntegerStdDev(Collection<Integer> values) {
    double mean = calculateIntegerMean(values);

    double result = 0;
    for (double x : values) {
      result += Math.pow(x - mean, 2);
    }
    return Math.sqrt(result / values.size());
  }
}
