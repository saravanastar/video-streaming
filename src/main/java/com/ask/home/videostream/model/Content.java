package com.ask.home.videostream.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;


/**
 * Contains the values of extracted video content and metadata
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Content {

    private byte[] content;
    private Long rangeStart;
    private Long rangeEnd;
    private Long contentLength;
    private String contentPath;
    private String contentType;
    private String contentName;
    private Long totalContentSize;
    private String objectKey;
}
