package com.ask.home.videostream.adapter;

import com.ask.home.videostream.model.Content;
import com.ask.home.videostream.model.ContentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LocalFileContentAdapterTest {

    LocalFileContentAdapter localFileContentAdapter;

    @BeforeEach
    public void setup() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("video").getFile());
        localFileContentAdapter = new LocalFileContentAdapter(file.getAbsolutePath());
    }

    @Test
    void findFileByKeyWithNullKey() {
        assertThrows(RuntimeException.class, () -> localFileContentAdapter.findFileByKey(null));
    }

    @Test
    void findFileByKeyWithInvalidKey() {
        Content responseContent = localFileContentAdapter.findFileByKey("test");
        assertNull(responseContent);
    }

    @Test
    void findFileByKeyWithValidKey() {
        Map<String, Content> cache = new HashMap<>();
        String fileKey = "957e9073-9aec-3be2-a94e-268312e13bed";
        Content content = Content.builder().contentName("test").objectKey(fileKey).build();
        cache.put(fileKey, content);
        ReflectionTestUtils.setField(localFileContentAdapter, "localFileMap", cache);
        Content responseContent = localFileContentAdapter.findFileByKey(fileKey);

        assertNotNull(responseContent);
        assertNotNull(responseContent.getContentName());
        assertNotNull(responseContent.getObjectKey());
        assertEquals(fileKey, responseContent.getObjectKey());
    }

    @Test
    void getContentWithEmptyData() {
        ContentRequest contentRequest = ContentRequest.builder().fileName("video_empty.mp4").fileType("mp4").filePath("").build();

        Content content = localFileContentAdapter.getContent(contentRequest);
        assertNotNull(content);
        assertEquals(0, content.getContent().length);
    }

    @Test
    void getContentWithRealVideoFile() {
        ContentRequest contentRequest = ContentRequest.builder().fileName("toystory.mp4").fileType("mp4").filePath("").build();

        Content content = localFileContentAdapter.getContent(contentRequest);
        assertNotNull(content);
        assertTrue(content.getContent().length > 0);
    }

    @Test
    void getContentWithNullObject() {
        assertThrows(RuntimeException.class, () -> localFileContentAdapter.getContent(null));
    }

    @Test
    void getContentSizeWithValidFile() {
        ContentRequest contentRequest = ContentRequest.builder().fileName("toystory.mp4").fileType("mp4").filePath("").build();

        long contentSize = localFileContentAdapter.getContentSize(contentRequest);

        assertEquals(33505479, contentSize);
    }

    @Test
    void findAllContents() {
        List<Content> contentList = localFileContentAdapter.findAllContents();
        assertNotNull(contentList);
        assertFalse(contentList.isEmpty());
    }
}