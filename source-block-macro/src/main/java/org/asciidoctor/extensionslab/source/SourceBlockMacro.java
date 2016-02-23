package org.asciidoctor.extensionslab.source;

import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.DefaultAttribute;
import org.asciidoctor.extension.DefaultAttributes;
import org.asciidoctor.extension.Name;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Name("source")
@DefaultAttributes({
    @DefaultAttribute(key = SourceBlockMacro.ATTR_TYPE, value = "java")
})
public class SourceBlockMacro extends BlockMacroProcessor {

    static final String ATTR_TYPE = "type";

    static final String ATTR_TAGS = "tags";

    @Override
    public Object process(StructuralNode parent, String target, Map<String, Object> attributes) {

        final String className = extractClassName(target);
        final String[] classNameParts = className.split("\\$");

        final String memberName = extractMemberName(target);

        final File sourceFile = findSourceFile(parent, classNameParts[0], (String) attributes.get(ATTR_TYPE));

        String content = getContent(sourceFile);

        if (memberName == null) {
            if (attributes.containsKey(ATTR_TAGS)) {
                Set<String> tags = new HashSet<String>(Arrays.asList(((String) attributes.get(ATTR_TAGS)).split(" *, *")));
                content = new TagFilter(tags).filterTags(content);
            }
        }

        return createBlock(parent, "listing", content);
    }

    private String getContent(File sourceFile) {

        final StringBuilder sb = new StringBuilder((int) sourceFile.length());

        final FileReader in;
        try {
            in = new FileReader(sourceFile);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Source file " + sourceFile + " not found!");
        }
        final char[] buf = new char[32];
        try {
            int charsRead = in.read(buf);
            while (charsRead > 0) {
                sb.append(buf, 0, charsRead);
                charsRead = in.read(buf);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected exception reading source file " + sourceFile);
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                // We would have to log if we had a logger
            }
        }
        return sb.toString();
    }

    private File findSourceFile(StructuralNode parent, String className, String type) {
        File sourceBaseDir = getSourceBaseDir(parent);
        File sourceFile = new SourceFileLocator(sourceBaseDir, className, type).findSourceFile();
        if (sourceFile == null) {
            throw new IllegalArgumentException("File for class " + className + " not found!");
        }
        return sourceFile;
    }


    private File getSourceBaseDir(StructuralNode parent) {
        String sourceBaseDir = (String) parent.getAttr("source-base-dir", null, true);
        if (sourceBaseDir != null) {
            return new File(sourceBaseDir);
        }

        File docdir = new File((String) parent.getAttr("docdir", null, true));
        return findSourceBaseDir(docdir);
    }

    private File findSourceBaseDir(File docDir) {
        if (docDir.getName().equals("src")) {
            return docDir;
        }
        if (docDir.getParent() == null) {
            // Not found
            return null;
        }
        return findSourceBaseDir(docDir.getParentFile());
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
