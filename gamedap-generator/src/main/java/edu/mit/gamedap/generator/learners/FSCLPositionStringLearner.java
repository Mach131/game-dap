package edu.mit.gamedap.generator.learners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.gamedap.generator.datatypes.LinePositionContext;
import edu.mit.gamedap.generator.datatypes.Vector;

/**
 * A string-based competitive learner, using the special competitive learning techniques of
 * frequency-sensitive learning and rival penalization.
 * https://github.com/sampsyo/ap/blob/master/code/cl/fscl.py 
 */
public class FSCLPositionStringLearner extends PositionStringCompetitiveLearner {
  private final Map<Vector<LinePositionContext, Character>, Integer> neuronWins;
  private final double rivalPenalty;
  private final double contextWeight;

  public FSCLPositionStringLearner(double learningRate, double contextWeight, long maxPosition, Set<Character> characterSet) {
    super(learningRate, contextWeight, maxPosition, characterSet);
    this.neuronWins = new HashMap<>();
    this.rivalPenalty = -learningRate;
    this.contextWeight = contextWeight;
  }

  public FSCLPositionStringLearner(double learningRate, double contextWeight, double rivalPenalty, int maxPosition, Set<Character> characterSet) {
    super(learningRate, contextWeight, maxPosition, characterSet);
    this.neuronWins = new HashMap<>();
    this.rivalPenalty = rivalPenalty;
    this.contextWeight = contextWeight;
  }

  @Override
  public void initialize(int neuronCount, List<Vector<LinePositionContext, Character>> stimuli) {
    super.initialize(neuronCount, stimuli);
    neuronWins.clear();
  }
  

  @Override
  double getNeuronActivation(Vector<LinePositionContext, Character> neuron, Vector<LinePositionContext, Character> stimulus) {
    int nw = neuronWins.getOrDefault(neuron, 0);
    return nw * neuron.distance(stimulus, this.contextWeight);
  }
  
  @Override
  void trainSingleStimulus(Vector<LinePositionContext, Character> stimulus, double learningRate) {
    List<Vector<LinePositionContext, Character>> winningNeurons = this.getWinningNeurons(stimulus, 2);
    Vector<LinePositionContext, Character> winner = winningNeurons.get(0);
    Vector<LinePositionContext, Character> rival = winningNeurons.get(1);

    neuronWins.merge(winner, 1, Integer::sum);
    this.trainSelectedNeuron(stimulus, winner, learningRate);
    this.trainSelectedNeuron(stimulus, rival, rivalPenalty);
  }
}
