package edu.mit.gamedap.generator.learners;

import java.util.List;
import java.util.Set;

import edu.mit.gamedap.generator.datatypes.StringVector;
import edu.mit.gamedap.generator.datatypes.Vector;

/**
 * A competitive learner that uses strings.
 * 
 * @see CompetitiveLearner
 */
public class StringCompetitiveLearner extends CompetitiveLearner<Character> {

  private final Set<Character> characterSet;

  /**
   * Initializes the learner with a set of characters to use for neuron generation.
   * 
   * @param learningRate The extent to which neurons are changed in a single learning step
   * @param characterSet The set of characters to use for neuron generation
   */
  public StringCompetitiveLearner(double learningRate, Set<Character> characterSet) {
    super(learningRate);
    this.characterSet = characterSet;
  }

  @Override
  Vector<Character> generateNeuron(int size) {
    return new StringVector(size, this.characterSet);
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
