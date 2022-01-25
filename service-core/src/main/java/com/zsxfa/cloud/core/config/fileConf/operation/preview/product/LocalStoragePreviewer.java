package com.zsxfa.cloud.core.config.fileConf.operation.preview.product;

import com.zsxfa.cloud.core.config.fileConf.domain.ThumbImage;
import com.zsxfa.cloud.core.config.fileConf.operation.preview.Previewer;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;
import com.zsxfa.cloud.core.service.impl.HDFSServiceImpl;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;

public class LocalStoragePreviewer extends Previewer {

    FileSystem fileSystem = HDFSServiceImpl.getFileSystem();

    public LocalStoragePreviewer(){

    }
    public LocalStoragePreviewer(ThumbImage thumbImage) {
        setThumbImage(thumbImage);
    }

    @Override
    public InputStream getInputStream(String fileUrl) {
        //设置文件路径
//        File file = UFOPUtils.getLocalSaveFile(fileUrl);
        Path path = new Path("/" + fileUrl);
        System.out.println("27获取流的文件路径是："+path);
        FSDataInputStream fsDataInputStream = null;
        try {
            fsDataInputStream = fileSystem.open(path);
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return fsDataInputStream;

    }
}
