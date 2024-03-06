package io.github.ericdriggs.reportcard.util.tar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnTarGzTest {

    @TempDir
    Path tempDir;

    @Test
    void unTarTest() throws IOException {
        Path tarGzPath = Path.of("src/test/resources/util/tar/abc.tar.gz");
        Set<String> pathsToMatch = new TreeSet<>(
        Set.of(
                "a/b/c/c.txt",
                "a/b/b.txt",
                "a.txt"
        ));
        try (InputStream inputStream = Files.newInputStream(tarGzPath)){
            TarExtractorCommonsCompress  extractor =  new TarExtractorCommonsCompress(inputStream, true, tempDir);
            extractor.untar();
        }

        try (Stream<Path> stream = Files.walk(tempDir)) {
            stream.filter(Files::isRegularFile)
                  .forEach(x -> { System.out.println(x);
                      String matchedPath = matchesPath(pathsToMatch, x.toString());
                      pathsToMatch.remove(matchedPath);
                  });
        }
        assertEquals(0, pathsToMatch.size());

    }

    protected String matchesPath(Set<String> paths, String absolutePath) {
        for (String path : paths) {
            if (absolutePath.contains(path)) {
                return path;
            }
        }
        throw new IllegalStateException("unable to find match for path: " + absolutePath);
    }

}
