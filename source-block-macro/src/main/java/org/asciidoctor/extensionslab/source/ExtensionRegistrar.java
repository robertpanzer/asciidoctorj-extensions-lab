package org.asciidoctor.extensionslab.source;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.spi.ExtensionRegistry;

public class ExtensionRegistrar implements ExtensionRegistry {

    @Override
    public void register(Asciidoctor asciidoctor) {
        asciidoctor.javaExtensionRegistry().blockMacro(new JavaSourceBlockMacro());
    }
}
