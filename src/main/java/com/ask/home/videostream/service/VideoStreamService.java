package com.ask.home.videostream.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static com.ask.home.videostream.constants.ApplicationConstants.VIDEO_PATH;

@Service
public class VideoStreamService {

    /**
     *
     * @param fileName
     * @param fileType
     * @param serverHttpResponse
     * @param range
     * @return
     */
    public ResponseEntity<byte[]> prepareContent(String fileName, String fileType, ServerHttpResponse serverHttpResponse, String range) {
        long rangeStart = 0;
        long rangeEnd = 1024;
        byte[] data = null;
        Long fileSize = 0L;
        String fullFileName = fileName + "." + fileType;
        try {
            fileSize = getFileSize(fullFileName);
            if (range == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .header("Content-Type", "video/" + fileType)
                        .header("Content-Length", String.valueOf(fileSize - 1))
                        .body(readByteRange(fullFileName, rangeStart, fileSize)); // Read the object and convert it as bytes
            }
            String[] ranges = range.split("-");
            rangeStart = Long.parseLong(ranges[0].substring(6));
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = fileSize;
            }
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize;
            }
            data = readByteRange(fullFileName, rangeStart, rangeEnd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header("Content-Type", "video/" + fileType)
                .header("Accept-Ranges", "bytes")
                .header("Content-Length", contentLength)
                .header("Content-Range", "bytes " + rangeStart + "-" + (rangeEnd - 1) + "/" + fileSize)
                .body(data);


    }

    /**
     * @param filename
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    public byte[] readByteRange(String filename, long start, long end) throws IOException {
        FileInputStream inputStream = new FileInputStream(VIDEO_PATH + filename);
        ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            bufferedOutputStream.write(data, 0, nRead);
        }
        bufferedOutputStream.flush();
        byte[] result = new byte[(int) (end - start)];
        System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, (int) (end - start));
        return result;
    }

    /**
     *
     * @param fileName
     * @return
     */
    public Long getFileSize(String fileName) {
        return Optional.ofNullable(fileName)
                .map(file -> VIDEO_PATH + file)
                .map(File::new)
                .map(File::length)
                .orElse(0L);
    }
}
