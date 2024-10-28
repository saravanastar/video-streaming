package com.ask.home.videostream.adapter;

import com.ask.home.videostream.model.Content;
import com.ask.home.videostream.model.ContentRequest;
import com.ask.home.videostream.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ask.home.videostream.util.FileUtil.getFilePath;

/**
 * Extract the content from local to the device(volume/drive).
 */
@Slf4j
public class LocalFileContentAdapter implements ContentAdapter {

    final private String localFilePath;

    /**
     * Constructor injection for the root content path.
     *
     * @param localFilePath String.
     */
    public LocalFileContentAdapter(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    @Override
    public Content getContent(final ContentRequest contentRequest) {
        boolean isValid = validateRequest(contentRequest);
        if (!isValid) {
            throw new RuntimeException("Not a valid content request");
        }
        try {
            byte[] content = readByBytesRange(contentRequest);
            return Content.builder()
                    .content(content)
                    .contentLength((long) content.length)
                    .rangeStart(contentRequest.getRangeStart())
                    .rangeEnd(contentRequest.getRangeEnd())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * read the bytes of the file by the range.
     *
     * @param contentRequest ContentRequest.
     * @return byte[]
     * @throws IOException ioException.
     */
    public byte[] readByBytesRange(final ContentRequest contentRequest) throws IOException {
        Path path = Paths.get(FileUtil.getFilePath(localFilePath), contentRequest.getFileName() + "." + contentRequest.getFileType());
        byte[] data = Files.readAllBytes(path);
        long end = contentRequest.getRangeEnd();
        long start = contentRequest.getRangeStart();
        byte[] result = new byte[(int) (end - start) + 1];
        System.arraycopy(data, (int) start, result, 0, (int) (end - start) + 1);
        return result;
    }

    /**
     * Content length.
     *
     * @param contentRequest ContentRequest.
     * @return Long.
     */
    @Override
    public Long getContentSize(ContentRequest contentRequest) {
        return Optional.ofNullable(contentRequest).map(_ -> Paths.get(getFilePath(localFilePath), prepareFileName(contentRequest))).map(this::sizeFromFile).orElse(0L);
    }

    @Override
    public List<Content> listAllContents() {
        try (Stream<Path> stream = Files.walk(Paths.get(getFilePath(localFilePath)), 10)) {
            return stream.filter(file -> !Files.isDirectory(file)).map(path -> Content.builder().contentName(path.getFileName().toString()).contentPath(path.getParent().toFile().toString()).build())

                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String prepareFileName(ContentRequest contentRequest) {
        return String.format("%s.%s", contentRequest.getFileName(), contentRequest.getFileType());
    }

    /**
     * Getting the size from the path.
     *
     * @param path Path.
     * @return Long.
     */
    private Long sizeFromFile(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ioException) {
            log.error("Error while getting the file size", ioException);
        }
        return 0L;
    }

    /**
     * Validate the content Request.
     *
     * @param contentRequest ContentRequest.
     * @return boolean.
     */
    private boolean validateRequest(final ContentRequest contentRequest) {
        if (contentRequest == null) {
            throw new RuntimeException("video request object is empty");
        }

        if (contentRequest.getRangeStart() > contentRequest.getRangeEnd()) {
            return false;
        }

        return contentRequest.getFileName() != null && contentRequest.getFileType() != null;
    }
}
