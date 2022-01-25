package com.zsxfa.cloud.core.config.fileConf.operation.download.product;

import com.zsxfa.cloud.core.config.fileConf.operation.download.Downloader;
import com.zsxfa.cloud.core.config.fileConf.operation.download.domain.DownloadFile;
import com.zsxfa.cloud.core.config.fileConf.util.IOUtils;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;
import com.zsxfa.cloud.core.service.impl.HDFSServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Slf4j
@Component
public class LocalStorageDownloader extends Downloader {
    FileSystem fileSystem = HDFSServiceImpl.getFileSystem();
    @Override
    public void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile) {
        System.out.println("24进入download方法了");

        FSDataInputStream fsDataInputStream = null;

        //设置文件路径
//        File file = new File(UFOPUtils.getStaticPath() + downloadFile.getFileUrl());
//        if (file.exists()) {
//            FileInputStream fis = null;
            try {
                fsDataInputStream = fileSystem.open(new Path("/"+downloadFile.getFileUrl()));
                System.out.println("34要下载的路径是："+"/"+downloadFile.getFileUrl());
//                fis = new FileInputStream(file);
//                IOUtils.writeInputStreamToResponse(fis, httpServletResponse);
                IOUtils.writeInputStreamToResponse(fsDataInputStream, httpServletResponse);

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    fsDataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//        }
    }

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        FSDataInputStream fsDataInputStream = null;
        //设置文件路径
//        File file = new File("/" + downloadFile.getFileUrl());
//        InputStream inputStream = null;
        try {
            fsDataInputStream = fileSystem.open(new Path("/"+downloadFile.getFileUrl()));
//            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fsDataInputStream;

    }
}
