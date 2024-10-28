package com.ask.home.videostream.service;

import com.ask.home.videostream.adapter.ContentAdapter;
import com.ask.home.videostream.model.Content;
import com.ask.home.videostream.model.ContentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ask.home.videostream.constants.ApplicationConstants.*;


/**
 * VideoStreamService.
 */
@Service
@Slf4j
public class VideoService {

    private final ContentAdapter videoContentAdapter;
    private static final String CONTENT_RANGE_FORMAT = "%s %s-%s/%s";

    public VideoService(final ContentAdapter videoContentAdapter) {
        this.videoContentAdapter = videoContentAdapter;
    }

    /**
     * Prepare the content.
     *
     * @param fileName String.
     * @param fileType String.
     * @param range    String.
     * @return ResponseEntity.
     */
    public ResponseEntity<byte[]> prepareContent(final String fileName, final String fileType, final String range) {

        try {
            ContentRequest contentRequest = ContentRequest.builder().fileName(fileName).fileType(fileType).build();
            final Long fileSize = videoContentAdapter.getContentSize(contentRequest);
            if (fileSize < 1) {
                throw new RuntimeException("Not a valid file size");
            }

            prepareContentRange(range, contentRequest);

            final Content content = videoContentAdapter.getContent(contentRequest);
            content.setContentType(fileType);
            content.setTotalContentSize(fileSize);
            return prepareResponseEntity(content);
        } catch (Exception exception) {
            log.error("Exception while reading the file {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Prepare the response Entity
     * @param content Content
     * @return ResponseEntity
     */
    private ResponseEntity<byte[]> prepareResponseEntity(final Content content) {
        HttpStatus httpStatus = HttpStatus.PARTIAL_CONTENT;
        if (content.getRangeEnd() != null && content.getRangeEnd() >= content.getTotalContentSize()) {
            httpStatus = HttpStatus.OK;
        }

        return ResponseEntity
                .status(httpStatus)
                .header(CONTENT_TYPE, VIDEO_CONTENT + content.getContentType())
                .header(ACCEPT_RANGES, BYTES)
                .header(CONTENT_LENGTH, String.valueOf(content.getContentLength()))
                .header(CONTENT_RANGE, String.format(CONTENT_RANGE_FORMAT, BYTES, content.getRangeStart(), content.getRangeEnd(), content.getTotalContentSize()))
                .body(content.getContent());
    }

    /**
     * Prepare the request
     * @param range String.
     * @param contentRequest ContentRequest.
     */
    private void prepareContentRange(final String range, final ContentRequest contentRequest) {
        // if range doesn't present default to chunk size.
        if (range == null) {
            contentRequest.setRangeStart(0L);
            contentRequest.setRangeEnd(CHUNK_SIZE);
        } else {
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
     * @return ResponseEntity<List<Content>>
     */
    public ResponseEntity<List<Content>> listContents() {
        List<Content> contentList = videoContentAdapter.listAllContents();
        if (contentList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(contentList);
    }
}