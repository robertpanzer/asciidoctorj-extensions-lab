package org.asciidoctor.extensionslab.source;

import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@Name("source")
public class SourceBlockMacro extends BlockMacroProcessor {

    @Override
    public Object process(StructuralNode parent, String target, Map<String, Object> map) {

        final String className = extractClassName(target);
        final String memberName = extractMemberName(target);

        final File sourceFile = findSourceFile(parent, className);

        try {
            return createBlock(parent, "listing", getContent(sourceFile));
        } catch (IOException e) {
            throw new IllegalStateException("Could not find file " + sourceFile);
        }
    }

    private String getContent(File sourceFile) throws IOException {

        final StringBuilder sb = new StringBuilder((int) sourceFile.length());

        final FileReader in = new FileReader(sourceFile);
        final char[] buf = new char[32];
        try {
            int charsRead = in.read(buf);
            while (charsRead > 0) {
                sb.append(buf, 0, charsRead);
                charsRead = in.read(buf);
            }
        } finally {
            in.close();
        }
        return sb.toString();
    }

    private File findSourceFile(StructuralNode parent, String className) {

        File sourceBaseDir = getSourceBaseDir(parent);
        String path = extractPath(className);

        return new SourceFileLocator(sourceBaseDir, path).findSourceFile();
    }


    private File getSourceBaseDir(StructuralNode parent) {
        String sourceBaseDir = (String) parent.getAttr("source-base-dir", null, true);
        if (sourceBaseDir != null) {
            return new File(sourceBaseDir);
        }

        return new File((String) parent.getAttr("docdir", null, true));
    }

    private String extractPath(String className) {
        String[] parts = className.split("$");
        String outerClassName = parts[0];
        return outerClassName.replaceAll("\\.", "/") + ".java";
    }


    private String extractClassName(String target) {
        return target.split("#")[0];
    }

    private String extractMemberName(String target) {
        String[] parts = target.split("#");
        if (parts.length > 1) {
            return parts[2];
        } else {
            return null;
        }
    }

}
