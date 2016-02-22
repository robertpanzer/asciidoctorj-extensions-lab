package org.asciidoctor.extensionslab.source

import groovy.util.logging.Log
import org.asciidoctor.Asciidoctor
import org.asciidoctor.OptionsBuilder
import org.asciidoctor.SafeMode
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import spock.lang.Specification

@Log
class SourceBlockMacroSpec extends Specification {

    def 'should insert full file contents from main java sources'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = new File('src/main/java/org/asciidoctor/extensionslab/source/ExtensionRegistrar.java').text

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/fullinclude.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))
        String listingContent = document.select('pre').text()
        then:
        listingContent.replaceAll('\\n', '') == javaContent.replaceAll('\\n', '')

    }

}
