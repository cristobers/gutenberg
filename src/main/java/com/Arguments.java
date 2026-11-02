package com;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

class ArgumentParseError extends Exception {
    public ArgumentParseError(String message) {
        super(message);
    }
}

/*
This is way too overengineered for what I need to use it for.
*/

public class Arguments {
    final String[] arguments;

    enum fileOrPathOrNonExistant {
        FILE,
        PATH,
        NON_EXISTANT,
    }

    public Arguments(String[] arguments) {
        this.arguments = arguments;
    }

    public HashMap<String, PathAndFileType> parse() throws ArgumentParseError {
        // We expect 6 arguments, 3 flags and 3 values.
        if (this.arguments.length != 6) {
            throw new ArgumentParseError("Incorrect number of arguments.");
        }

        // Make the HashMap<>() of flags to file locations.
        Optional<HashMap<String, String>> optionalFlags = this.createFlagAndFileLocationHashMap();
        if (optionalFlags.isEmpty()) {
            throw new ArgumentParseError("Failed to parse all of the flags given as arguments.");
        }

        Optional <HashMap<String, PathAndFileType>> optionalFlagsWithValidPaths = this.validateFileLocations(optionalFlags.get());
        if (optionalFlagsWithValidPaths.isEmpty()) {
            throw new ArgumentParseError("Either template file or markdown file doesn't exist.");
        }

        // // Let's now check what type of file the path points to,
        // // whether that be a File or a Directory.
        return optionalFlagsWithValidPaths.get();
    }

    private Optional<HashMap<String, PathAndFileType>> validateFileLocations(HashMap<String, String> flagAndFileLocations) {
        // We will be making a file for the output if it doesn't exist,
        // so we don't need to check whether it exists within this.

        fileOrPathOrNonExistant markdownPath = typeOfFileAtPath(flagAndFileLocations.get("--markdown"));
        fileOrPathOrNonExistant templatePath = typeOfFileAtPath(flagAndFileLocations.get("--template"));
        fileOrPathOrNonExistant outputPath   = typeOfFileAtPath(flagAndFileLocations.get("--output"));

        // Both markdown and template paths should be present.
        if (markdownPath == fileOrPathOrNonExistant.NON_EXISTANT || templatePath == fileOrPathOrNonExistant.NON_EXISTANT) {
            // TODO: maybe we throw an exception here.
            return Optional.empty();
        }

        // Make sure we're passing in the correct file types.
        if ( !flagAndFileLocations.get("--markdown").endsWith(".md") || !flagAndFileLocations.get("--template").endsWith(".html") ) {
            // TODO: maybe we throw an exception here.
            return Optional.empty();
        }

        // templatePath should both be file.
        if (templatePath != fileOrPathOrNonExistant.FILE) {
            // TODO: maybe we throw an exception here.
            return Optional.empty();
        }

        HashMap<String, PathAndFileType> flagsToPaths = new HashMap<String, PathAndFileType>();
        for (String flag : flagAndFileLocations.keySet()) {
            switch (flag) {
                case "--markdown":
                    flagsToPaths.put( flag, new PathAndFileType( Paths.get(flagAndFileLocations.get(flag)), markdownPath ) );
                    break;
                case "--template":
                    flagsToPaths.put( flag, new PathAndFileType( Paths.get(flagAndFileLocations.get(flag)), templatePath ) );
                    break;
                case "--output":
                    flagsToPaths.put( flag, new PathAndFileType( Paths.get(flagAndFileLocations.get(flag)), outputPath ) );
                    break;
            }
        }
        return Optional.of(flagsToPaths);
    }

    private fileOrPathOrNonExistant typeOfFileAtPath(String filePath) {
        boolean result = false;
        try {
            result = Files.exists( Paths.get(filePath) );
        } catch (InvalidPathException ex) {
            System.err.println(ex.getMessage());
            System.exit(-67);
        }

        if (result == false) {
            return fileOrPathOrNonExistant.NON_EXISTANT;
        } else {
            boolean isDirectory = Files.isDirectory( Paths.get(filePath) );
            if (isDirectory) {
                return fileOrPathOrNonExistant.PATH;
            } else {
                return fileOrPathOrNonExistant.FILE;
            }
        }
    }

    private Optional<HashMap<String, String>> createFlagAndFileLocationHashMap() {
        HashMap<String, String> argumentMap = new HashMap<>();

        int argumentIndex = 0;
        while (argumentIndex < this.arguments.length) {
            String currentStringEntry = this.arguments[argumentIndex];
            if (isTemplateFlag(currentStringEntry)) {
                // Increment argumentIndex prior to getting templatePath, 
                // so that we go past the "--template" String.
                String templatePath = this.arguments[++argumentIndex];
                argumentMap.put("--template", templatePath);
                // Increment again so that we head past the path and
                // end up on (what should be) the next flag to parse.
                argumentIndex++;
            } else if (isMarkdownFlag(currentStringEntry)) {
                String markdownPath = this.arguments[++argumentIndex];
                argumentMap.put("--markdown", markdownPath);
                argumentIndex++;
            } else if (isOutputFlag(currentStringEntry)) {
                String outputPath = this.arguments[++argumentIndex];
                argumentMap.put("--output", outputPath);
                argumentIndex++;
            } else {
                System.err.println("Unknown flag: " + currentStringEntry);
                return Optional.empty();
            }
        }

        return Optional.of(argumentMap);
    }

    private boolean isOutputFlag(String potentialFlag) {
        return potentialFlag.equalsIgnoreCase("--out")    ||
               potentialFlag.equalsIgnoreCase("--output") ||
               potentialFlag.equalsIgnoreCase("--o");
    }

    private boolean isMarkdownFlag(String potentialFlag) {
        return potentialFlag.equalsIgnoreCase("--markdown") ||
               potentialFlag.equalsIgnoreCase("--md");
    }

    private boolean isTemplateFlag(String potentialFlag) {
        return potentialFlag.equalsIgnoreCase("--template");
    }

    public void print(HashMap<String, PathAndFileType> arguments) {
        System.out.println("Flags and paths");
        Iterator<Entry<String,PathAndFileType>> it = arguments.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, PathAndFileType> pair = (Map.Entry<String, PathAndFileType>)it.next();
            System.out.println(pair.getKey() + " : " + pair.getValue());
        }
    }
}
