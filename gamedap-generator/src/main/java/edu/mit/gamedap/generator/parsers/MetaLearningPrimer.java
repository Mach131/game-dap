package edu.mit.gamedap.generator.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.mit.gamedap.generator.datatypes.EmptyContext;
import edu.mit.gamedap.generator.datatypes.GeneralContextStringVector;
import edu.mit.gamedap.generator.datatypes.LinePositionContext;
import edu.mit.gamedap.generator.datatypes.MetaContext;
import edu.mit.gamedap.generator.datatypes.MetaVector;
import edu.mit.gamedap.generator.datatypes.Vector;
import edu.mit.gamedap.generator.datatypes.VectorCluster;
import edu.mit.gamedap.generator.datatypes.TextGroup;
import edu.mit.gamedap.generator.learners.CompetitiveLearner;
import edu.mit.gamedap.generator.learners.FSCLMetaLearner;


/**
 * Contains parsing methods inspired by https://www.cs.hmc.edu/~asampson/ap/technique.html
 */
public class MetaLearningPrimer implements ParseLearningPrimer<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> {
  public static final double LINE_POSITION_WEIGHTING = 40;
  public static final int DEFAULT_MAX_DEPTH = 1;
  public static final double DEFAULT_DEPTH_INCREASE_PROBABILITY = 0.025;
  public static final int DEFAULT_DEPTH_INCREASE_MAX_LENGTH = 10;
  public static final double DEFAULT_SUB_CONTEXT_WEIGHT = 1.5;
  public static final int DEFAULT_SUB_VECTOR_LENGTH_MISMATCH_DISTANCE = 1;

  private final int neuronCount;
  private final double learningRate;
  private final int trainingEpochs;
  private final double contextWeight;

  private final int maxDepth;
  private final double depthIncreaseProbability;
  private final int depthIncreaseMaxLength;
  private final double subContextWeight;
  private final int subVectorLengthMismatchDistance;

  public MetaLearningPrimer() {
    this.neuronCount = SampsonParser.DEFAULT_NEURON_COUNT;
    this.learningRate = SampsonParser.DEFAULT_LEARNING_RATE;
    this.trainingEpochs = SampsonParser.DEFAULT_TRAINING_EPOCHS;
    this.contextWeight = 1.0;

    this.maxDepth = DEFAULT_MAX_DEPTH;
    this.depthIncreaseProbability = DEFAULT_DEPTH_INCREASE_PROBABILITY;
    this.depthIncreaseMaxLength = DEFAULT_DEPTH_INCREASE_MAX_LENGTH;
    this.subContextWeight = DEFAULT_SUB_CONTEXT_WEIGHT;
    this.subVectorLengthMismatchDistance = DEFAULT_SUB_VECTOR_LENGTH_MISMATCH_DISTANCE;
  }

  public MetaLearningPrimer(int neuronCount, double learningRate, int trainingEpochs, double contextWeight,
      int maxDepth, double depthIncreaseProbability, int depthIncreaseMaxLength, double subContextWeight,
      int subVectorLengthMismatchDistance) {
    this.neuronCount = neuronCount;
    this.learningRate = learningRate;
    this.trainingEpochs = trainingEpochs;
    this.contextWeight = contextWeight;

    this.maxDepth = maxDepth;
    this.depthIncreaseProbability = depthIncreaseProbability;
    this.depthIncreaseMaxLength = depthIncreaseMaxLength;
    this.subContextWeight = subContextWeight;
    this.subVectorLengthMismatchDistance = subVectorLengthMismatchDistance;
    
  }

  /**
   * Gets the maximum position of a given set of positional vectors.
   * 
   * @param vecs the input vectors
   * @return The maximum position of the input vectors
   */
  long getMaxPosition(List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> vecs) {
    return vecs.stream()
        .map((vec) -> ((LinePositionContext) vec.getContext()).getPositionIndex())
        .reduce(0L, (a, b) -> Math.max(a, b));
  }

  @Override
  public double getContextWeight() {
    return this.contextWeight;
  }

  @Override
  public List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> makeSubstringVectors(
        String text, int w, Set<Character> characterSet) {
    assert(w <= text.length());

    List<TextGroup> asTextGroups = new ArrayList<>();
    for (int i = 0; i < text.length();  i ++) {
      asTextGroups.add(TextGroup.asGroup(text.substring(i, i+1)));
    }
    return makeSubstringVectors(asTextGroups, w, characterSet);
  }

  public List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> makeSubstringVectors(
    List<TextGroup> textGroups, int w, Set<Character> characterSet) {
      assert(w <= textGroups.size());
  
      List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> result = new ArrayList<>();
      List<Vector<MetaContext<EmptyContext>, Character>> convertedTextGroups = textGroups.stream()
        .map(textGroup ->
          (Vector<MetaContext<EmptyContext>, Character>) new GeneralContextStringVector<>(
              new MetaContext<>(new EmptyContext(), textGroup.getDepth()), textGroup.getText(), characterSet))
        .toList();

      int linePosition = 0;
      for (int i = 0; i <= convertedTextGroups.size() - w; i++) {
        Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> newVector =
          new MetaVector<>(
            new LinePositionContext(linePosition),
            convertedTextGroups.subList(i, i+w),
            this.subContextWeight, this.subVectorLengthMismatchDistance);

        result.add(newVector);
        String newText = textGroups.get(i).getText();
        linePosition += newText.length();
        if (newText.matches("[\r\n].*")) {
          linePosition = 0;
        }
      }
  
      return result;
  }

  @Override
  public List<VectorCluster<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> assignVectorClusters(
      List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> substrings,
      Set<Character> characterSet) {
    long maxPosition = this.getMaxPosition(substrings);
    CompetitiveLearner<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> cl = new FSCLMetaLearner(
      learningRate, contextWeight, maxPosition, characterSet, this.maxDepth, this.depthIncreaseProbability, this.depthIncreaseMaxLength,
      this.subContextWeight, this.subVectorLengthMismatchDistance);
    cl.initialize(neuronCount, substrings);
    cl.train(trainingEpochs);
    return cl.cluster();
  }
}
