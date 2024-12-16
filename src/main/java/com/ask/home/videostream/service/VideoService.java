package com.ask.home.videostream.service;

import com.ask.home.videostream.adapter.ContentAdapter;
import com.ask.home.videostream.model.Content;
import com.ask.home.videostream.model.ContentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ask.home.videostream.constants.ApplicationConstants.*;


/**
 * Video Service that process the incoming request and extract the data out.
 */
@Service
@Slf4j
public class VideoService {

    private static final String CONTENT_RANGE_FORMAT = "%s %s-%s/%s";
    private final ContentAdapter videoContentAdapter;

    public VideoService(final ContentAdapter videoContentAdapter) {
        this.videoContentAdapter = videoContentAdapter;
    }

    /**
     * Method to get the video data by the Object Key.
     *
     * @param range     Range of the content size.
     * @param objectKey Object Key
     * @return byte array of video with ResponseEntity.
     */
    public ResponseEntity<byte[]> prepareContentByObjectKey(final String range, final String objectKey) {
        final Content content = videoContentAdapter.findFileByKey(objectKey);
        if (content == null) {
            return ResponseEntity.notFound().build();
        }
        final ContentRequest contentRequest = ContentRequest.builder().fileName(content.getContentName()).fileType(content.getContentType()).filePath(content.getContentPath()).build();
        return prepareContent(range, contentRequest);
    }

    /**
     * Get the Content by the path
     *
     * @param range           Range of the content size.
     * @param filePathAndName relative path of the file and file name
     * @param fileType        File Type
     * @return byte array of video with ResponseEntity.
     */
    public ResponseEntity<byte[]> prepareContentByFilePath(final String range, final String filePathAndName, final String fileType) {
        final String[] filePathAndNameSplit = filePathAndName.split("\\+");
        final String fileName = filePathAndNameSplit[filePathAndNameSplit.length - 1];
        final String filePath = Arrays.stream(filePathAndNameSplit).limit(filePathAndNameSplit.length - 1).collect(Collectors.joining("/"));
        final String fileNameAndType = String.format("%s.%s", fileName, fileType);

        final ContentRequest contentRequest = ContentRequest.builder().fileName(fileNameAndType).fileType(fileType).filePath(filePath).build();
        return prepareContent(range, contentRequest);
    }

    /**
     * Get the content based on the request Object(ContentRequest)
     *
     * @param range          Range of the content size.
     * @param contentRequest Content Data.
     * @return byte array of video with ResponseEntity.
     */
    private ResponseEntity<byte[]> prepareContent(final String range, final ContentRequest contentRequest) {

        try {

            final Long fileSize = videoContentAdapter.getContentSize(contentRequest);
            if (fileSize < 1) {
                throw new RuntimeException("Not a valid file size");
            }

            prepareContentRange(range, contentRequest);

            final Content content = videoContentAdapter.getContent(contentRequest);
            content.setContentType(contentRequest.getFileType());
            content.setTotalContentSize(fileSize);
            return prepareResponseEntity(content);
        } catch (Exception exception) {
            log.error("Exception while reading the file {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Prepare the response Entity
     *
     * @param content Content
     * @return ResponseEntity
     */
    private ResponseEntity<byte[]> prepareResponseEntity(final Content content) {
        HttpStatus httpStatus = HttpStatus.PARTIAL_CONTENT;
        if (content.getRangeEnd() != null && content.getRangeEnd() >= content.getTotalContentSize()) {
            httpStatus = HttpStatus.OK;
        }

        return ResponseEntity.status(httpStatus).header(CONTENT_TYPE, VIDEO_CONTENT + content.getContentType()).header(ACCEPT_RANGES, BYTES).header(CONTENT_LENGTH, String.valueOf(content.getContentLength())).header(CONTENT_RANGE, String.format(CONTENT_RANGE_FORMAT, BYTES, content.getRangeStart(), content.getRangeEnd(), content.getTotalContentSize())).body(content.getContent());
    }

    /**
     * Prepare the request
     *
     * @param range          String.
     * @param contentRequest ContentRequest.
     */
    private void prepareContentRange(final String range, final ContentRequest contentRequest) {
        // if range doesn't present default to chunk size.
        if (range == null) {
            contentRequest.setRangeStart(0L);
            contentRequest.setRangeEnd(CHUNK_SIZE);
        } else {
            //format Range: bytes=0-499
            String[] ranges = range.split("-");
            long rangeStart = Long.parseLong(ranges[0].substring(6));
            // default rangeEnd with chunk size
            long rangeEnd = rangeStart + CHUNK_SIZE;

            // if range end present in the request then pick from there
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            }

            // Get the minimum of file size or rangeEnd.
            rangeEnd = Math.min(rangeEnd, videoContentAdapter.getContentSize(contentRequest) - 1);
            contentRequest.setRangeStart(rangeStart);
            contentRequest.setRangeEnd(rangeEnd);
        }
    }

    /**
     * List Contents
     *
     * @return ResponseEntity<List < Content>>
     */
    public ResponseEntity<List<Content>> getAllContents() {
        List<Content> contentList = videoContentAdapter.findAllContents();
        if (contentList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(contentList);
    }
}