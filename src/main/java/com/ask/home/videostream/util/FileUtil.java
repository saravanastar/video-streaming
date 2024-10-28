package com.ask.home.videostream.util;

import java.io.File;
import java.net.URL;

import static com.ask.home.videostream.constants.ApplicationConstants.VIDEO;
public class FileUtil {

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

    public static String getFilePath(final String filePath) {
       return new File(filePath).getAbsolutePath();
    }
}
