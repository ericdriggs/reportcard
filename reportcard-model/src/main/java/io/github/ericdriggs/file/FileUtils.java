package io.github.ericdriggs.file;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
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
            if (isJunkFile(absolutePath)) {
                continue;
            }
            fileContents.add(stringFromPath(absolutePath));
        }
        return fileContents;
    }

    public static boolean isJunkFile(String absolutePath) {
        Set<String> junkPatterns = Set.of("/._", "/.DS_Store");
        for (String pattern : junkPatterns) {
            if (absolutePath.contains(pattern)) {
                return true;
            }
        }
        return false;
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
        try {
            return Files.readString(Path.of(absolutePath));
        } catch (IOException e) {
            log.error("error while reading file: {}, e.getMessage(): {}", absolutePath, e.getMessage());
            throw e;
        }
    }

    @SneakyThrows(IOException.class)
    public static void writeFile(Path path, String contents) {
        Files.writeString(
                path,
                contents,
                StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
