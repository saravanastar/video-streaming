package com.ask.home.videostream.adapter;

import com.ask.home.videostream.model.Content;
import com.ask.home.videostream.model.ContentRequest;
import com.ask.home.videostream.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ask.home.videostream.util.FileUtil.getFilePath;

/**
 * Extract the content from local to the device(volume/drive).
 */
@Slf4j
public class LocalFileContentAdapter implements ContentAdapter {

    final public static String FILE_PATH_FORMAT = "%s/%s";
    final private Map<String, Content> localFileMap;
    final private String localFilePath;

    /**
     * Constructor injection for the root content path.
     *
     * @param localFilePath String.
     */
    public LocalFileContentAdapter(String localFilePath) {
        this.localFilePath = localFilePath;
        localFileMap = new HashMap<>();
    }

    /**
     * find Object By key.
     *
     * @param fileKey String
     * @return Content.
     */
    public Content findFileByKey(final String fileKey) {
        if (fileKey == null) {
            throw new RuntimeException("FileKey can't be null");
        }
        if (localFileMap.isEmpty()) {
            findAllContents();
        }
        return localFileMap.get(fileKey);
    }


    @Override
    public Content getContent(final ContentRequest contentRequest) {
        boolean isValid = validateRequest(contentRequest);
        if (!isValid) {
            throw new RuntimeException("Not a valid content request");
        }
        try {
            byte[] content = readByBytesRange(contentRequest);
            return Content.builder().content(content).contentLength((long) content.length).rangeStart(contentRequest.getRangeStart()).rangeEnd(contentRequest.getRangeEnd()).build();
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
    private byte[] readByBytesRange(final ContentRequest contentRequest) throws IOException {
        Path path = Paths.get(getFilePath(localFilePath, contentRequest.getFilePath(), contentRequest.getFileName()));
        byte[] data = Files.readAllBytes(path);
        if (data.length == 0) {
            return data;
        }
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
        return Optional.ofNullable(contentRequest).map(_ -> Paths.get(getFilePath(localFilePath, contentRequest.getFilePath(), contentRequest.getFileName()))).map(this::sizeFromFile).orElse(0L);
    }

    @Override
    public List<Content> findAllContents() {
        Path path = Paths.get(new File(localFilePath).getAbsolutePath());
        try (Stream<Path> stream = Files.walk(path, 10)) {
            return stream.filter(file -> !Files.isDirectory(file)).map(this::prepareContent).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * prepareContent.
     *
     * @param path Path
     * @return Content
     */
    private Content prepareContent(final Path path) {
        if (FileUtil.isVideoFile(path)) {
            final String fileName = path.getFileName().toString();
            String extension = "";
            int index = fileName.lastIndexOf('.');
            if (index > 0) {
                extension = fileName.substring(index + 1);
            }
            BasicFileAttributes basicFileAttributes = getFileAttribute(path);

            // prepare content path - remove root file path
            String contentPath = path.getParent().toFile().toString();
            int localFilePathIndex = contentPath.indexOf(localFilePath);
            if (localFilePathIndex > -1) {
                contentPath = contentPath.substring(localFilePathIndex);
                contentPath = contentPath.replace(localFilePath, "");
            }
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encodedByte = encoder.encode(basicFileAttributes.fileKey().toString().getBytes());
            UUID uuid = UUID.nameUUIDFromBytes(encodedByte);

            Content content = Content.builder().contentName(fileName).objectKey(uuid.toString()).contentPath(contentPath).contentType(extension).totalContentSize(basicFileAttributes.size()).build();
            localFileMap.put(uuid.toString(), content);
            return content;
        }
        return null;
    }

    /**
     * Read basic file Attributes
     *
     * @param path Path
     * @return BasicFileAttributes.
     */
    private BasicFileAttributes getFileAttribute(final Path path) {
        try {
            return Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
