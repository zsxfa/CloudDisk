package com.zsxfa.cloud.core.config.fileConf.operation.preview;

import com.qiwenshare.common.operation.ImageOperation;
import com.qiwenshare.common.operation.VideoOperation;
import com.zsxfa.cloud.core.config.fileConf.domain.ThumbImage;
import com.zsxfa.cloud.core.config.fileConf.operation.preview.domain.PreviewFile;
import com.zsxfa.cloud.core.config.fileConf.util.CharsetUtils;
import com.zsxfa.cloud.core.config.fileConf.util.IOUtils;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;
import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Data
public abstract class Previewer {

    private ThumbImage thumbImage;

    public void imageThumbnailPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {
        String fileUrl = previewFile.getFileUrl();
        boolean isVideo = UFOPUtils.isVideoFile(UFOPUtils.getFileExtendName(fileUrl));
        String thumbnailImgUrl = previewFile.getFileUrl();
        if (isVideo) {
            thumbnailImgUrl = fileUrl.replace("." + UFOPUtils.getFileExtendName(fileUrl), ".jpg");
        }
        String localURL = UFOPUtils.pathSplitFormat(thumbnailImgUrl);
        System.out.println("进行路径格式化后的结果是: "+localURL);
        File saveFile = UFOPUtils.getCacheFile(localURL);
        if (saveFile.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(saveFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            IOUtils.writeInputStreamToResponse(fis, httpServletResponse);

        } else {
            InputStream inputstream = getInputStream(previewFile.getFileUrl());
            InputStream in = null;
            System.out.println("43savePath是："+saveFile.getAbsolutePath());
            try {
                int width = 150;
                int height = 150;
                System.out.println("缩略图的高度和宽度是："+height + " " + width);
                if (isVideo) {
                    in = VideoOperation.thumbnailsImage(inputstream, saveFile, width, height);
                } else {
                    in = ImageOperation.thumbnailsImage(inputstream, saveFile, width, height);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            IOUtils.writeInputStreamToResponse(in, httpServletResponse);
        }
    }

    public void imageOriginalPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {

        //获取到的是hdfs上文件的输出流
        InputStream inputStream = getInputStream(previewFile.getFileUrl());

        OutputStream outputStream = null;
        try {
            outputStream = httpServletResponse.getOutputStream();
            byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(inputStream);
            bytes = CharsetUtils.convertCharset(bytes, UFOPUtils.getFileExtendName(previewFile.getFileUrl()));
            outputStream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    protected abstract InputStream getInputStream(String fileUrl);
}
