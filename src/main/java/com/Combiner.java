package com;

public class Combiner {
    /*
    Take in the Markdown String and the Template String,
    combine them together, and output that as a String.
    */

    public static String combineTemplateAndMarkdown( String template, String markdown ) {
        return template.replaceAll("\\{DOCUMENT_BODY\\}", markdown);
    }
}
