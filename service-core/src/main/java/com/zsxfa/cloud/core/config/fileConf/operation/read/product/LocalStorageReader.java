package com.zsxfa.cloud.core.config.fileConf.operation.read.product;

import com.zsxfa.cloud.core.config.fileConf.exception.ReadException;
import com.zsxfa.cloud.core.config.fileConf.operation.read.Reader;
import com.zsxfa.cloud.core.config.fileConf.operation.read.domain.ReadFile;
import com.zsxfa.cloud.core.config.fileConf.util.ReadFileUtils;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;

import java.io.IOException;

public class LocalStorageReader extends Reader {
    @Override
    public String read(ReadFile readFile) {

        String fileContent;
        try {
            fileContent = ReadFileUtils.getContentByPath(UFOPUtils.getStaticPath() + readFile.getFileUrl());
        } catch (IOException e) {
            throw new ReadException("文件读取出现异常", e);
        }
        return fileContent;
    }
}
