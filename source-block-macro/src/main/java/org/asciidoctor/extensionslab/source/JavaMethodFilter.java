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
import java.util.Arrays;
import java.util.List;

public class JavaMethodFilter {

    private class MethodFilterListener extends Java8BaseListener {

        private final String methodName;

        private final String[] parameterTypes;

        private Interval interval;

        MethodFilterListener(String methodName, String[] parameterTypes) {
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;

        }

        @Override
        public void exitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
            String currentMethodName = ctx.methodHeader().methodDeclarator().Identifier().getSymbol().getText();
            if (currentMethodName.equals(methodName) && matchesParameters(ctx)) {
                if (interval != null) {
                    throw new IllegalArgumentException("Multiple matching methods found for methodName " + methodName +
                        parameterTypes == null ? "" : "(" + String.join(", ", Arrays.asList(parameterTypes)));
                }
                interval = ctx.getSourceInterval();
            }
        }

        public Interval getInterval() {
            return interval;
        }


        private boolean matchesParameters(Java8Parser.MethodDeclarationContext ctx) {
            if (parameterTypes == null) {
                return true;
            }

            Java8Parser.FormalParameterListContext formalParameterListContext = ctx.methodHeader().methodDeclarator().formalParameterList();
            if (parameterTypes.length == 0) {
                if (formalParameterListContext == null) {
                    return true;
                } else if (formalParameterListContext.lastFormalParameter() != null) {
                    return false;
                }
            }

            if (parameterTypes.length != 0 &&
                (formalParameterListContext == null
                    || formalParameterListContext.lastFormalParameter() == null)) {
                return false;
            }

            if (parameterTypes.length == 1 && formalParameterListContext.formalParameters() != null) {
                return false;
            }
            if (parameterTypes.length > 1 &&
                (formalParameterListContext.formalParameters() == null
                    || formalParameterListContext.formalParameters().formalParameter() == null
                    || parameterTypes.length != formalParameterListContext.formalParameters().formalParameter().size() + 1)) {
                return false;
            }

            // Match first n - 1 parameters
            for (int i = 0; i < parameterTypes.length - 1; i++) {
                if (!matchesParameter(parameterTypes[i], formalParameterListContext.formalParameters().formalParameter().get(i).unannType())) {
                    return false;
                }
            }
            // Match last parameter
            final String baseType;
            final Java8Parser.UnannTypeContext unannType;
            Java8Parser.LastFormalParameterContext lastParam = formalParameterListContext.lastFormalParameter();
            if (parameterTypes[parameterTypes.length - 1].endsWith("...")) {
                // Last requested param is vararg
                if (lastParam.formalParameter() != null) {
                    return false;
                }
                baseType = parameterTypes[parameterTypes.length - 1].substring(0, parameterTypes[parameterTypes.length - 1].length() - 3);
                unannType = lastParam.unannType();
            } else {
                if (lastParam.formalParameter() == null) {
                    return false;
                }
                baseType = parameterTypes[parameterTypes.length - 1];
                unannType = lastParam.formalParameter().unannType();
            }

            if (!matchesParameter(baseType, unannType)) {
                return false;
            }

            return true;
        }

        private boolean matchesParameter(String parameterType, Java8Parser.UnannTypeContext unannTypeContext) {

            if (unannTypeContext.unannPrimitiveType() != null) {

                return matchesPrimitiveType(parameterType, unannTypeContext.unannPrimitiveType());

            } else if (unannTypeContext.unannReferenceType() != null) {

                return matchesReferenceType(parameterType, unannTypeContext.unannReferenceType());

            }
            return false;
        }

        private boolean matchesReferenceType(String parameterType, Java8Parser.UnannReferenceTypeContext unannReferenceTypeContext) {

            if (unannReferenceTypeContext.unannArrayType() != null) {

                return matchesArray(parameterType, unannReferenceTypeContext.unannArrayType());

            } else if (unannReferenceTypeContext.unannClassOrInterfaceType() != null) {

                return matchesClassOrInterfaceType(parameterType, unannReferenceTypeContext.unannClassOrInterfaceType());

            } else if (unannReferenceTypeContext.unannTypeVariable() != null) {

                return matchesTypeVariable(parameterType, unannReferenceTypeContext.unannTypeVariable());

            }
            return false;
        }

        private boolean matchesTypeVariable(String parameterType, Java8Parser.UnannTypeVariableContext unannTypeVariableContext) {
            return parameterType.equals(unannTypeVariableContext.Identifier().getSymbol().getText());
        }

