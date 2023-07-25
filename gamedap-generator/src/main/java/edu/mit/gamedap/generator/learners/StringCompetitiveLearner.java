package edu.mit.gamedap.generator.learners;

import java.util.List;

import edu.mit.gamedap.generator.datatypes.Vector;

/**
 * A competitive learner that uses strings.
 * 
 * @see CompetitiveLearner
 */
public class StringCompetitiveLearner extends CompetitiveLearner<Character> {

  public StringCompetitiveLearner(int neuronCount, double learningRate, List<Vector<Character>> stimuli) {
    super(neuronCount, learningRate, stimuli);
    //TODO Auto-generated constructor stub
  }

  @Override
  Vector<Character> generateNeuron() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'generateNeuron'");
  }

  @Override
  double getNeuronActivation(Vector<Character> neuron, Vector<Character> stimulus) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getNeuronActivation'");
  }

  @Override
  void trainSingleStimulus(Vector<Character> stimulus) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'trainSingleStimulus'");
  }

  @Override
  int quantize(Vector<Character> stimulus) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'quantize'");
  }
}
