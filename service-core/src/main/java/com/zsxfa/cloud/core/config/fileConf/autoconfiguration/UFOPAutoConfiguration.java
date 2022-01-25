package com.zsxfa.cloud.core.config.fileConf.autoconfiguration;

import com.zsxfa.cloud.base.util.UFOPUtils;
import com.zsxfa.cloud.core.config.fileConf.factory.UFOPFactory;

import com.zsxfa.cloud.core.config.fileConf.operation.copy.product.FastDFSCopier;
import com.zsxfa.cloud.core.config.fileConf.operation.delete.product.FastDFSDeleter;
import com.zsxfa.cloud.core.config.fileConf.operation.download.product.FastDFSDownloader;
import com.zsxfa.cloud.core.config.fileConf.operation.preview.product.FastDFSPreviewer;
import com.zsxfa.cloud.core.config.fileConf.operation.read.product.FastDFSReader;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.product.AliyunOSSUploader;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.product.FastDFSUploader;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.product.MinioUploader;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.product.QiniuyunKodoUploader;
import com.zsxfa.cloud.core.config.fileConf.operation.write.product.FastDFSWriter;
import com.zsxfa.cloud.core.config.fileConf.util.RedisUtil;
import com.zsxfa.cloud.core.config.fileConf.util.concurrent.locks.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
//@ConditionalOnClass(UFOService.class)
@EnableConfigurationProperties({UFOPProperties.class})
public class UFOPAutoConfiguration {

    @Autowired
    private UFOPProperties ufopProperties;


//    @Bean
    public UFOPFactory ufopFactory() {
        UFOPUtils.LOCAL_STORAGE_PATH = ufopProperties.getLocalStoragePath();
        return new UFOPFactory(ufopProperties);
    }
////    @Bean
//    public FastDFSCopier fastDFSCreater() {
//        return new FastDFSCopier();
//    }
////    @Bean
//    public FastDFSUploader fastDFSUploader() {
//        return new FastDFSUploader();
//    }
////    @Bean
//    public FastDFSDownloader fastDFSDownloader() {
//        return new FastDFSDownloader();
//    }
////    @Bean
//    public FastDFSDeleter fastDFSDeleter() {
//        return new FastDFSDeleter();
//    }
////    @Bean
//    public FastDFSReader fastDFSReader() {
//        return new FastDFSReader();
//    }
////    @Bean
//    public FastDFSWriter fastDFSWriter() {
//        return new FastDFSWriter();
//    }
////    @Bean
//    public FastDFSPreviewer fastDFSPreviewer() {
//        return new FastDFSPreviewer(ufopProperties.getThumbImage());
//    }
////    @Bean
//    public AliyunOSSUploader aliyunOSSUploader() {
//        return new AliyunOSSUploader(ufopProperties.getAliyun());
//    }
////    @Bean
//    public MinioUploader minioUploader() {
//        return new MinioUploader(ufopProperties.getMinio());
//    }
////    @Bean
//    public QiniuyunKodoUploader qiniuyunKodoUploader() {
//        return new QiniuyunKodoUploader(ufopProperties.getQiniuyun());
//    }
//
//
    @Bean
    public RedisLock redisLock() {
        return new RedisLock();
    }
    @Bean
    public RedisUtil redisUtil() {
        return new RedisUtil();
    }

}
