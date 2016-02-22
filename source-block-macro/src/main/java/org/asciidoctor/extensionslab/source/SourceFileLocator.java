package org.asciidoctor.extensionslab.source;

import java.io.File;

public class SourceFileLocator {

    private final File sourceBaseDir;

    private final String relativeFileName;

    public SourceFileLocator(File baseDir, String relativeFileName) {
        this.relativeFileName = relativeFileName;

        File dynamicSourceBaseDir = findSourceBaseDir(baseDir);

        if (dynamicSourceBaseDir == null) {
            throw new IllegalStateException("Could not determine source base dir. Please define document attribute 'source-base-dir' to the localion where the source files are stored, e.g. ':source-base-dir: /dev/my-project/src'.");
        }
        this.sourceBaseDir = dynamicSourceBaseDir;
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


    public File findSourceFile() {

        File current = new File(sourceBaseDir, relativeFileName);
        if (current.exists()) {
            return current;
        }

        for (File child: sourceBaseDir.listFiles()) {
            if (child.isDirectory()) {
                return findRecursively(child, relativeFileName);
            }
        }
        return null;
    }

    private File findRecursively(File sourceBaseDir, String path) {

        for (File child: sourceBaseDir.listFiles()) {
            if (matches(child, path)) {
                return child;
            }
            if (child.isDirectory()) {
                return findRecursively(child, path);
            }
        }
        return null;
    }

    public static boolean matches(File child, String relativePath) {
        String normalizedChild = child.getAbsolutePath().replaceAll("\\\\", "/");
        String normalizedRelativePath = relativePath.replaceAll("\\\\", "/");
        return normalizedChild.endsWith(normalizedRelativePath);
    }


}
