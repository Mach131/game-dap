package edu.mit.gamedap.generator.datatypes;

/**
 * An empty context object.
 */
public class EmptyContext implements VectorContext {
    @Override
    public int getContextSize() {
        return 0;
    }

    @Override
    public void randomize() {

    }

    @Override
    public double contextDistance(VectorContext other) {
        return 0;
    }

    @Override
    public void becomeSimilarTo(VectorContext other, double similarityProportion) {
    }

    @Override
    public void becomeDifferentFrom(VectorContext other, double differenceProportion) {
    }
}
