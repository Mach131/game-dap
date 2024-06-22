package edu.mit.gamedap.generator.datatypes;

/**
 * A recursive-ish structure for tracking previously grouped text.
 */
public interface TextGroup {
    public static TextGroup asGroup(String text) {
        return new TextGroupLeaf(text);
    }
    public TextGroup joinWith(TextGroup other);
    public String getText();
    public int getDepth();
}

class TextGroupLeaf implements TextGroup {
    private final String text;

    public TextGroupLeaf(String text) {
        this.text = text;
    }
    public TextGroup joinWith(TextGroup other) {
        return new TextGroupJoin(this, other);
    }
    public String getText() { return this.text; }
    public int getDepth() { return 0; }
}

class TextGroupJoin implements TextGroup {
    private final String text;
    private final int depth;

    public TextGroupJoin(TextGroup firstGroup, TextGroup secondGroup) {
        this.text = firstGroup.getText() + secondGroup.getText();
        this.depth = Math.max(firstGroup.getDepth(), secondGroup.getDepth()) + 1;
    }
    public TextGroup joinWith(TextGroup other) {
        return new TextGroupJoin(this, other);
    }
    public String getText() { return this.text; }
    public int getDepth() { return this.depth; }
}