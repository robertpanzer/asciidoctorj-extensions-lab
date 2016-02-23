package org.asciidoctor.extensionslab.source;

import java.io.File;

public class SourceFileLocator {

    private final File sourceBaseDir;

    private final String relativeFileName;

    public SourceFileLocator(File baseDir, String className, String type) {
        this.relativeFileName = extractPath(className, type);
        this.sourceBaseDir = baseDir;
    }

    private String extractPath(String className, String type) {
        return className.replaceAll("\\.", "/") + "." + type;
    }

    public File findSourceFile() {
        return findRecursively(sourceBaseDir, relativeFileName);
    }

    private File findRecursively(File sourceBaseDir, String path) {

        for (File child: sourceBaseDir.listFiles()) {
            if (matches(child, path)) {
                return child;
            }
            if (child.isDirectory()) {
                File f = findRecursively(child, path);
                if (f != null) {
                    return f;
                }
            }
        }
        return null;
    }

    public static boolean matches(File file, String relativePath) {
        String normalizedChild = file.getAbsolutePath().replaceAll("\\\\", "/");
        String normalizedRelativePath = relativePath.replaceAll("\\\\", "/");
        return normalizedChild.endsWith(normalizedRelativePath);
    }


}
