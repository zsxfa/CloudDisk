package com.zsxfa.cloud.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.pojo.entity.UserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zsxfa.cloud.core.pojo.vo.FileListVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zsxfa
 */
public interface UserFileService extends IService<UserFile> {
    //通过用户返回文件列表
    List<FileListVo> userFileList(UserFile userFile, Long beginCount, Long pageCount);

    //管理员端返回文件列表
    List<FileListVo> adminFileList(Long beginCount, Long pageCount, Long userId);

    //通过用户所查询的文件名返回文件列表
    List<FileListVo> userSearchFileList(UserFile userFile, Long beginCount, Long pageCount);


    //查找不符合扩展名的
    List<FileListVo> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, Long userId);
    //查找不符合扩展名的个数
    Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, Long userId);

    //通过扩展名找文件
    List<FileListVo> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, Long userId);
    //通过扩展名找文件个数
    Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, Long userId);

    //移动文件到回收站 里面调用了updateFileDeleteStateByFilePath方法
    void deleteUserFile(Long userFileId, Long userId);
    //文件路径，模糊匹配右边 在updateFileDeleteStateByFilePath中会用到
    List<UserFile> selectFileListLikeRightFilePath(String filePath, long userId);

    //判断文件夹是否存在
    boolean isDirExist(String fileName, String filePath, Long userId);

    //移动文件
    void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName, Long userId);
    //获取文件目录树
    List<UserFile> selectFilePathTreeByUserId(Long userId);

    //通过名称和路径查询文件
    List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId);
    //修改文件夹的名字
    void replaceUserFilePath(String filePath, String oldFilePath, Long userId);

    //获取用户名
    User getUserInfo(Long userId, Long fileId);

    //按条件查询文件
    Page<UserFile> searchFileList(Long page, Long limit, String fileName, Long userId);

    //管理员查看所有文件
    List<FileListVo> adminsSelectFileNotInExtendNames(List<String> arrList, Long page, Long limit, Long userId);
    Long adminSelectCountNotInExtendNames(List<String> arrList, Long page, Long limit, Long userId);
    List<FileListVo> adminSelectFileByExtendName(List<String> fileExtendsByType, Long page, Long limit, Long userId);
    Long adminSelectCountByExtendName(List<String> fileExtendsByType, Long page, Long limit, Long userId);

    //查询相同文件
    List<UserFile> selectSameUserFile(String fileName, String filePath, String extendName, Long userId);
}


