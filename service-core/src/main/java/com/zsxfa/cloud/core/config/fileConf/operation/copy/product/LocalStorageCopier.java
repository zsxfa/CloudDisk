package com.zsxfa.cloud.core.config.fileConf.operation.copy.product;

import com.zsxfa.cloud.core.config.fileConf.exception.CopyException;
import com.zsxfa.cloud.core.config.fileConf.operation.copy.Copier;
import com.zsxfa.cloud.core.config.fileConf.operation.copy.domain.CopyFile;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
public class LocalStorageCopier extends Copier {
    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = UFOPUtils.getUploadFileUrl(uuid, copyFile.getExtendName());
        File saveFile = new File(UFOPUtils.getStaticPath() + fileUrl);
        try {
            FileUtils.copyInputStreamToFile(inputStream, saveFile);
        } catch (IOException e) {
            throw new CopyException("创建文件出现异常", e);
        }
        return fileUrl;
    }
}
