package com;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class Markdown {
    /// Given some markdown in String format, return it as HTML.
    public static String toHTML(Path markdownPath) throws IOException {
        try {
            String markdownFile = readMarkdown(markdownPath);
            return toHTML(markdownFile);
        } catch (IOException e) {
            // Pass it back up to Main.
            throw e;
        }
    }

    public static String toHTML(String markdown) {
        MutableDataSet options = new MutableDataSet();

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node document = parser.parse(markdown);

        return renderer.render(document);
    }

    /// Given a Path to a markdown file, read it, and return its contents as a String.
    private static String readMarkdown(Path markdownPath) throws IOException {
        return Files.readString(markdownPath);
    }
}
