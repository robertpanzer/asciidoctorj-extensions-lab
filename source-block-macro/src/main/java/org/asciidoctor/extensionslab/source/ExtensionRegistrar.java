package org.asciidoctor.extensionslab.source;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.ast.AbstractBlock;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.spi.ExtensionRegistry;

import java.util.Map;

public class ExtensionRegistrar implements ExtensionRegistry {

    @Override
    public void register(Asciidoctor asciidoctor) {
        asciidoctor.javaExtensionRegistry().blockMacro(new SourceBlockMacro());
    }
}
