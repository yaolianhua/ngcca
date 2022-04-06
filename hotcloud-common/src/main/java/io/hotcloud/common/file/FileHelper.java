package io.hotcloud.common.file;

import io.hotcloud.common.Assert;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class FileHelper {

    private FileHelper() {
    }

    public static boolean exists(String path) {
        Assert.hasText(path, "path is null", 400);
        return Files.exists(Path.of(path));
    }

    public static boolean deleteRecursively(Path root) throws IOException {
        if (root == null) {
            return false;
        }
        if (!Files.exists(root)) {
            return false;
        }

        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        return true;
    }
}
