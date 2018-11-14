package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


public class FtpUtil {
    private static  final Logger logger =LoggerFactory.getLogger(FtpUtil.class);

    private static String ftpIp=PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser=PropertiesUtil.getProperty("ftp.user");
    private static String ftppass=PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int prot;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FtpUtil(String ip,int port,String user,String pwd){
        this.ip=ip;
        this.prot=port;
        this.user=user;
        this.pwd=pwd;
    }
    public static boolean uploadFile(List<File> fileList) throws IOException {
            FtpUtil ftpUtil =new FtpUtil(ftpIp,21,ftpUser,ftppass);
            logger.info("开始连接FTP服务器");
            boolean result =ftpUtil.uploadFile("img",fileList);
            logger.info("开始连接FTP服务器，结束上传");
                return result;

    }
    private boolean uploadFile(String remotPath,List<File> fileList) throws IOException {
        boolean uploaded=true;
        FileInputStream fis=null;
        //连接FTP服务器
        if(connertServer(this.ip,this.prot,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotPath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for(File fileItem:fileList){
                    fis=new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
            } catch (IOException e) {
              logger.error("上传文件异常",e);
                e.printStackTrace();
            }finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }
    private boolean connertServer(String ip,int port,String user,String pwd){
        boolean isSuccess=false;
        ftpClient =new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess=ftpClient.login(user,pwd);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
            e.printStackTrace();
        }
        return isSuccess;
    }


    public static String getFtpIp() {
        return ftpIp;
    }

    public static void setFtpIp(String ftpIp) {
        FtpUtil.ftpIp = ftpIp;
    }

    public static String getFtpUser() {
        return ftpUser;
    }

    public static void setFtpUser(String ftpUser) {
        FtpUtil.ftpUser = ftpUser;
    }

    public static String getFtppass() {
        return ftppass;
    }

    public static void setFtppass(String ftppass) {
        FtpUtil.ftppass = ftppass;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getProt() {
        return prot;
    }

    public void setProt(int prot) {
        this.prot = prot;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
