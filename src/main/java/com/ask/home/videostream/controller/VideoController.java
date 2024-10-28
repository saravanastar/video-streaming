package com.ask.home.videostream.controller;

import com.ask.home.videostream.model.Content;
import com.ask.home.videostream.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/stream/{fileType}/{fileName}")
    public Mono<ResponseEntity<byte[]>> streamVideo(ServerHttpResponse serverHttpResponse, @RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @PathVariable("fileType") String fileType,
                                                    @PathVariable("fileName") String fileName) {
        return Mono.just(videoService.prepareContent(fileName, fileType, httpRangeList));
    }

    @GetMapping
    public Mono<ResponseEntity<List<Content>>> listVideos() {

        return Mono.just(videoService.listContents());
    }
}
