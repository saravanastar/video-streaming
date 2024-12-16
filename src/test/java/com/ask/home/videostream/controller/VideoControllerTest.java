package com.ask.home.videostream.controller;

import com.ask.home.videostream.service.VideoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = VideoController.class)
@ExtendWith(SpringExtension.class)
class VideoControllerTest {

    @MockBean
    VideoService videoService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void streamVideoWithFilePathAndName() {
        when(videoService.prepareContentByFilePath(any(), any(), any())).thenReturn(ResponseEntity.ok(new byte[]{}));
        webTestClient.get().uri("/api/v1/videos/stream/mp4/toystory").exchange().expectStatus().is2xxSuccessful();

        verify(videoService, times(1)).prepareContentByFilePath(any(), any(), any());
        verify(videoService, times(0)).prepareContentByObjectKey(any(), any());
    }

    @Test
    void streamVideoWithFilePathFolderAndName() {
        when(videoService.prepareContentByFilePath(any(), any(), any())).thenReturn(ResponseEntity.ok(new byte[]{}));
        webTestClient.get().uri("/api/v1/videos/stream/mp4/video1+toystory").exchange().expectStatus().is2xxSuccessful();

        verify(videoService, times(1)).prepareContentByFilePath(any(), any(), any());
        verify(videoService, times(0)).prepareContentByObjectKey(any(), any());
    }

    @Test
    void testStreamVideoWithObjectKey() {
        when(videoService.prepareContentByObjectKey(any(), any())).thenReturn(ResponseEntity.ok(new byte[]{}));
        webTestClient.get().uri("/api/v1/videos/stream/object-key/test-key").exchange().expectStatus().is2xxSuccessful();

        verify(videoService, times(0)).prepareContentByFilePath(any(), any(), any());
        verify(videoService, times(1)).prepareContentByObjectKey(any(), any());
    }

    @Test
    void getAllContents() {
        when(videoService.getAllContents()).thenReturn(ResponseEntity.ok(Collections.emptyList()));
        webTestClient.get().uri("/api/v1/videos").exchange().expectStatus().is2xxSuccessful();

        verify(videoService, times(0)).prepareContentByFilePath(any(), any(), any());
        verify(videoService, times(0)).prepareContentByObjectKey(any(), any());
        verify(videoService, times(1)).getAllContents();
    }
}