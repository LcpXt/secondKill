package com.colin.secondkill.mapper;

import com.colin.secondkill.bean.HeadImg;
import com.colin.secondkill.bean.HeadImg;

/**
 * 2024年06月07日20:54
 */
public interface FileMapper {
    HeadImg selectFileById(int id);

    void insertHeadImg(HeadImg headImg);
}
