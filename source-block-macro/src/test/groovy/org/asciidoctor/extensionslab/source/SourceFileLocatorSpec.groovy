package org.asciidoctor.extensionslab.source

import groovy.util.logging.Log
import spock.lang.Specification

@Log
class SourceFileLocatorSpec extends Specification {


    public static final String TYPE_JAVA = 'java'
    public static final String CLASS_NAME = 'org.asciidoctor.extensionslab.source.ExtensionRegistrar'

    def 'should find java main source from directory containing asciidoc files'() {
        given:
        File srcDir = new File('src/main')
        SourceFileLocator locator = new SourceFileLocator(srcDir, CLASS_NAME, TYPE_JAVA)

        when:
        File sourceFile = locator.findSourceFile()

        then:
        sourceFile == new File('src/main/java/org/asciidoctor/extensionslab/source/ExtensionRegistrar.java')
    }


    def 'should throw exception when file is not found'() {
        given:
        File srcDir = new File('src/test')
        SourceFileLocator locator = new SourceFileLocator(srcDir, CLASS_NAME, TYPE_JAVA)

        when:
        File sourceFile = locator.findSourceFile()

        then:
        sourceFile == null
    }

}