        private boolean matchesArray(String parameterType, Java8Parser.UnannArrayTypeContext unannArrayTypeContext) {

            if (parameterType.endsWith(unannArrayTypeContext.dims().getText())) {

                final String baseParameterType = parameterType.substring(0, parameterType.indexOf('[')).trim();

                if (unannArrayTypeContext.unannClassOrInterfaceType() != null) {

                    return matchesClassOrInterfaceType(baseParameterType, unannArrayTypeContext.unannClassOrInterfaceType());

                } else if (unannArrayTypeContext.unannPrimitiveType() != null) {

                    return matchesPrimitiveType(baseParameterType, unannArrayTypeContext.unannPrimitiveType());

                } else if (unannArrayTypeContext.unannTypeVariable() != null) {

                    return matchesTypeVariable(parameterType, unannArrayTypeContext.unannTypeVariable());
                }
            }
            return false;
        }

        private boolean matchesClassOrInterfaceType(String parameterType, Java8Parser.UnannClassOrInterfaceTypeContext classOrInterfaceTypeContext) {

            String simpleClassName = null;

            if (classOrInterfaceTypeContext.unannClassType_lf_unannClassOrInterfaceType() != null && classOrInterfaceTypeContext.unannClassType_lf_unannClassOrInterfaceType().size() > 0) {

                List<Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext> typePath = classOrInterfaceTypeContext.unannClassType_lf_unannClassOrInterfaceType();
                simpleClassName = typePath.get(typePath.size() - 1).Identifier().getSymbol().getText();

            } else if (classOrInterfaceTypeContext.unannInterfaceType_lf_unannClassOrInterfaceType() != null && classOrInterfaceTypeContext.unannInterfaceType_lf_unannClassOrInterfaceType().size() > 0) {

                List<Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext> typePath = classOrInterfaceTypeContext.unannInterfaceType_lf_unannClassOrInterfaceType();
                simpleClassName = typePath.get(typePath.size() - 1).unannClassType_lf_unannClassOrInterfaceType().Identifier().getSymbol().getText();

            } else if (classOrInterfaceTypeContext.unannClassType_lfno_unannClassOrInterfaceType() != null) {

                simpleClassName = classOrInterfaceTypeContext.unannClassType_lfno_unannClassOrInterfaceType().Identifier().getSymbol().getText();

            } else if (classOrInterfaceTypeContext.unannInterfaceType_lfno_unannClassOrInterfaceType() != null) {

                simpleClassName = classOrInterfaceTypeContext.unannInterfaceType_lfno_unannClassOrInterfaceType().unannClassType_lfno_unannClassOrInterfaceType().Identifier().getSymbol().getText();

            }

            return parameterType.equals(simpleClassName);
        }

        private boolean matchesPrimitiveType(String parameterType, Java8Parser.UnannPrimitiveTypeContext context) {
            return parameterType.equals(context.getText());
        }

    }

    public String filterMethod(String compilationUnit, String methodNameAndParams) {

        final String methodName = extractMethodName(methodNameAndParams);
        final String[] params = extractParams(methodNameAndParams);

        ANTLRInputStream in = new ANTLRInputStream(compilationUnit);
        TokenStream tokenStream = new CommonTokenStream(new Java8Lexer(in));
        Java8Parser parser = new Java8Parser(tokenStream);

        MethodFilterListener filterListener = new MethodFilterListener(methodName, params);

        parser.addParseListener(filterListener);
        parser.setBuildParseTree(true);
        parser.compilationUnit();

        if (filterListener.getInterval() == null) {
            throw new IllegalArgumentException("Method '" + methodName + "' not found!");
        }

        final Token startToken = tokenStream.get(filterListener.getInterval().a);
        final Token endToken = tokenStream.get(filterListener.getInterval().b);

        final int startLine = startToken.getLine();
        final int endLine = endToken.getLine();

        return getLines(compilationUnit, startLine, endLine);
    }

    private String extractMethodName(final String methodNameAndParams) {
        final int braceIndex = methodNameAndParams.indexOf('(');
        if (braceIndex >= 0) {
            return methodNameAndParams.substring(0, braceIndex);
        } else {
            return methodNameAndParams;
        }
    }

    private String[] extractParams(final String methodNameAndParams) {
        final int braceIndex = methodNameAndParams.indexOf('(');
        if (braceIndex < 0) {
            return null;
        }

        final String parameterList = methodNameAndParams.substring(braceIndex + 1, methodNameAndParams.length() - 1);

        String[] ret = parameterList.split("\\s*,\\s*");
        if (ret.length == 1 && "".equals(ret[0])) {
            return new String[0];
        } else {
            return ret;
        }

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
