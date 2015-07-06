package org.asciidoctor.extensionslab.githubcontributors

import groovy.json.JsonSlurper

import org.asciidoctor.ast.AbstractBlock
import org.asciidoctor.ast.Cell
import org.asciidoctor.ast.Column
import org.asciidoctor.ast.DocumentRuby
import org.asciidoctor.ast.Inline
import org.asciidoctor.ast.Row
import org.asciidoctor.ast.Table
import org.asciidoctor.extension.BlockMacroProcessor
import org.asciidoctor.extension.DefaultAttribute
import org.asciidoctor.extension.DefaultAttributes
import org.asciidoctor.extension.Name

@Name('githubcontributors')
@DefaultAttributes([
        @DefaultAttribute(key = 'columns', value = '3')
])
class GithubContributorsBlockMacro extends BlockMacroProcessor {

    @Override
    Object process(AbstractBlock parent, String target, Map<String, Object> map) {

        int numberOfColumns = map.columns as int

        URL url = new URL("https://api.github.com/repos/${target}/contributors")
        URLConnection connection = url.openConnection()
        String content = connection.inputStream.text
        List contributors = new JsonSlurper().parseText(content)

        // Create the table
        Table table = createTable(parent)
        table.grid = 'rows'
        table.title = "Github contributors of $target"

        // Create three columns
        numberOfColumns.times {
            Column column = createTableColumn(table, 0)
            column.setHorizontalAlignment(Table.HorizontalAlignment.CENTER)
            table.columns << column
        }

        contributors.collate(numberOfColumns).each { nContributors ->
            Row row = createTableRow(table)

            nContributors.eachWithIndex {
                contributor, index ->
                    DocumentRuby innerDocument = createDocument(table.document)
                    innerDocument.blocks << createBlock(innerDocument, 'image', null, [type: 'image', target: contributor.avatar_url, width: '128px'])
                    innerDocument.blocks << createBlock(innerDocument, 'paragraph', contributor.login, )
                    Cell avatarCell = createTableCell(table.columns[index], innerDocument);
                    row.cells << avatarCell
            }

            (nContributors.size() - numberOfColumns).times { it ->
                Cell avatarCell = createTableCell(table.columns[numberOfColumns - it], "");
            }
            table.body << row
        }


        table
    }
}
