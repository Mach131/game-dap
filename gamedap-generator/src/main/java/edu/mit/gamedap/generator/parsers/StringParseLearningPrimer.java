package edu.mit.gamedap.generator.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.mit.gamedap.generator.datatypes.EmptyContext;
import edu.mit.gamedap.generator.datatypes.StringVector;
import edu.mit.gamedap.generator.datatypes.Vector;
import edu.mit.gamedap.generator.datatypes.VectorCluster;
import edu.mit.gamedap.generator.learners.CompetitiveLearner;
import edu.mit.gamedap.generator.learners.FSCLStringLearner;

/**
 * Contains parsing methods inspired by https://www.cs.hmc.edu/~asampson/ap/technique.html
 */
public class StringParseLearningPrimer implements ParseLearningPrimer<EmptyContext, Character> {
  private final int neuronCount;
  private final double learningRate;
  private final int trainingEpochs;

  public StringParseLearningPrimer() {
    this.neuronCount = SampsonParser.DEFAULT_NEURON_COUNT;
    this.learningRate = SampsonParser.DEFAULT_LEARNING_RATE;
    this.trainingEpochs = SampsonParser.DEFAULT_TRAINING_EPOCHS;
  }

  public StringParseLearningPrimer(int neuronCount, double learningRate, int trainingEpochs) {
    this.neuronCount = neuronCount;
    this.learningRate = learningRate;
    this.trainingEpochs = trainingEpochs;
  }

  @Override
  public List<Vector<EmptyContext, Character>> makeSubstringVectors(String text, int w, Set<Character> characterSet) {
    assert(w <= text.length());

    List<Vector<EmptyContext, Character>> result = new ArrayList<>();
    for (int i = 0; i <= text.length() - w; i++) {
      result.add(new StringVector(
        text.substring(i, i + w), characterSet));
    }

    return result;
  }

  @Override
  public List<VectorCluster<EmptyContext, Character>> assignVectorClusters(List<Vector<EmptyContext, Character>> substrings,
      Set<Character> characterSet) {
    CompetitiveLearner<EmptyContext, Character> cl = new FSCLStringLearner(learningRate, characterSet);
    cl.initialize(neuronCount, substrings);
    cl.train(trainingEpochs);
    return cl.cluster();
  }
}
