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

    def 'should filter via methods without parameter list'() {

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

    def 'should filter via methods with no arguments'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public void testMethod3() {
        // A test method without arguments
}'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludewithnoargs.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
        document.select(LISTINGBLOCK).select(TITLE).text() == 'A method with no args'
    }

    def 'should filter via methods with one primitive argument'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public void testMethod3(int x) {
        // A test method with one int arg
}'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludewithoneprimitivearg.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
        document.select(LISTINGBLOCK).select(TITLE).text() == 'A method with one int arg'
    }

    def 'should filter via methods with one raw argument'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public void testMethod3(java.util.List list) {
        // A test method with one raw List arg
}'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludewithonerawarg.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
        document.select(LISTINGBLOCK).select(TITLE).text() == 'A method with one raw arg'
    }

    def 'should filter via methods with one parameterized argument'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public void testMethod3(java.util.Map<String, java.util.Object> list) {
        // A test method with one parameterized Map arg
    }'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludewithoneparameterizedarg.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
        document.select(LISTINGBLOCK).select(TITLE).text() == 'A method with one parameterized arg'
    }

    def 'should filter via methods with one primitive array argument'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public void testMethod3(float [ ] floats) {
        // A test method with an array of floats
    }'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludewithoneprimitivearrayarg.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
        document.select(LISTINGBLOCK).select(TITLE).text() == 'A method with one float array arg'
    }

    def 'should filter via methods with a two dim array argument of objects'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public void testMethod3(java.awt.Frame [][] frames) {
        // A test method with an 2 dim array of frames
    }'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludewithonetwodimensionalarrayarg.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
    }

    def 'should filter via methods with a generic type variable'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public <T> void testMethod3(T t) {
        // A method with a T
    }'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludewithgenericparam.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
    }

    def 'should filter via methods with multiple params'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public <T> void testMethod3(T t, boolean[] b, List<Integer> l, int i) {
        // A method with a T
    }'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludewithmultipleargs.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
    }

    def 'should filter via methods with a vararg param'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public <T> void testMethod3(int... ints) {
        // A method with a vararg
    }'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludewithvararg.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
    }

    def 'should throw an exception with multiple matches'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        when:
        Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludewithmultiplematches.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        thrown(IllegalArgumentException)
    }

    def 'should filter via methods from outer class if inner class defines same method'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public void testMethod() {
        System.out.println("The outer method");
    }'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludeinnerclassesfromouterclass.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
    }

    def 'should filter via methods from inner class if inner class defines same method'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public void testMethod() {
        System.out.println("The inner method");
    }'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludeinnerclassesfrominnerclass.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
    }

    def 'should filter via methods from inner class if inner inner class defines same method'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        String javaContent = '''public void testMethod() {
        System.out.println("The inner inner method");
    }'''

        when:
        Document document = Jsoup.parse(asciidoctor.convertFile(
                new File('src/test/resources/methodincludeinnerclassesfrominnerinnerclass.adoc'),
                OptionsBuilder.options().safe(SafeMode.UNSAFE).toFile(false)))

        then:
        document.select(PRE).text().replaceAll(BLANK_RE, '') == javaContent.replaceAll(BLANK_RE, '')
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
