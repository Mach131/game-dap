// Generated from edu\mit\gamedap\parser\GeneratedGrammar.g4 by ANTLR 4.13.0
package edu.mit.gamedap.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GeneratedGrammarParser}.
 */
public interface GeneratedGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link GeneratedGrammarParser#section}.
	 * @param ctx the parse tree
	 */
	void enterSection(GeneratedGrammarParser.SectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GeneratedGrammarParser#section}.
	 * @param ctx the parse tree
	 */
	void exitSection(GeneratedGrammarParser.SectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GeneratedGrammarParser#title}.
	 * @param ctx the parse tree
	 */
	void enterTitle(GeneratedGrammarParser.TitleContext ctx);
	/**
	 * Exit a parse tree produced by {@link GeneratedGrammarParser#title}.
	 * @param ctx the parse tree
	 */
	void exitTitle(GeneratedGrammarParser.TitleContext ctx);
	/**
	 * Enter a parse tree produced by {@link GeneratedGrammarParser#pair}.
	 * @param ctx the parse tree
	 */
	void enterPair(GeneratedGrammarParser.PairContext ctx);
	/**
	 * Exit a parse tree produced by {@link GeneratedGrammarParser#pair}.
	 * @param ctx the parse tree
	 */
	void exitPair(GeneratedGrammarParser.PairContext ctx);
}