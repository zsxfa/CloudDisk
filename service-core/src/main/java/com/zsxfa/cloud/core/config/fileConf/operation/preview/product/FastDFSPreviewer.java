package com.zsxfa.cloud.core.config.fileConf.operation.preview.product;

import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.zsxfa.cloud.core.config.fileConf.domain.ThumbImage;
import com.zsxfa.cloud.core.config.fileConf.operation.preview.Previewer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
//@Component
public class FastDFSPreviewer extends Previewer {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    public FastDFSPreviewer(){}

    public FastDFSPreviewer(ThumbImage thumbImage) {

        setThumbImage(thumbImage);
    }

    @Override
    public InputStream getInputStream(String fileUrl) {
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        group = "group1";
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = fastFileStorageClient.downloadFile(group, path, downloadByteArray);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        return inputStream;
    }


}
