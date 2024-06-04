package com.rootable.libraryservice2022.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFileDir() {
        return fileDir;
    }

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    //서버 파일 저장 형식 (uuid.확장자)
    public String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename); //확장자
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    public String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}
