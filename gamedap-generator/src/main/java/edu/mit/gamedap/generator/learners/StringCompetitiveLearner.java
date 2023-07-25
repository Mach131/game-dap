package edu.mit.gamedap.generator.learners;

import java.util.List;

import edu.mit.gamedap.generator.datatypes.Vector;

/**
 * A competitive learner that uses strings.
 * 
 * @see CompetitiveLearner
 */
public class StringCompetitiveLearner extends CompetitiveLearner<String> {

  public StringCompetitiveLearner(int neuronCount, double learningRate, List<Vector<String>> stimuli) {
    super(neuronCount, learningRate, stimuli);
    //TODO Auto-generated constructor stub
  }

  @Override
  Vector<String> generateNeuron() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'generateNeuron'");
  }

  @Override
  double getNeuronActivation(Vector<String> neuron, Vector<String> stimulus) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getNeuronActivation'");
  }

  @Override
  void trainSingleStimulus(Vector<String> stimulus) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'trainSingleStimulus'");
  }

  @Override
  int quantize(Vector<String> stimulus) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'quantize'");
  }
}
