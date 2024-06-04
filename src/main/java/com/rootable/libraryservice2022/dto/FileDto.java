package com.rootable.libraryservice2022.dto;

import com.rootable.libraryservice2022.domain.File;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileDto {

    private Long id;
    private String originFilename;
    private String filename;
    private String filePath;

    public File toEntity() {
        File build = File.builder()
                .id(id)
                .originFilename(originFilename)
                .filename(filename)
                .filePath(filePath)
                .build();
        return build;
    }

    @Builder
    public FileDto(Long id, String originFilename, String filename, String filePath) {
        this.id = id;
        this.originFilename = originFilename;
        this.filename = filename;
        this.filePath = filePath;
    }

}
