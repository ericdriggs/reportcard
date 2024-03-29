package io.github.ericdriggs.file;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    public static String absolutePathFromRelativePath(String relativePath) {
        return Path.of(Path.of("").toString(), relativePath).toAbsolutePath().toString();
    }

    public static String regexForExtension(String fileNameExtension) {
        return ".*[.]" + fileNameExtension;
    }

    public static List<String> fileContentsFromPathAndRegex(String absolutePath, String fileNameRegex) {
        List<String> absolutePaths = filePathsForPathAndRegex(absolutePath, fileNameRegex);
        return fileContentsFromPaths(absolutePaths);
    }

    public static List<String> fileContentsFromPathAndRegex(Path absolutePath, String fileNameRegex) {
        List<String> absolutePaths = filePathsForPathAndRegex(absolutePath, fileNameRegex);
        return fileContentsFromPaths(absolutePaths);
    }


    public static List<String> fileContentsFromPaths(List<String> absolutePaths) {
        List<String> fileContents = new ArrayList<>();
        for (String absolutePath : absolutePaths) {
            fileContents.add(stringFromPath(absolutePath));
        }
        return fileContents;
    }

    public static List<String> filePathsForPathAndRegex(String absolutePath, String fileNameRegex) {
        Path dirPath = Path.of(absolutePath);
        return filePathsForPathAndRegex(dirPath, fileNameRegex);
    }

    @SneakyThrows(IOException.class)
    public static List<String> filePathsForPathAndRegex(Path dirPath, String fileNameRegex) {


        //not recursive
        final int maxDepth = 1;

        try (Stream<Path> walk = Files.find(
                dirPath,
                maxDepth,
                (path, basicFileAttributes) -> path.toFile().getName().matches(fileNameRegex))) {

            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toAbsolutePath().toString())
                    .collect(Collectors.toList());
            return result;
        }
    }

    @SneakyThrows(IOException.class)
    public static String stringFromPath(String absolutePath) {
        return Files.readString(Path.of(absolutePath));
    }

    @SneakyThrows(IOException.class)
    public static void writeFile(Path path, String contents) {
        Files.writeString(
                path,
                contents,
                StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
