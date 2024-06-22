package edu.mit.gamedap.generator.datatypes;

import java.util.Random;

/**
 * A context object that can wrap another context object, adding on information about how many times the vector
 * has been "tokenized" during the process of parsing.
 */
public class MetaContext<C extends VectorContext> implements VectorContext {

    private static final int DEFAULT_MAX_DEPTH = 1;
    private static final double DEFAULT_DEPTH_INCREASE_PROBABILITY = 0.025;
    private static final double RECURSIVE_DEPTH_CHILD_DISTANCE_WEIGHT = 0.75;

    private int recursiveDepth;
    private C context;
    private int maxDepth;
    private double depthIncreaseProbability;

    public MetaContext(C context, int recursiveDepth) {
        this.context = context;
        this.recursiveDepth = recursiveDepth;
        this.maxDepth = DEFAULT_MAX_DEPTH;
        this.depthIncreaseProbability = DEFAULT_DEPTH_INCREASE_PROBABILITY;
    }

    public MetaContext(C context, int recursiveDepth, int maxDepth, double depthIncreaseProbability) {
        this.context = context;
        this.recursiveDepth = recursiveDepth;
        this.maxDepth = maxDepth;
        this.depthIncreaseProbability = depthIncreaseProbability;
    }

    public static <C extends VectorContext> MetaContext<C> giveRandomDepth(C newChildContext, int maxDepth, double depthIncreaseProbability) {
        MetaContext<C> newContext = new MetaContext<C>(newChildContext, 0, maxDepth, depthIncreaseProbability);
        newContext.randomize();
        return newContext;
    }

    public C getChildContext() {
        return context;
    }

    @Override
    public int getContextSize() {
        return context.getContextSize() + 1;
    }

    @Override
    public void randomize() {
        this.context.randomize();

        Random random = new Random();
        int randomDepth = 0;
        while (randomDepth < maxDepth) {
            if (random.nextDouble() < depthIncreaseProbability) {
                randomDepth += 1;
            } else {
                break;
            }
        }
        this.recursiveDepth = randomDepth;
    }

    private double getRecursiveDepthWeight() {
        return this.getContextSize() - (this.context.getContextSize() * RECURSIVE_DEPTH_CHILD_DISTANCE_WEIGHT);
    }

    @Override
    public double contextDistance(VectorContext other) {
        assert(this.getContextSize() == other.getContextSize());

        if (other instanceof MetaContext) {
            @SuppressWarnings("unchecked")
            MetaContext<VectorContext> omc = (MetaContext<VectorContext>) other;

            double recursiveDepthWeight = this.getRecursiveDepthWeight();
            double recursiveDistanceComponent = this.recursiveDepth == omc.recursiveDepth ? 0 : recursiveDepthWeight;
            return recursiveDistanceComponent + (RECURSIVE_DEPTH_CHILD_DISTANCE_WEIGHT * this.context.contextDistance(omc.context));
        } else {
            return this.getContextSize();
        }
    }

    @Override
    public void becomeSimilarTo(VectorContext other, double similarityProportion) {
        if (other instanceof MetaContext) {
            @SuppressWarnings("unchecked")
            MetaContext<VectorContext> omc = (MetaContext<VectorContext>) other;


            double totalDistance = this.contextDistance(other);
            double targetDistance = totalDistance * (1 - similarityProportion);
            double targetDelta = totalDistance - targetDistance;

            double maxChildSimilarityContribution = this.context.contextDistance(omc.context) * RECURSIVE_DEPTH_CHILD_DISTANCE_WEIGHT;

            if (targetDelta > maxChildSimilarityContribution) {
                this.recursiveDepth = omc.recursiveDepth;
                double recursiveDepthWeight = this.getRecursiveDepthWeight();
                targetDelta -= recursiveDepthWeight;
            }
            
            if (targetDelta > 0 && maxChildSimilarityContribution > 0) {
                double childSimilarityProportion = targetDelta / maxChildSimilarityContribution;
                assert(childSimilarityProportion <= 1);
                this.context.becomeSimilarTo(omc.context, childSimilarityProportion);
            }
        }
    }

    @Override
    public void becomeDifferentFrom(VectorContext other, double differenceProportion) {
        if (other instanceof MetaContext) {
            @SuppressWarnings("unchecked")
            MetaContext<VectorContext> omc = (MetaContext<VectorContext>) other;

            double totalDistance = this.contextDistance(other);
            double targetDistance = totalDistance + ((this.getContextSize() - totalDistance) * differenceProportion);
            double targetDelta = targetDistance - totalDistance;

            double maxChildDifferenceContribution = (this.context.getContextSize() - this.context.contextDistance(omc.context)) *
                                                        RECURSIVE_DEPTH_CHILD_DISTANCE_WEIGHT;

            if (targetDelta > maxChildDifferenceContribution) {
                if (omc.recursiveDepth == 0) {
                    this.recursiveDepth = 1;
                } else {
                    this.recursiveDepth = omc.recursiveDepth + (((new Random().nextInt(2)) * 2) - 1);
                }
                double recursiveDepthWeight = this.getRecursiveDepthWeight();
                targetDelta -= recursiveDepthWeight;
            }
            
            if (targetDelta > 0 && maxChildDifferenceContribution > 0) {
                double childDifferenceProportion = targetDelta / maxChildDifferenceContribution;
                assert(childDifferenceProportion <= 1);
                this.context.becomeDifferentFrom(omc.context, childDifferenceProportion);
            }
        }
    }

    @Override
    public String toString() {
      return String.format("<Meta Context: '%s', %s>", this.recursiveDepth, this.context.toString());
    }
  
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof MetaContext)) {
        return false;
      }
      @SuppressWarnings("unchecked")
      MetaContext<VectorContext> omc = (MetaContext<VectorContext>) obj;
      return omc.recursiveDepth == this.recursiveDepth && omc.context.equals(this.context);
    }
}
