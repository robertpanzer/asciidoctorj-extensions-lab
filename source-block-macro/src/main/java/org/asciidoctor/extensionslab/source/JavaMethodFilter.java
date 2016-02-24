package org.asciidoctor.extensionslab.source;

import org.asciidoctor.extensionslab.source.java8grammar.*;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class JavaMethodFilter {

    private class MethodFilterListener extends Java8BaseListener {

        private final String methodName;

        private Interval interval;

        MethodFilterListener(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public void exitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
            String currentMethodName = ctx.methodHeader().methodDeclarator().Identifier().getSymbol().getText();
            if (currentMethodName.equals(methodName)) {
                interval = ctx.getSourceInterval();
            }
        }

        public Interval getInterval() {
            return interval;
        }
    }


    public String filterMethod(String compilationUnit, String methodName) {

        ANTLRInputStream in = new ANTLRInputStream(compilationUnit);
        TokenStream tokenStream = new CommonTokenStream(new Java8Lexer(in));
        Java8Parser parser = new Java8Parser(tokenStream);

        MethodFilterListener filterListener = new MethodFilterListener(methodName);

        parser.addParseListener(filterListener);
        parser.setBuildParseTree(true);
        parser.compilationUnit();

        final Token startToken = tokenStream.get(filterListener.getInterval().a);
        final Token endToken = tokenStream.get(filterListener.getInterval().b);

        final int startLine = startToken.getLine();
        final int endLine = endToken.getLine();

        return getLines(compilationUnit, startLine, endLine);
    }

    private String getLines(final String compilationUnit, final int startLine, final int endLine) {

        final BufferedReader in = new BufferedReader(new StringReader(compilationUnit));
        final StringBuilder sb = new StringBuilder();

        int currentLine = 1;
        try {
            String line = in.readLine();

            while (line != null && currentLine <= endLine) {
                if (currentLine >= startLine) {
                    sb.append(line).append("\n");
                }
                line = in.readLine();
                currentLine++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

}
