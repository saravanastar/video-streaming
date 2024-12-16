package com.ask.home.videostream.adapter;

import com.ask.home.videostream.model.Content;
import com.ask.home.videostream.model.ContentRequest;

import java.util.List;

/**
 * ContentAdapter
 */
public interface ContentAdapter {
    Content getContent(ContentRequest contentRequest);
    Long getContentSize(ContentRequest contentRequest);
    List<Content> findAllContents();
    Content findFileByKey(final String fileKey);
}
