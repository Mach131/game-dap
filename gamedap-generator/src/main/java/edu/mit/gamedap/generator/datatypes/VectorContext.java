package edu.mit.gamedap.generator.datatypes;

/**
 * Represents additional context related to a string vector, containing arbitrary information related to where it appears
 * in the original text.
 * Also provides methods that indicate how the context should be compared.
 */
public interface VectorContext {
    /**
     * Gets the number of components tracked by the context.
     * @return
     */
    public int getContextSize();

    /**
     * Randomly re-initializes the values in the context. Does not change the size.
     */
    public void randomize();

    /**
     * Gets the "distance" between two context objects that contain the same types of data.
     * Should return a value between 0 and this.getContextSize(), where a higher value
     * indicates "more different" contexts. If there is a type mismatch, always returns
     * the maximum (this.getContextSize())
     * 
     * @param other context object
     * @return weighted sum of "distances" between each context component; between 0 and this.getContextSize()
     */
    public double contextDistance(VectorContext other);

    /**
     * Mutates this context to become more similar to another. Has no effect if the types
     * the contexts contain do not match.
     * 
     * @param other
     * @param similarityProportion between 0 and 1, the extent to which this should become more similar
     *  to other; i.e. no change if 0, becomes equal to other (has a distance of 0) if 1
     */
    public void becomeSimilarTo(VectorContext other, double similarityProportion);

    /**
     * Mutates this context to become more different from another. Has no effect if the types
     * the contexts contain do not match.
     * 
     * @param other
     * @param similarityProportion between 0 and 1, the extent to which this should become more different
     *  from other; i.e. no change if 0, has a distance (very close to) this.getContextSize() if 1
     */
    public void becomeDifferentFrom(VectorContext other, double differenceProportion);
}
