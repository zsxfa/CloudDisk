package com.zsxfa.cloud.core.config.fileConf.operation.delete;

import com.zsxfa.cloud.core.config.fileConf.operation.delete.domain.DeleteFile;

public abstract class Deleter {
    public abstract void delete(DeleteFile deleteFile);
}
