package edu.mit.gamedap.generator.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.mit.gamedap.generator.Utils;
import edu.mit.gamedap.generator.datatypes.StringVector;
import edu.mit.gamedap.generator.datatypes.Vector;
import edu.mit.gamedap.generator.datatypes.VectorCluster;
import edu.mit.gamedap.generator.learners.CompetitiveLearner;
import edu.mit.gamedap.generator.learners.FSCLStringLearner;
import edu.mit.gamedap.generator.learners.StringCompetitiveLearner;

/**
 * Contains parsing methods inspired by https://www.cs.hmc.edu/~asampson/ap/technique.html
 */
public class StringParseLearningPrimer implements ParseLearningPrimer<Character> {
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
  public List<Vector<Character>> makeSubstringVectors(String text, int w, Set<Character> characterSet) {
    assert(w <= text.length());

    List<Vector<Character>> result = new ArrayList<>();
    for (int i = 0; i <= text.length() - w; i++) {
      result.add(new StringVector(
        text.substring(i, i + w), characterSet));
    }

    return result;
  }

  @Override
  public List<VectorCluster<Character>> assignVectorClusters(List<Vector<Character>> substrings, Set<Character> characterSet) {
    CompetitiveLearner<Character> cl = new FSCLStringLearner(learningRate, characterSet);
    cl.initialize(neuronCount, substrings);
    cl.train(trainingEpochs);
    return cl.cluster();
  }
}
