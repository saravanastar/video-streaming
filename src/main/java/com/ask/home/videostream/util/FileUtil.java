package com.ask.home.videostream.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.ask.home.videostream.adapter.LocalFileContentAdapter.FILE_PATH_FORMAT;
import static com.ask.home.videostream.constants.ApplicationConstants.VIDEO;
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Get the filePath.
     *
     * @return String.
     */
    public static String getFilePath() {
        URL url = FileUtil.class.getResource(VIDEO);
        assert url != null;
        return new File(url.getFile()).getAbsolutePath();
    }

    public static String getFilePath(final String basePath, String filePath, String fileName) {
        String path;
        if (filePath != null && filePath.trim().isEmpty()) {
            path = String.format(FILE_PATH_FORMAT, basePath, fileName);
        } else {
            path = String.format(FILE_PATH_FORMAT,String.format(FILE_PATH_FORMAT, basePath, filePath), fileName);
        }
       return new File(path).getAbsolutePath();
    }

    /**
     * Check the file is video.
     * @param path Path
     * @return boolean
     */
    public static boolean isVideoFile(Path path) {
        try {
            String contentType = Files.probeContentType(path);
            return contentType != null && contentType.startsWith("video/");
        } catch (IOException ioException) {
            log.error("Exception in when checking the file is video file {}", ioException.getMessage());
            return false;
        }
    }
}
