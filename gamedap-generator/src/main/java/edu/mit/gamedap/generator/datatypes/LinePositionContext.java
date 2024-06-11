package edu.mit.gamedap.generator.datatypes;

import java.util.Random;

/**
 * A context object containing a single value, related to the proximity of the vector to the beginning of a text line.
 * Two indices closer to the beginning of the line are considered to be "closer together" than two equally spaced indices
 * near the end of a line.
 */
public class LinePositionContext implements VectorContext {

    private static final long LINE_POSITION_WEIGHTING = 20;
    private static final double DISTANCE_CONST = 1 / (1 - (1 / Math.E)); // Based on _linePositionConverter

    private long positionIndex;
    private final long maxLinePosition;

    public LinePositionContext(long positionIndex) {
        this.positionIndex = positionIndex;
        this.maxLinePosition = -1;
    }

    public LinePositionContext(long positionIndex, long maxLinePosition) {
        this.positionIndex = positionIndex;
        this.maxLinePosition = maxLinePosition;
    }

    public static LinePositionContext makeRandom(long maxLinePosition) {
        LinePositionContext newContext = new LinePositionContext(0, maxLinePosition);
        newContext.randomize();
        return newContext;
    }

    public long getPositionIndex() {
        return positionIndex;
    }

    @Override
    public int getContextSize() {
        return 1;
    }

    @Override
    public void randomize() {
        Random rng = new Random();
        if (this.maxLinePosition > 0) {
            this.positionIndex = rng.nextLong(2 * this.maxLinePosition);
        } else {
            this.positionIndex = rng.nextLong(
                Math.max(this.positionIndex - LINE_POSITION_WEIGHTING, 0),
                this.positionIndex + LINE_POSITION_WEIGHTING);
        }
    }

    /**
     * A function on line position integers to emphasize substrings closer to the beginning of a line.
     * 
     * @param linePosition The position of a substring within a line.
     * @return A value such that lower linePositions are closer together, and higher linePositions are further apart.
     */
    private double _linePositionConverter(long linePosition) {
      return Math.exp((linePosition / LINE_POSITION_WEIGHTING) - 1);
    }

    @Override
    public double contextDistance(VectorContext other) {
        assert(this.getContextSize() == other.getContextSize());

        if (other instanceof LinePositionContext) {
            LinePositionContext ov = (LinePositionContext) other;
            double a = _linePositionConverter(this.positionIndex);
            double b = _linePositionConverter(ov.positionIndex);
            return 1 - Math.exp(- DISTANCE_CONST * Math.abs(a - b));
        } else {
            return 1;
        }
    }

    @Override
    public void becomeSimilarTo(VectorContext other, double similarityProportion) {
        if (other instanceof LinePositionContext) {
            LinePositionContext olpc = (LinePositionContext) other;
            double currentDistance = this.contextDistance(other);
            double targetDistance = currentDistance * (1 - similarityProportion);
            this.positionIndex = _getIndexWithDistance(olpc.positionIndex, targetDistance);
        }
    }

    @Override
    public void becomeDifferentFrom(VectorContext other, double differenceProportion) {
        if (other instanceof LinePositionContext) {
            LinePositionContext olpc = (LinePositionContext) other;
            double currentDistance = this.contextDistance(other);
            double targetDistance = currentDistance + ((1 - currentDistance) * differenceProportion);
            this.positionIndex = _getIndexWithDistance(olpc.positionIndex, targetDistance);
        }
    }

    private long _getIndexWithDistance(long otherIndex, double targetDistance) {
        if (targetDistance <= 0) {
            return otherIndex;
        } else if (targetDistance >= 1) {
            return otherIndex + (LINE_POSITION_WEIGHTING * 3);
        } else {
            double convertedOther = _linePositionConverter(otherIndex);
            double convertedTarget = convertedOther + (Math.log(1 - targetDistance) / (- DISTANCE_CONST));
            return Math.round(LINE_POSITION_WEIGHTING * (Math.log(convertedTarget) + 1));
        }
    }

    @Override
    public String toString() {
      return String.format("<LinePositionContext: '%s'>", this.positionIndex);
    }
  
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof LinePositionContext)) {
        return false;
      }
      LinePositionContext ov = (LinePositionContext) obj;
      return ov.positionIndex == this.positionIndex;
    }
}
