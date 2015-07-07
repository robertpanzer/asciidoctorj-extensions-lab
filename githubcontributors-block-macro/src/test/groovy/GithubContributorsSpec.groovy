package org.asciidoctor.extensionslab.githubcontributors

import groovy.util.logging.Log
import org.asciidoctor.Asciidoctor
import org.asciidoctor.OptionsBuilder
import org.asciidoctor.SafeMode
import spock.lang.Specification

@Log
class GithubContributorsSpec extends Specification {

    def 'should create table of contributors'() {

        given:
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()

        when:
        log.info asciidoctor.convertFile(
                new File('src/test/resources/asciidoctorj.adoc'),
                OptionsBuilder.options().headerFooter(true).safe(SafeMode.UNSAFE))

        then:
        noExceptionThrown()

    }

}
