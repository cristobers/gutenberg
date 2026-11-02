package com;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.HashMap;

import com.Arguments.fileOrPathOrNonExistant;

/*
TODO:
    * A useage command would be nice.
    * Sanitize the outputted html (make it sit nicely in the file with the other HTML tags.)
        * This also means make sure the user isnt putting in <script> tags.
*/

public class Main {
    public static void main(String[] args) {

        HashMap<String, PathAndFileType> programFlags = null;
        Arguments arguments = new Arguments(args);
        try {
            programFlags = arguments.parse();
        } catch (ArgumentParseError e) {
            System.err.println(e);
            System.exit(-68);
        }

        arguments.print(programFlags);

        PathAndFileType markdown = programFlags.get("--markdown");
        PathAndFileType template = programFlags.get("--template");
        PathAndFileType output   = programFlags.get("--output");

        boolean markdownIsFile = markdown.fileType() == fileOrPathOrNonExistant.FILE;
        boolean templateIsFile = template.fileType() == fileOrPathOrNonExistant.FILE;
        if ( markdownIsFile && templateIsFile ) {
            // Apply the template to the markdown.
            try {
                String markdownFileContents = Markdown.toHTML(markdown.path());
                String templateFileContents = Files.readString(template.path());

                String combined = Combiner.combineTemplateAndMarkdown(templateFileContents, markdownFileContents);
                Writer fileWriter = new FileWriter(output.path().toString(), false);
                fileWriter.write(combined);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}