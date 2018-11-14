package com.mmall.services;

import com.google.common.collect.Lists;
import com.mmall.util.FtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements  IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    public String upload(MultipartFile file, String path){

        String fileName =file.getOriginalFilename();  //文件名
        String fileExtensName =fileName.substring(fileName.lastIndexOf(".")+1);//获得后缀名jpg
        String uploadFileName= UUID.randomUUID().toString()+"."+fileExtensName;//避免上传文件同名覆盖 加随机数
        logger.info("开始上传文件，上传文件的文件名:{},上传路经:{},新的文件名:{}",fileName,path,uploadFileName);

        File fileDir =new File(path);
        if(!fileDir.exists()){//如果不存在
            fileDir.setWritable(true);//可写
            fileDir.mkdirs();//文件夹可创建
        }
        File targetFile =new File(path,uploadFileName);

        try {
            file.transferTo(targetFile);     //文件上传成功
            FtpUtil.uploadFile(Lists.newArrayList(targetFile));//将 targetFile上传到ftp服务器
            targetFile.delete();//上传完之后 删除upload下面文件
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return  null;
        }
    return targetFile.getName();
    }
}
