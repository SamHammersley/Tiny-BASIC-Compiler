package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;

public abstract class Statement implements AbstractSyntaxTreeNode {

    protected final String keyword;

    public Statement(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return keyword + " ";
    }

}