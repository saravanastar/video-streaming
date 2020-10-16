package com.ask.home.videostream.constants;

public class ApplicationConstants {
    public static final String VIDEO = "/video";

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String VIDEO_CONTENT = "video/";
    public static final String CONTENT_RANGE = "Content-Range";
    public static final String ACCEPT_RANGES = "Accept-Ranges";
    public static final String BYTES = "bytes";
    public static final int BYTE_RANGE = 1024;

    public static final int RESPONSE_MAX_RANGE = 1048576; // 1MB (max size of a chunck sent to client)
    
    private ApplicationConstants() {
    }
}
