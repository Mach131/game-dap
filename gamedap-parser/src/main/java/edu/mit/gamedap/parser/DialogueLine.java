package edu.mit.gamedap.parser;

import java.util.List;

public class DialogueLine {
    private final List<String> parts;
    private final List<String> presentDelims;

    public DialogueLine(List<String> parts, List<String> presentDelims) {
        this.parts = parts;
        this.presentDelims = presentDelims;
    }

    public int numParts() {
        return this.parts.size();
    }
    
    public String getPart(int i) {
        return parts.get(i);
    }

    public String getDelim(int i) {
        return presentDelims.get(i);
    }


    @Override
    public String toString() {
        String output = "[Pair] ";
        for (String part : this.parts) {
            output += "| " + part;
        }
        return output;
    }
}
