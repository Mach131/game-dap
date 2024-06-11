package edu.mit.gamedap.generator.learners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.gamedap.generator.datatypes.EmptyContext;
import edu.mit.gamedap.generator.datatypes.Vector;

/**
 * A string-basec competitive learner, using the special competitive learning techniques of
 * frequency-sensitive learning and rival penalization.
 * https://github.com/sampsyo/ap/blob/master/code/cl/fscl.py 
 */
public class FSCLStringLearner extends StringCompetitiveLearner {
  private final Map<Vector<EmptyContext, Character>, Integer> neuronWins;
  private final double rivalPenalty;
  private final double contextWeight;

  public FSCLStringLearner(double learningRate, double contextWeight, Set<Character> characterSet) {
    super(learningRate, contextWeight, characterSet);
    this.contextWeight = contextWeight;
    this.neuronWins = new HashMap<>();
    this.rivalPenalty = -learningRate;
  }

  public FSCLStringLearner(double learningRate, double contextWeight, double rivalPenalty, Set<Character> characterSet) {
    super(learningRate, contextWeight, characterSet);
    this.contextWeight = contextWeight;
    this.neuronWins = new HashMap<>();
    this.rivalPenalty = rivalPenalty;
  }

  @Override
  public void initialize(int neuronCount, List<Vector<EmptyContext, Character>> stimuli) {
    super.initialize(neuronCount, stimuli);
    neuronWins.clear();
  }
  

  @Override
  double getNeuronActivation(Vector<EmptyContext, Character> neuron, Vector<EmptyContext, Character> stimulus) {
    int nw = neuronWins.getOrDefault(neuron, 0);
    return nw * neuron.distance(stimulus, contextWeight);
  }
  
  @Override
  void trainSingleStimulus(Vector<EmptyContext, Character> stimulus, double learningRate) {
    List<Vector<EmptyContext, Character>> winningNeurons = this.getWinningNeurons(stimulus, 2);
    Vector<EmptyContext, Character> winner = winningNeurons.get(0);
    Vector<EmptyContext, Character> rival = winningNeurons.get(1);

    neuronWins.merge(winner, 1, Integer::sum);
    this.trainSelectedNeuron(stimulus, winner, learningRate);
    this.trainSelectedNeuron(stimulus, rival, rivalPenalty);
  }
}
