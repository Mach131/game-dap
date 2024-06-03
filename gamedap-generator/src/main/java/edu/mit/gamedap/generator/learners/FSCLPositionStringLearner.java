package edu.mit.gamedap.generator.learners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.gamedap.generator.datatypes.LinePositionStringElt;
import edu.mit.gamedap.generator.datatypes.Vector;

/**
 * A string-basec competitive learner, using the special competitive learning techniques of
 * frequency-sensitive learning and rival penalization.
 * https://github.com/sampsyo/ap/blob/master/code/cl/fscl.py 
 */
public class FSCLPositionStringLearner extends PositionStringCompetitiveLearner {
  private final Map<Vector<LinePositionStringElt>, Integer> neuronWins;
  private final double rivalPenalty;

  public FSCLPositionStringLearner(double learningRate, double maxPosition, Set<Character> characterSet) {
    super(learningRate, maxPosition, characterSet);
    this.neuronWins = new HashMap<>();
    this.rivalPenalty = -learningRate;
  }

  public FSCLPositionStringLearner(double learningRate, double rivalPenalty, int maxPosition, Set<Character> characterSet) {
    super(learningRate, maxPosition, characterSet);
    this.neuronWins = new HashMap<>();
    this.rivalPenalty = rivalPenalty;
  }

  @Override
  public void initialize(int neuronCount, List<Vector<LinePositionStringElt>> stimuli) {
    super.initialize(neuronCount, stimuli);
    neuronWins.clear();
  }
  

  @Override
  double getNeuronActivation(Vector<LinePositionStringElt> neuron, Vector<LinePositionStringElt> stimulus) {
    int nw = neuronWins.getOrDefault(neuron, 0);
    return nw * neuron.distance(stimulus);
  }
  
  @Override
  void trainSingleStimulus(Vector<LinePositionStringElt> stimulus, double learningRate) {
    List<Vector<LinePositionStringElt>> winningNeurons = this.getWinningNeurons(stimulus, 2);
    Vector<LinePositionStringElt> winner = winningNeurons.get(0);
    Vector<LinePositionStringElt> rival = winningNeurons.get(1);

    neuronWins.merge(winner, 1, Integer::sum);
    this.trainSelectedNeuron(stimulus, winner, learningRate);
    this.trainSelectedNeuron(stimulus, rival, rivalPenalty);
  }
}
