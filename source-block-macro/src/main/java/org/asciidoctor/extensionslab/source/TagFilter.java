package org.asciidoctor.extensionslab.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Set;
import java.util.regex.Pattern;

public class TagFilter {

    private final Pattern tagDirectiveRx;

    private final Set<String> tags;

    public TagFilter(Set<String> tags) {
        this.tags = tags;
        tagDirectiveRx = Pattern.compile("\\b(?:tag|end)::\\S+\\[\\]$/");
    }

    public String filterTags(String content) {
        StringBuilder sb = new StringBuilder(content.length());

        String activeTag = null;

        BufferedReader reader = new BufferedReader(new StringReader(content));

        try {
            String line = reader.readLine();

            while (line != null) {
                if (activeTag != null) {
                    if (line.endsWith("end::" + activeTag + "[]")) {
                        activeTag = null;
                    } else {
                        if (!tagDirectiveRx.matcher(line).matches()) {
                            sb.append(line).append("\n");
                        }
                    }
                } else {
                    activeTag = identifyTag(line);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception while filtering tagged content");
        }

        return sb.toString();
    }

    private String identifyTag(String line) {
        for (String tag: tags) {
            if (line.endsWith("tag::" + tag + "[]")) {
                return tag;
            }
        }
        return null;
    }


}
