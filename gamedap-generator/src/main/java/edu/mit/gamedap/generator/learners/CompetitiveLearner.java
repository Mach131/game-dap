package edu.mit.gamedap.generator.learners;

import java.util.ArrayList;
import java.util.List;

import edu.mit.gamedap.generator.datatypes.Vector;
import edu.mit.gamedap.generator.datatypes.VectorCluster;

/**
 * Represents an environment for the competitive learning algorithm implemented in 
 * https://github.com/sampsyo/ap/blob/master/code/cl/__init__.py. Essentially, aims to
 * train a set of initially random data to align with the data provided as input.
 */
public abstract class CompetitiveLearner<T> {

  private final List<Vector<T>> stimuli;
  private final List<Vector<T>> neurons;
  private final double learningRate;
  private double progress;

  public CompetitiveLearner(int neuronCount, double learningRate, List<Vector<T>> stimuli) {
    this.stimuli = stimuli;
    this.learningRate = learningRate;

    this.neurons = new ArrayList<>();
    for (int i = 0; i < neuronCount; i++) {
      this.neurons.add(this.generateNeuron());
    }

    this.progress = 0;
  }

  /**
   * Generates a new random neuron.
   * 
   * @return A random vector of the learning data type
   */
  abstract Vector<T> generateNeuron();

  /**
   * Gets the activation of a neuron with respect to a stimulus. In a step of competitive learning,
   * the neuron with the minimum activation will be trained.
   * 
   * @param neuron Neuron to check
   * @param stimulus Stimulus to check
   * @return The activation of the neuron with respect to the stimulus
   */
  abstract double getNeuronActivation(Vector<T> neuron, Vector<T> stimulus);

  /**
   * Performs one step of training for a given stimulus. Will often use 
   * {@link CompetitiveLearner#getWinningNeurons(Vector, int) getWinningNeuron(s)} to select a neuron
   * to modify based on the stimulus.
   * 
   * @param stimulus The stimulus to use for training
   */
  abstract void trainSingleStimulus(Vector<T> stimulus);

  /**
   * Returns the index of the neuron closest to the given stimulus. Assumes that neurons are present and
   * {@link CompetitiveLearner#train(int) training} has been completed. The distance metric used is
   * implementation-dependent.
   * 
   * @param stimulus The stimulus to quantize
   * @return The index of the nearest neuron
   */
  abstract int quantize(Vector<T> stimulus);

  /**
   * Returns the neurons selected to be trained by a given stimulus. Based on the activations returned
   * by {@link CompetitiveLearner#getNeuronActivation(Vector, Vector) getNeuronActivation}.
   * 
   * @param stimulus The stimulus to train on
   * @param neuronCount The number of neurons to train
   * @return A list of neuronCount neurons with minimal activations with respect to stimulus.
   */
  List<Vector<T>> getWinningNeurons(Vector<T> stimulus, int neuronCount) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  /**
   * Similar to getWinningNeurons, but returns a single neuron.
   * 
   * @see CompetitiveLearner#getWinningNeurons(Vector, int)
   */
  Vector<T> getWinningNeuron(Vector<T> stimulus) {
    return getWinningNeurons(stimulus, 1).get(0);
  }

  /**
   * Trains the neurons on the provided stimuli for a given number of epochs. Within each epoch,
   * training will be done for every stimuli in a randomized order.
   * 
   * @param epochs the number of training iterations to perform
   * @see CompetitiveLearner#trainSingleStimulus(Vector)
   */
  public void train(int epochs) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  /**
   * Groups the stimuli into clusters, mapping them to their nearest neuron (as indicated by
   * {@link CompetitiveLearner#quantize(Vector) quantize}). Assumes that neurons are present
   * and {@link CompetitiveLearner#train(int) training} has been completed.
   * 
   * @return A list of clusters with neurons as the centers and stimuli as the clustered vectors, 
   * where every stimulus is in exactly one cluster.
   */
  public List<VectorCluster<T>> cluster() {
    throw new UnsupportedOperationException("not yet implemented");
  }
}