package edu.mit.gamedap.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.*;

import edu.mit.gamedap.parser.GeneratedGrammarParser.PairContext;

public class ExamplePairBuilder extends GeneratedGrammarBaseListener {
    private List<ExamplePair> pairs = new ArrayList<>();

    public List<ExamplePair> getPairs() {
        return pairs;
    }

    @Override
    public void enterPair(PairContext ctx) {
        List<TerminalNode> pairNodes = ctx.TEXT();
        ExamplePair newPair = new ExamplePair(pairNodes.get(0).getText(), pairNodes.get(1).getText());
        this.pairs.add(newPair);
    }
}
