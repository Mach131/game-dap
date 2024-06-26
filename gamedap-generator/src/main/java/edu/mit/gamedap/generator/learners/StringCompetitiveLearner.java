package edu.mit.gamedap.generator.learners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import edu.mit.gamedap.generator.datatypes.EmptyContext;
import edu.mit.gamedap.generator.datatypes.StringVector;
import edu.mit.gamedap.generator.datatypes.Vector;

/**
 * A competitive learner that uses strings.
 * 
 * @see CompetitiveLearner
 */
public class StringCompetitiveLearner extends CompetitiveLearner<EmptyContext, Character> {

  private final Set<Character> characterSet;
  private final double contextWeight;

  /**
   * Initializes the learner with a set of characters to use for neuron generation.
   * 
   * @param learningRate The extent to which neurons are changed in a single learning step
   * @param characterSet The set of characters to use for neuron generation
   */
  public StringCompetitiveLearner(double learningRate, double contextWeight, Set<Character> characterSet) {
    super(learningRate, contextWeight);
    this.characterSet = characterSet;
    this.contextWeight = contextWeight;
  }

  @Override
  Vector<EmptyContext, Character> generateNeuron(int size) {
    return new StringVector(size, this.characterSet);
  }

  @Override
  double getNeuronActivation(Vector<EmptyContext, Character> neuron, Vector<EmptyContext, Character> stimulus) {
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
  void trainSelectedNeuron(Vector<EmptyContext, Character> stimulus, Vector<EmptyContext, Character> neuron, double learningAmount) {
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
          while (neuron.get(i) == stimulus.get(i)) {
            neuron.set(i, neuron.randomElement());
          }
        }
      });
  }

  @Override
  void trainSingleStimulus(Vector<EmptyContext, Character> stimulus, double learningRate) {
    Vector<EmptyContext, Character> winningNeuron = this.getWinningNeuron(stimulus);
    this.trainSelectedNeuron(stimulus, winningNeuron, learningRate);
  }
}
