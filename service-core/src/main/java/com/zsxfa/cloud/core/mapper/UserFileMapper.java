package com.zsxfa.cloud.core.mapper;

import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.pojo.entity.UserFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsxfa.cloud.core.pojo.vo.FileListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zsxfa
 */
public interface UserFileMapper extends BaseMapper<UserFile> {
    //通过用户返回文件列表
    List<FileListVo> userFileList(@Param("userFile") UserFile userFile, Long beginCount, Long pageCount);

    //管理员端返回文件列表
    List<FileListVo> adminFileList(Long beginCount, Long pageCount, Long userId);
    //获取用户
    User getUserInfo(Long userId, Long fileId);

    //通过用户搜索的文件名返回文件列表
    List<FileListVo> userSearchFileList(@Param("userFile") UserFile userFile, Long beginCount, Long pageCount);

    //通过id返回存储大小
    Long selectStorageSizeByUserId(@Param("userId") Long userId);

    //查找不符合扩展名的
    List<FileListVo> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, Long userId);
    //查找不符合扩展名的个数
    Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, Long userId);

    //通过扩展名找文件
    List<FileListVo> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, Long userId);
    //通过扩展名找文件个数
    Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, Long userId);

    //移动文件通过路径和名字
    void updateFilepathByPathAndName(String oldfilePath, String newfilePath, String fileName, String extendName, Long userId);
    //移动文件通过路径
    void updateFilepathByFilepath(String oldfilePath, String newfilePath, Long userId);
    //修改文件夹的名字
    void replaceFilePath(@Param("filePath") String filePath, String oldFilePath, Long userId);

    //管理员查看所有文件
    List<FileListVo> adminSelectFileNotInExtendNames(List<String> fileNameList, Long page, Long limit, Long userId);
    Long adminSelectCountNotInExtendNames(List<String> fileNameList, Long page, Long limit, Long userId);

    List<FileListVo> adminSelectFileByExtendName(List<String> fileNameList, Long page, Long limit, Long userId);
    Long adminSelectCountByExtendName(List<String> fileNameList, Long page, Long limit, Long userId);


}
