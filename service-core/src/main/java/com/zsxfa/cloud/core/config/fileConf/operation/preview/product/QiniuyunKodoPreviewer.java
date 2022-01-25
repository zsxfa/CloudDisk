package com.zsxfa.cloud.core.config.fileConf.operation.preview.product;

import com.qiniu.util.Auth;
import com.qiwenshare.common.util.HttpsUtils;
import com.zsxfa.cloud.core.config.fileConf.config.QiniuyunConfig;
import com.zsxfa.cloud.core.config.fileConf.domain.ThumbImage;
import com.zsxfa.cloud.core.config.fileConf.operation.preview.Previewer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Getter
@Setter
@Slf4j
public class QiniuyunKodoPreviewer extends Previewer {


    private QiniuyunConfig qiniuyunConfig;

    public QiniuyunKodoPreviewer(){

    }

    public QiniuyunKodoPreviewer(QiniuyunConfig qiniuyunConfig, ThumbImage thumbImage) {
        this.qiniuyunConfig = qiniuyunConfig;
        setThumbImage(thumbImage);
    }


    @Override
    public InputStream getInputStream(String fileUrl) {

        String encodedFileName = null;
        try {
            encodedFileName = URLEncoder.encode(fileUrl, "utf-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String publicUrl = String.format("%s/%s", qiniuyunConfig.getKodo().getDomain(), encodedFileName);
        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());
        long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
        String urlString = auth.privateDownloadUrl(publicUrl, expireInSeconds);

        return HttpsUtils.doGet(urlString);
    }


}
