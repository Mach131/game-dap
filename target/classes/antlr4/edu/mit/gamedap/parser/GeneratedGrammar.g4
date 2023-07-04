grammar GeneratedGrammar;
section
    : (title NEW_LINE)? (pair NEW_LINE)+ EOF ;
title: TEXT ;
pair: TEXT PAIR_SEP TEXT ;
// lexer
TEXT : [a-zA-Z_0-9 ]+ ;
PAIR_SEP : ':' | [\t] ;
NEW_LINE : [\n\r\f]+ ;