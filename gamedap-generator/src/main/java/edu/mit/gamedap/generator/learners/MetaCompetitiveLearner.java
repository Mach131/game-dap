package edu.mit.gamedap.generator.learners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import edu.mit.gamedap.generator.datatypes.EmptyContext;
import edu.mit.gamedap.generator.datatypes.GeneralContextStringVector;
import edu.mit.gamedap.generator.datatypes.LinePositionContext;
import edu.mit.gamedap.generator.datatypes.MetaContext;
import edu.mit.gamedap.generator.datatypes.MetaVector;
import edu.mit.gamedap.generator.datatypes.Vector;

/**
 * A competitive learner that uses meta-vectors. These are groups of string/character vectors that have potentially
 * been combined by previous learning steps; it's assumed that uncombined vectors contain single characters.
 * 
 * @see CompetitiveLearner
 */
public class MetaCompetitiveLearner extends CompetitiveLearner<LinePositionContext, Vector<MetaContext<EmptyContext>,  Character>> {

  private final Set<Character> characterSet;
  private final double contextWeight;
  private final long maxPosition;
  private final int maxDepth;
  private final double depthIncreaseProbability;
  private final int depthIncreaseMaxLength;
  private final double subContextWeight;
  private final int subVectorLengthMismatchDistance;

  /**
   * Initializes the learner with a set of parameters to use for neuron generation.
   * 
   * @param learningRate The extent to which neurons are changed in a single learning step
   * @param maxPosition The maximum line position of any vector for neuron generation
   * @param characterSet The set of characters to use for neuron generation
   */
  public MetaCompetitiveLearner(double learningRate, double contextWeight, long maxPosition, Set<Character> characterSet,
      int maxDepth, double depthIncreaseProbability, int depthIncreaseMaxLength, double subContextWeight,
      int subVectorLengthMismatchDistance) {
    super(learningRate, contextWeight);
    this.contextWeight = contextWeight;
    this.maxPosition = maxPosition;
    this.characterSet = characterSet;
    this.maxDepth = maxDepth;
    this.depthIncreaseProbability = depthIncreaseProbability;
    this.depthIncreaseMaxLength = depthIncreaseMaxLength;
    this.subContextWeight = subContextWeight;
    this.subVectorLengthMismatchDistance = subVectorLengthMismatchDistance;
  }

  @Override
  Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> generateNeuron(int size) {
    Random random = new Random();

    LinePositionContext context = LinePositionContext.makeRandom(maxPosition);
    List<Vector<MetaContext<EmptyContext>, Character>> subVectors = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      int subVectorDepth = 0;
      int subVectorSize = 1;
      while (subVectorDepth < this.maxDepth && random.nextDouble() < this.depthIncreaseProbability) {
        subVectorDepth += 1;
        subVectorSize += random.nextInt(this.depthIncreaseMaxLength);
      }

      MetaContext<EmptyContext> newContext = new MetaContext<EmptyContext>(
        new EmptyContext(), subVectorDepth, this.maxDepth, this.depthIncreaseProbability);
      subVectors.add(
        new GeneralContextStringVector<MetaContext<EmptyContext>>(newContext, subVectorSize, this.characterSet, false));
    }

    return new MetaVector<LinePositionContext, MetaContext<EmptyContext>>(
        context, subVectors, this.subContextWeight, this.subVectorLengthMismatchDistance);
  }

  @Override
  double getNeuronActivation(Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> neuron,
                             Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> stimulus) {
    return neuron.distance(stimulus, this.contextWeight);
  }

  /**
   * Updates a single neuron to become more or less similar to the stimulus. Uses the
   * {@link Vector#randomElement() vector's random element function} to create random differences if
   * necessary, so this assumes that the random element function can return more than one value.
   * 
   * @param stimulus The stimulus to reference
   * @param neuron The neuron to modify; assumed to be the same size as the stimulus
   * @param learningAmount The proportion of indices to be changed; elements in the neuron will become equal
   * to those in the stimulus if this is positive, and vice-versa if this is negative.
   */
  void trainSelectedNeuron(Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> stimulus,
                           Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> neuron, double learningAmount) {
    // Find the differing indices if learningAmount is positive, or the matching ones if negative
    List<Integer> targetIndices = new ArrayList<>(IntStream.range(0, stimulus.size())
      .filter(i -> (stimulus.get(i) == neuron.get(i)) != (learningAmount >= 0))
      .boxed().toList());

    // Choose a random subset to modify
    int maxAdaptations = (int) Math.round(Math.ceil(Math.abs(stimulus.size() * learningAmount)));
    int actualAdaptations = Math.min(maxAdaptations, targetIndices.size());
    Collections.shuffle(targetIndices);
    List<Integer> adaptationIndices = targetIndices.subList(0, actualAdaptations);

    // Create similarities if positive or random differences if negative
    adaptationIndices.stream()
      .forEach(i -> {
        if (learningAmount >= 0) {
          neuron.set(i, stimulus.get(i));
        } else {
          while (neuron.get(i).equals(stimulus.get(i))) {
            neuron.set(i, neuron.randomElement());
          }
        }
      });

      // Adjustments for context
      if (learningAmount >= 0) {
        neuron.getContext().becomeSimilarTo(stimulus.getContext(), learningAmount);
      } else {
        neuron.getContext().becomeDifferentFrom(stimulus.getContext(), Math.abs(learningAmount));
      }
  }

  @Override
  void trainSingleStimulus(Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> stimulus, double learningRate) {
    Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> winningNeuron = this.getWinningNeuron(stimulus);
    this.trainSelectedNeuron(stimulus, winningNeuron, learningRate);
  }
}
