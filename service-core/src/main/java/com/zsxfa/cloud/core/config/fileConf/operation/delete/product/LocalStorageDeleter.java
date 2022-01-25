package com.zsxfa.cloud.core.config.fileConf.operation.delete.product;

import com.zsxfa.cloud.core.config.fileConf.operation.delete.Deleter;
import com.zsxfa.cloud.core.config.fileConf.operation.delete.domain.DeleteFile;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LocalStorageDeleter extends Deleter {
    @Override
    public void delete(DeleteFile deleteFile) {
        File localSaveFile = UFOPUtils.getLocalSaveFile(deleteFile.getFileUrl());
        if (localSaveFile.exists()) {
            localSaveFile.delete();
        }

        String extendName = UFOPUtils.getFileExtendName(deleteFile.getFileUrl());
        if (UFOPUtils.isImageFile(extendName)) {
            File cacheFile = UFOPUtils.getCacheFile(deleteFile.getFileUrl());
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
        }
    }
}
