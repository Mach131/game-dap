package edu.mit.gamedap.generator.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.mit.gamedap.generator.datatypes.LinePositionContext;
import edu.mit.gamedap.generator.datatypes.LinePositionStringVector;
import edu.mit.gamedap.generator.datatypes.Vector;
import edu.mit.gamedap.generator.datatypes.VectorCluster;
import edu.mit.gamedap.generator.learners.CompetitiveLearner;
import edu.mit.gamedap.generator.learners.FSCLPositionStringLearner;

/// TODO: extract different parts so that they can use the same parsing method and stuff

/**
 * Contains parsing methods inspired by https://www.cs.hmc.edu/~asampson/ap/technique.html
 */
public class PositionalLearningPrimer implements ParseLearningPrimer<LinePositionContext, Character> {
  public static final double LINE_POSITION_WEIGHTING = 40;

  private final int neuronCount;
  private final double learningRate;
  private final int trainingEpochs;
  private final double contextWeight;

  public PositionalLearningPrimer() {
    this.neuronCount = SampsonParser.DEFAULT_NEURON_COUNT;
    this.learningRate = SampsonParser.DEFAULT_LEARNING_RATE;
    this.trainingEpochs = SampsonParser.DEFAULT_TRAINING_EPOCHS;
    this.contextWeight = 1.0;
  }

  public PositionalLearningPrimer(int neuronCount, double learningRate, int trainingEpochs, double contextWeight) {
    this.neuronCount = neuronCount;
    this.learningRate = learningRate;
    this.trainingEpochs = trainingEpochs;
    this.contextWeight = contextWeight;
  }

  /**
   * Gets the maximum position of a given set of positional vectors.
   * 
   * @param vecs the input vectors
   * @return The maximum position of the input vectors
   */
  long getMaxPosition(List<Vector<LinePositionContext, Character>> vecs) {
    return vecs.stream()
        .map((vec) -> ((LinePositionContext) vec.getContext()).getPositionIndex())
        .reduce(0L, (a, b) -> Math.max(a, b));
  }

  @Override
  public double getContextWeight() {
    return this.contextWeight;
  }

  @Override
  public List<Vector<LinePositionContext, Character>> makeSubstringVectors(String text, int w, Set<Character> characterSet) {
    assert(w <= text.length());

    List<Vector<LinePositionContext, Character>> result = new ArrayList<>();
    int linePosition = 0;
    for (int i = 0; i <= text.length() - w; i++) {
      result.add(new LinePositionStringVector(new LinePositionContext(linePosition), text.substring(i, i + w), characterSet));
      linePosition += 1;
      if (text.substring(i, i+1).matches("[\r\n]")) {
        linePosition = 0;
      }
    }

    return result;
  }

  @Override
  public List<VectorCluster<LinePositionContext, Character>> assignVectorClusters(List<Vector<LinePositionContext, Character>> substrings,
      Set<Character> characterSet) {
    long maxPosition = this.getMaxPosition(substrings);
    CompetitiveLearner<LinePositionContext, Character> cl = new FSCLPositionStringLearner(learningRate, contextWeight, maxPosition, characterSet);
    cl.initialize(neuronCount, substrings);
    cl.train(trainingEpochs);
    return cl.cluster();
  }
}
