package com.ericdriggs.file;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    public static String regexForExtension (String fileNameExtension) {
        return ".*[.]" + fileNameExtension;
    }

    public static List<String> fileContentsFromRelativeDir(String relativeDir, String fileNameRegex) {
        List<String> relativePaths = findFileNamesForRelativeDir(relativeDir, fileNameRegex);
        List<String> fileContents = new ArrayList<>();
        for ( String relativePath : relativePaths) {
            fileContents.add(stringFromRelativePath(relativePath));
        }
        return fileContents;
    }

    /**
     *
     * @param relativeDir the directory to search. If file is provided, will search parent directory.
     *                    Will NOT search recursively.
     * @param fileNameRegex the regex to match filename against
     * @return a list of relative filenames for the provided directory
     */
    @SneakyThrows(IOException.class)
    public static List<String> findFileNamesForRelativeDir(String relativeDir, String fileNameRegex)  {
        Path dirPath = Path.of(Path.of("").toString(), relativeDir);

        //not recursive
        final int maxDepth = 1;

        try (Stream<Path> walk = Files.find(
                dirPath,
                maxDepth,
                (path, basicFileAttributes) -> path.toFile().getName().matches(fileNameRegex))) {

            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString())
                    .collect(Collectors.toList());
            return result;
        }
    }

    /**
     * @param relativePath a path relative to project root, e.g. src/test/resources/coverage/backtothefuture-swagger.json
     * @return the contents of the file
     * @throws RuntimeException if unable to load file
     */

    public static String stringFromRelativePath(String relativePath) {
        Path absolutePath = Path.of(Path.of("").toString(), relativePath).toAbsolutePath();
        return stringFromAbsolutePath(absolutePath);
    }

    @SneakyThrows(IOException.class)
    public static String stringFromAbsolutePath( Path absolutePath) {
        return Files.readString(absolutePath);
    }

    public static void saveRelativePath(String relativePath, String fileName, String fileContents ) {
        Path dirPath = Path.of(Path.of("").toString(), relativePath).toAbsolutePath();
        Path filePath = Path.of(dirPath.toString(), fileName);
        try {
            Files.createDirectories(dirPath);
            Files.write(filePath, fileContents.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
