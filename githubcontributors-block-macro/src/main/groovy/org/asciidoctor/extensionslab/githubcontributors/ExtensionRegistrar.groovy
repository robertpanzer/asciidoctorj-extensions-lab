package org.asciidoctor.extensionslab.githubcontributors

import org.asciidoctor.Asciidoctor
import org.asciidoctor.extension.spi.ExtensionRegistry

class ExtensionRegistrar implements ExtensionRegistry {

    @Override
    void register(Asciidoctor asciidoctor) {
        asciidoctor.javaExtensionRegistry().blockMacro(GithubContributorsBlockMacro)
    }
}
