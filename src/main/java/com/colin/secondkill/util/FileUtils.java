package com.colin.secondkill.util;

import cn.hutool.core.io.IoUtil;
import com.colin.secondkill.exception.NullFileException;
import com.colin.secondkill.exception.ReadWriteFileException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 2024年06月07日16:19
 */
public class FileUtils {

    public static String readWriteFile(String headImgResourceLocationPrefix, String username, String originalFilename, long timestamp, InputStream inputStream) throws IOException, NullFileException, ReadWriteFileException {
        FileOutputStream fileOutputStream = null;
        String finalFileName = null;
        try {
            File file = new File(headImgResourceLocationPrefix + username);
            if (!file.exists() && !file.isDirectory()) {
                file.mkdir();
            }
            // 获取文件拼接时间戳后的最终名称
            finalFileName = FileUtils.getTimestampFileName(originalFilename, timestamp);

            fileOutputStream = new FileOutputStream(headImgResourceLocationPrefix + username + "/" + finalFileName);
            // 利用hutool工具包的IoUtil 快速的做文件的IO操作
            IoUtil.copy(inputStream, fileOutputStream);
            return finalFileName;
        }
//        catch (NullFileException | FileNotFoundException e) {
//            //清空之前的文件
//            File file = new File(headImgResourceLocationPrefix + username + "/" + finalFileName);
//            if (file.exists()){
//                file.delete();
//            }
//            throw new ReadWriteFileException("文件读写异常");
//        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    public static String getSuffixName(String originalFileName) throws NullFileException {
        if(originalFileName == null){
            throw new NullFileException("originalFileName is null");
        }
        int i = originalFileName.lastIndexOf(".");
        return originalFileName.substring(i);//.txt
    }

    /**
     * 获取拼接时间后的文件最终名称
     * @param originalFileName 用户上传时的原始名称
     * @return
     */
    public static String getTimestampFileName(String originalFileName, long currentTime) throws NullFileException {
        if(originalFileName == null){
            throw new NullFileException("originalFileName is null");
        }
        int i = originalFileName.lastIndexOf(".");
        String suffixName = originalFileName.substring(i);//.txt
        return originalFileName.substring(0, i) + "-" + currentTime + suffixName;
    }
}
