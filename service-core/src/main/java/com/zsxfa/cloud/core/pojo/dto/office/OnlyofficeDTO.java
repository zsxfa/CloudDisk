package com.zsxfa.cloud.core.pojo.dto.office;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OnlyofficeDTO {
    private long fileId;

    private String fileName;

    private String fileUrl;

    private String extendName;
}
