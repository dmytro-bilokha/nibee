package com.dmytrobilokha.nibee.service.file;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class FileService {

    private static final Map<String, String> CONTENT_TYPE_MAP;

    static {
        Map<String, String> contentTypeMap = new HashMap<>();
        contentTypeMap.put("txt", "text/plain");
        contentTypeMap.put("pdf", "application/pdf");
        contentTypeMap.put("7z", "application/x-7z-compressed");
        contentTypeMap.put("gif", "image/gif");
        contentTypeMap.put("jp2", "image/jp2");
        contentTypeMap.put("jpg2", "image/jp2");
        contentTypeMap.put("jpg", "image/jpeg");
        contentTypeMap.put("jpe", "image/jpeg");
        contentTypeMap.put("jpeg", "image/jpeg");
        contentTypeMap.put("pcx", "image/pcx");
        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("svg", "image/svg+xml");
        contentTypeMap.put("svgz", "image/svg+xml");
        contentTypeMap.put("tif", "image/tiff");
        contentTypeMap.put("tiff", "image/tiff");
        contentTypeMap.put("gz", "application/gzip");
        contentTypeMap.put("zip", "application/zip");
        contentTypeMap.put("tgz", "application/x-gtar-compressed");
        contentTypeMap.put("tar", "application/x-tar");
        contentTypeMap.put("odp", "application/vnd.oasis.opendocument.presentation");
        contentTypeMap.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
        contentTypeMap.put("odt", "application/vnd.oasis.opendocument.text");
        CONTENT_TYPE_MAP = Collections.unmodifiableMap(contentTypeMap);
    }

    public String getFileContentType(Path resourcePath) {
        Path filePath = resourcePath.getFileName();
        if (filePath == null) {
            return "";
        }
        String fileName = filePath.toString();
        if (fileName.length() < 3) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 1 || lastDotIndex >= fileName.length()-1) {
            return "";
        }
        String fileExtension = fileName.substring(lastDotIndex + 1);
        return CONTENT_TYPE_MAP.getOrDefault(fileExtension, "");
    }

    public long getFileSize(Path filePath) throws IOException {
        return Files.size(filePath);
    }

    public boolean isFileRegularAndReadable(Path filePath) {
        return Files.isReadable(filePath) && Files.isRegularFile(filePath);
    }

    public void dumpFileToStream(Path filePath, OutputStream out) throws IOException {
        byte[] buffer = new byte[8 * 1024];
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        try (SeekableByteChannel channel = Files.newByteChannel(filePath, StandardOpenOption.READ)) {
            for (int length = 0; (length = channel.read(byteBuffer)) != -1;) {
                out.write(buffer, 0, length);
                byteBuffer.clear();
            }
        }
    }

}
