package com.ask.home.videostream.service;

import com.ask.home.videostream.adapter.ContentAdapter;
import com.ask.home.videostream.model.Content;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @InjectMocks
    VideoService videoService;

    @Mock
    ContentAdapter videoContentAdapter;


    @Test
    void prepareContentByObjectKeyWithValidObjectKey() {
        Content content = Content.builder().contentPath("").contentName("toystory.mp4").content(new byte[]{}).build();
        Mockito.when(videoContentAdapter.getContent(any())).thenReturn(content);
        Mockito.when(videoContentAdapter.findFileByKey(any())).thenReturn(content);
        Mockito.when(videoContentAdapter.getContentSize(any())).thenReturn(10L);

        ResponseEntity<byte[]> responseEntity = videoService.prepareContentByObjectKey("bytes=0-", "test-key");
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    void prepareContentByObjectKeyWithContentNotFound() {
        Mockito.when(videoContentAdapter.findFileByKey(any())).thenReturn(null);

        ResponseEntity<byte[]> responseEntity = videoService.prepareContentByObjectKey("bytes=0-", "test-key");
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    void prepareContentByFilePath() {
        Content content = Content.builder().contentPath("").contentName("toystory.mp4").content(new byte[]{}).build();
        Mockito.when(videoContentAdapter.getContent(any())).thenReturn(content);
        Mockito.when(videoContentAdapter.getContentSize(any())).thenReturn(10L);

        ResponseEntity<byte[]> responseEntity = videoService.prepareContentByFilePath("bytes=0-", "toystory", "mp4");
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    void prepareContentByFilePathWithoutRange() {
        Content content = Content.builder().contentPath("").contentName("toystory.mp4").content(new byte[]{}).build();
        Mockito.when(videoContentAdapter.getContent(any())).thenReturn(content);
        Mockito.when(videoContentAdapter.getContentSize(any())).thenReturn(10L);

        ResponseEntity<byte[]> responseEntity = videoService.prepareContentByFilePath(null, "toystory", "mp4");
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getAllContentsWithData() {
        Content content = Content.builder().contentPath("").contentName("toystory.mp4").content(new byte[]{}).build();
        Mockito.when(videoContentAdapter.findAllContents()).thenReturn(Collections.singletonList(content));

        ResponseEntity<List<Content>> responseEntity = videoService.getAllContents();
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getAllContentsWithNoData() {
        Mockito.when(videoContentAdapter.findAllContents()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Content>> responseEntity = videoService.getAllContents();
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals(204, responseEntity.getStatusCode().value());
    }
}