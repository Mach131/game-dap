package edu.mit.gamedap.generator;

import java.util.List;

/**
 * A competitive learner that uses integer sequences represented as integer vectors.
 * 
 * @see CompetitiveLearner
 */
public class SequenceCompetitiveLearner extends CompetitiveLearner<Vector<Integer>> {

  public SequenceCompetitiveLearner(int neuronCount, double learningRate, List<Vector<Integer>> stimuli) {
    super(neuronCount, learningRate, stimuli);
    //TODO Auto-generated constructor stub
  }

  @Override
  Vector<Integer> generateNeuron() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'generateNeuron'");
  }

  @Override
  double getNeuronActivation(Vector<Integer> neuron, Vector<Integer> stimulus) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getNeuronActivation'");
  }

  @Override
  void trainSingleStimulus(Vector<Integer> stimulus) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'trainSingleStimulus'");
  }

  @Override
  int quantize(Vector<Integer> stimulus) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'quantize'");
  }

}
