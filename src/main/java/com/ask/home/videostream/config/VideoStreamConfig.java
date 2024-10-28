package com.ask.home.videostream.config;


import com.ask.home.videostream.adapter.ContentAdapter;
import com.ask.home.videostream.adapter.LocalFileContentAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class VideoStreamConfig {

    @Bean
    public ContentAdapter videoContentAdapter(@Value("${video.content.path}") final String videoContentRootPath) {
        log.info("video Content Path {}", videoContentRootPath);
        return new LocalFileContentAdapter(videoContentRootPath);
    }
}
