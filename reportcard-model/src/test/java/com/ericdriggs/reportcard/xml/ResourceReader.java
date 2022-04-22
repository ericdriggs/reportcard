package com.ericdriggs.reportcard.xml;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ResourceReader {

    public static String resourceAsString(String filePath) {
        InputStream inputStream = getFileAsIOStream(filePath);
        return getInputStreamAsString(inputStream);
    }

    private static String getInputStreamAsString(InputStream inputStream) {
        return new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private static InputStream getFileAsIOStream(final String fileName) {
        InputStream ioStream = ResourceReader.class
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }
}