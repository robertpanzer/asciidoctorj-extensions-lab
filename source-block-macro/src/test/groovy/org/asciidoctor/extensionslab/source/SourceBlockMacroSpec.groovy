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


    public static final String PRE = 'pre'
    public static final String NEWLINE_RE = '[\\n\\r]'
    public static final String BLANK_RE = ' '

    def 'should insert full file contents from main java sources'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = new File('src/main/java/org/asciidoctor/extensionslab/source/ExtensionRegistrar.java').text

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/fullinclude.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))
        String listingContent = document.select(PRE).text()
        then:
        listingContent.replaceAll(NEWLINE_RE, '') == javaContent.replaceAll(NEWLINE_RE, '')

    }

    def 'should filter via methods'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public void testMethod1() {
        // A test method
}'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodinclude.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
    }

    def 'should filter via tags'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public void testMethod1() {
        // A test method
}'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/taginclude.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select('pre').text().replaceAll(' ', '') == javaContent.replaceAll(' ', '')
    }

}
