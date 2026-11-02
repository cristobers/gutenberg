package com;

import java.util.ArrayList;
import java.util.List;

public class Combiner {
    /*
    Take in the Markdown file as a String and the Template file as a String,
    combine them together, and output that as a String.
    */

    public static String combineTemplateAndMarkdown( String template, String markdown ) {
        // TODO: We need something in here that counts the amount of whitespaces needed to make 
        //       this sit nicely in the outputted HTML.

        String documentBodyLine = findDocumentBodyTag(template);
        if (documentBodyLine == null) {
            System.err.println("Failed to find the {DOCUMENT_BODY} tag.");
            return null;
        }
        int whitespaceCount = countWhitespaces(documentBodyLine);
        if (whitespaceCount == -1) {
            System.err.println("Something went wrong.");
            return null;
        }
        String newMarkdown = modifyWhitespace(markdown, whitespaceCount);
        return template.replaceAll("\\{DOCUMENT_BODY\\}", newMarkdown);
    }

    private static String modifyWhitespace(String markdown, int amount) {
        List<String> output = new ArrayList<>();
        String[] splitMarkdown = markdown.split("\n");
        for (String markdownLine : splitMarkdown) {
            output.add(markdownLine);
        }
        return String.join( appendWhitespaces(amount-1, "\n") , output);
    }  

    private static String appendWhitespaces(int amount, String line) {
        String temp = new String();
        temp += line;
        for (int i = 0; i < amount; i++) {
            temp += ' ';
        }
        return temp;
    }

    /// Return the line that contains the document body string.
    private static String findDocumentBodyTag(String template) {
        String[] lines = template.split("\n");
        for (String line : lines) {
            if (line.contains("{DOCUMENT_BODY}")) {
                return line;
            }
        }
        return null;
    }

    /// Count the amount of whitespaces that appear before we reach a non whitespace character.
    /// Returns -1 on error.
    private static int countWhitespaces(String line) {
        int count = 0;
        while (line.charAt(count++) == ' ') {
            if (count >= line.length()) {
                return -1;
            }
        }
        return count;
    }
}
