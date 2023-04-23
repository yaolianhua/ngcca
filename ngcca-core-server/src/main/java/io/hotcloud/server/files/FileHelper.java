package io.hotcloud.server.files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.Assert;

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

    public static String getUserHome() {
        return FileUtils.getUserDirectoryPath();
    }

    public static String getFilename(String file){
        return FilenameUtils.getBaseName(file);
    }

    public static boolean exists(String path) {
        Assert.hasText(path, "path is null");
        return Files.exists(Path.of(path));
    }

    public static boolean exists(Path path) {
        Assert.notNull(path, "path is null");
        return Files.exists(path);
    }

    public static void createDirectories(Path path) throws IOException {
        Assert.notNull(path, "path is null");
        Files.createDirectories(path);
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
