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

    public static final String LISTINGBLOCK = '.listingblock'

    public static final String TITLE = '.title'

    public static final String CODE = 'code'
    public static final String JAVA = 'java'

    public static final String NEWLINE_RE = '[\\n\\r]'
    public static final String BLANK_RE = ' '
    public static final String CODERAY = 'CodeRay'
    public static final String HIGHLIGHT = 'highlight'
    public static final String DATA_LANG = 'data-lang'

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

        String javaContent = '''public void testMethod2() {
        // A test method
}'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodinclude.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
        document.select(LISTINGBLOCK).select(TITLE).text() == 'A very interesting method'
        document.select(PRE).hasClass(CODERAY)
        document.select(PRE).hasClass(HIGHLIGHT)
        document.select(PRE).select(CODE).attr(DATA_LANG) == JAVA
    }

    def 'should throw an exception if method is not found'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        when:
        asciidoctor.convertFile(
                new File('src/test/resources/unresolvedmethodinclude.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false))

        then:
        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.cause.message.contains "Method 'testMethod1' not found!"
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
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
    }

}
