package org.asciidoctor.extensionslab.source;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.DefaultAttributes;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.spi.ExtensionRegistry;

@Name("source")
@DefaultAttributes({
//        @DefaultAttribute(key = 'columns', value = '3')
})
public class ExtensionRegistrar implements ExtensionRegistry {

    @Override
    public void register(Asciidoctor asciidoctor) {
        asciidoctor.javaExtensionRegistry().blockMacro(SourceBlockMacro.class);
    }
}
