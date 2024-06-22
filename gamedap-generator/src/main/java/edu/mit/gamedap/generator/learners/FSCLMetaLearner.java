package edu.mit.gamedap.generator.learners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.gamedap.generator.datatypes.EmptyContext;
import edu.mit.gamedap.generator.datatypes.LinePositionContext;
import edu.mit.gamedap.generator.datatypes.MetaContext;
import edu.mit.gamedap.generator.datatypes.Vector;

/**
 * A competitive learner for meta vectors, using the special competitive learning techniques of
 * frequency-sensitive learning and rival penalization.
 * https://github.com/sampsyo/ap/blob/master/code/cl/fscl.py 
 */
public class FSCLMetaLearner extends MetaCompetitiveLearner {
  private final Map<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>, Integer> neuronWins;
  private final double rivalPenalty;
  private final double contextWeight;

  public FSCLMetaLearner(double learningRate, double contextWeight, long maxPosition, Set<Character> characterSet,
      int maxDepth, double depthIncreaseProbability, int depthIncreaseMaxLength, double subContextWeight,
      int subVectorLengthMismatchDistance) {

    super(learningRate, contextWeight, maxPosition, characterSet, maxDepth, depthIncreaseProbability,
        depthIncreaseMaxLength, subContextWeight, subVectorLengthMismatchDistance);
    this.neuronWins = new HashMap<>();
    this.rivalPenalty = -learningRate;
    this.contextWeight = contextWeight;
  }

  public FSCLMetaLearner(double learningRate, double contextWeight, double rivalPenalty, long maxPosition,
      Set<Character> characterSet, int maxDepth, double depthIncreaseProbability, int depthIncreaseMaxLength,
      double subContextWeight, int subVectorLengthMismatchDistance) {

    super(learningRate, contextWeight, maxPosition, characterSet, maxDepth, depthIncreaseProbability,
        depthIncreaseMaxLength, subContextWeight, subVectorLengthMismatchDistance);
    this.neuronWins = new HashMap<>();
    this.rivalPenalty = rivalPenalty;
    this.contextWeight = contextWeight;
  }

  @Override
  public void initialize(int neuronCount, List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> stimuli) {
    super.initialize(neuronCount, stimuli);
    neuronWins.clear();
  }
  

  @Override
  double getNeuronActivation(
      Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> neuron,
      Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> stimulus) {
    int nw = neuronWins.getOrDefault(neuron, 0);
    return nw * neuron.distance(stimulus, this.contextWeight);
  }
  
  @Override
  void trainSingleStimulus(Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> stimulus, double learningRate) {
    List<Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>>> winningNeurons = this.getWinningNeurons(stimulus, 2);
    Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> winner = winningNeurons.get(0);
    Vector<LinePositionContext, Vector<MetaContext<EmptyContext>, Character>> rival = winningNeurons.get(1);

    neuronWins.merge(winner, 1, Integer::sum);
    this.trainSelectedNeuron(stimulus, winner, learningRate);
    this.trainSelectedNeuron(stimulus, rival, rivalPenalty);
  }
}
