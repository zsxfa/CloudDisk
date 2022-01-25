package com.zsxfa.cloud.core.config.fileConf.operation.write;

import com.zsxfa.cloud.core.config.fileConf.operation.write.domain.WriteFile;

import java.io.InputStream;

public abstract class Writer {
    public abstract void write(InputStream inputStream, WriteFile writeFile);
}
