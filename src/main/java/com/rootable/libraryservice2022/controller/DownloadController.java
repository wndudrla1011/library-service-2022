package com.rootable.libraryservice2022.controller;

import com.rootable.libraryservice2022.service.FileService;
import com.rootable.libraryservice2022.dto.FileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DownloadController {

    private final FileService fileService;

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("fileId") Long fileId) throws IOException {

        log.info("다운로드가 진행됩니다.");

        FileDto fileDto = fileService.getFile(fileId); //FileDto 생성
        Path path = Paths.get(fileDto.getFilePath()); //경로 접근
        Resource resource = new InputStreamResource(Files.newInputStream(path)); //해당 경로에 파일 생성
        //생성할 파일 관련 정보 설정
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                        fileDto.getOriginFilename() + "\"")
                .body(resource);

    }

}
