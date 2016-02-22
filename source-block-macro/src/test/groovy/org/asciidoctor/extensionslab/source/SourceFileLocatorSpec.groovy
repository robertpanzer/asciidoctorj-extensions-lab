package org.asciidoctor.extensionslab.source

import groovy.util.logging.Log
import spock.lang.Specification

@Log
class SourceFileLocatorSpec extends Specification {

    def 'should find java main source from directory containing asciidoc files'() {
        given:
        File srcDir = new File('src/main/resources')
        SourceFileLocator locator = new SourceFileLocator(srcDir, "org/asciidoctor/extensionslab/source/ExtensionRegistrar.java")

        when:
        File sourceFile = locator.findSourceFile()

        then:
        sourceFile == new File('src/main/java/org/asciidoctor/extensionslab/source/ExtensionRegistrar.java')
    }


    def 'should throw exception without sourcedir'() {
        given:
        File srcDir = new File('.')

        when:
        SourceFileLocator locator = new SourceFileLocator(srcDir, "org/asciidoctor/extensionslab/source/ExtensionRegistrar.java")

        then:
        thrown(IllegalStateException)
    }

}
