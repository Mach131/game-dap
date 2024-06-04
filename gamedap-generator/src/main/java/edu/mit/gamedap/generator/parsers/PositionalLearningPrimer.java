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
public class PositionalLearningPrimer implements ParseLearningPrimer<LinePositionStringElt> {
  public static final double LINE_POSITION_WEIGHTING = 40;

  private final int neuronCount;
  private final double learningRate;
  private final int trainingEpochs;

  public PositionalLearningPrimer() {
    this.neuronCount = SampsonParser.DEFAULT_NEURON_COUNT;
    this.learningRate = SampsonParser.DEFAULT_LEARNING_RATE;
    this.trainingEpochs = SampsonParser.DEFAULT_TRAINING_EPOCHS;
  }

  public PositionalLearningPrimer(int neuronCount, double learningRate, int trainingEpochs) {
    this.neuronCount = neuronCount;
    this.learningRate = learningRate;
    this.trainingEpochs = trainingEpochs;
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

  @Override
  public List<Vector<LinePositionStringElt>> makeSubstringVectors(String text, int w, Set<Character> characterSet) {
    assert(w <= text.length());

    List<Vector<LinePositionStringElt>> result = new ArrayList<>();
    int linePosition = 0;
    for (int i = 0; i <= text.length() - w; i++) {
      result.add(new LinePositionStringVector(this._linePositionConverter(linePosition), text.substring(i, i + w), characterSet));
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

  @Override
  public List<VectorCluster<LinePositionStringElt>> assignVectorClusters(List<Vector<LinePositionStringElt>> substrings, Set<Character> characterSet) {
    double maxPosition = this.getMaxPosition(substrings);
    CompetitiveLearner<LinePositionStringElt> cl = new FSCLPositionStringLearner(learningRate, maxPosition, characterSet);
    cl.initialize(neuronCount, substrings);
    cl.train(trainingEpochs);
    return cl.cluster();
  }
}
