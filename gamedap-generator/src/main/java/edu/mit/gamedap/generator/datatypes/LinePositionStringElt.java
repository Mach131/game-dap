package edu.mit.gamedap.generator.datatypes;

/**
 * A more generalized element of a LinePositionStringVector. The element representing the line position is
 * an integer; all other elements are characters.
 */
public class LinePositionStringElt {
    public final boolean isCharacter;
    private Character characterElement;
    private Double numberElement;
  
    public LinePositionStringElt(Character elt) {
      this.isCharacter = true;
      this.characterElement = elt;
    }

    public LinePositionStringElt(Double elt) {
        this.isCharacter = false;
        this.numberElement = elt;
    }

    public Double getNumberValue() {
        if (this.isCharacter) {
            return null;
        }
        return this.numberElement;
    }

    public Character getCharacterValue() {
        if (!this.isCharacter) {
            return null;
        }
        return this.characterElement;
    }

    @Override
    public String toString() {
        if (this.isCharacter) {
            return this.characterElement.toString();
        } else {
            return this.numberElement.toString();
        }
    }
  
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof LinePositionStringElt)) {
        return false;
      }
      LinePositionStringElt ov = (LinePositionStringElt) obj;
      if (this.isCharacter != ov.isCharacter) {
        return false;
      }
      if (this.isCharacter) {
        return this.characterElement.equals(ov.characterElement);
      } else {
        return this.numberElement.equals(ov.numberElement);
      }
    }
  }
