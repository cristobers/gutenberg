package com;

import java.nio.file.Path;

import com.Arguments.fileOrPathOrNonExistant;

public record PathAndFileType(Path path, fileOrPathOrNonExistant fileType) { }