package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;

import java.util.Objects;

public abstract class Statement implements AbstractSyntaxTreeNode {

    protected final String keyword;

    public Statement(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return keyword + " ";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), keyword.hashCode());
    }

}