package com.ask.home.videostream.controller;

import com.ask.home.videostream.model.Content;
import com.ask.home.videostream.service.VideoService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/stream/{fileType}/{filePathAndName}")
    public Mono<ResponseEntity<byte[]>> streamVideoByPath(@RequestHeader(value = "Range", required = false) String httpRangeList, @PathVariable("fileType") String fileType, @PathVariable("filePathAndName") String filePathAndName) {
        return Mono.just(videoService.prepareContentByFilePath(httpRangeList, filePathAndName, fileType));
    }

    @GetMapping("/stream/object-key/{objectKey}")
    public Mono<ResponseEntity<byte[]>> streamVideoByObjectKey(@RequestHeader(value = "Range", required = false) String httpRangeList, @PathVariable("objectKey") String objectKey) {
        return Mono.just(videoService.prepareContentByObjectKey(httpRangeList, objectKey));
    }

    @GetMapping
    public Mono<ResponseEntity<List<Content>>> getAllContents() {

        return Mono.just(videoService.getAllContents());
    }
}
