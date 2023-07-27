package edu.mit.gamedap.generator.learners;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

  public CompetitiveLearner(double learningRate) {
    this.learningRate = learningRate;
    this.stimuli = new ArrayList<>();
    this.neurons = new ArrayList<>();
  }

  /**
   * Prepares the environment for learning, resetting the neurons and stimuli.
   * 
   * @param neuronCount The number of neurons to generate
   * @param stimuli The stimuli to train on, assumed to be a non-empty list with vectors
   * of a constant size.
   */
  public void initialize(int neuronCount, List<Vector<T>> stimuli) {
    this.neurons.clear();
    this.stimuli.clear();

    this.stimuli.addAll(stimuli);
    int size = stimuli.get(0).size();
    for (int i = 0; i < neuronCount; i++) {
      this.neurons.add(this.generateNeuron(size));
    }
  }

  /**
   * Generates a new random neuron.
   * 
   * @param size the size of the vectors to generate
   * @return A random vector of the learning data type
   */
  abstract Vector<T> generateNeuron(int size);

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
   * @param learningRate The size of the learning adjustment to be made
   */
  abstract void trainSingleStimulus(Vector<T> stimulus, double learningRate);

  /**
   * Returns the neurons selected to be trained by a given stimulus. Based on the activations returned
   * by {@link CompetitiveLearner#getNeuronActivation(Vector, Vector) getNeuronActivation}.
   * 
   * @param stimulus The stimulus to train on
   * @param neuronCount The number of neurons to train
   * @return A list of neuronCount neurons with minimal activations with respect to stimulus.
   */
  List<Vector<T>> getWinningNeurons(Vector<T> stimulus, int neuronCount) {
    return this.neurons.stream()
      .sorted(Comparator.comparingDouble(n -> this.getNeuronActivation(n, stimulus)))
      .limit(neuronCount)
      .toList();
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
   * @see CompetitiveLearner#trainSingleStimulus(Vector, Double)
   */
  public void train(int epochs) {
    for (int epoch = 0; epoch < epochs; epoch++) {
      for (Vector<T> stimulus : this.stimuli) {
        this.trainSingleStimulus(stimulus, this.learningRate);
      }
    }
  }

  /**
   * Returns the index of the neuron closest to the given stimulus. Assumes that neurons are present and
   * {@link CompetitiveLearner#train(int) training} has been completed. The distance metric used is
   * based on the vector type.
   * 
   * @param stimulus The stimulus to quantize
   * @return The index of the nearest neuron
   */
  public int quantize(Vector<T> stimulus) {
    int idx = 0;
    double minDist = this.neurons.get(0).distance(stimulus);

    if (this.neurons.size() > 1) {
      for (int i = 0; i < this.neurons.size(); i++) {
        double dist = this.neurons.get(i).distance(stimulus);
        if (dist < minDist) {
          idx = i;
          minDist = dist;
        }
      }
    }
    
    return idx;
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
    // Quantize all stimuli
    List<Integer> quantizedIndices = this.stimuli.stream()
      .map(s -> this.quantize(s))
      .toList();

    // Organize each stimulus under the corresponding neuron index
    Map<Integer, List<Vector<T>>> quantizationMap = new HashMap<>();
    for (int i = 0; i < quantizedIndices.size(); i++) {
      int neuronIndex = quantizedIndices.get(i);
      if (!quantizationMap.containsKey(neuronIndex)) {
        quantizationMap.put(neuronIndex, new ArrayList<>());
      }
      quantizationMap.get(neuronIndex).add(this.stimuli.get(i));
    }

    // create VectorClusters
    return quantizationMap.keySet().stream()
      .map(n -> new VectorCluster<>(this.neurons.get(n), quantizationMap.get(n)))
      .toList();
  }
}
