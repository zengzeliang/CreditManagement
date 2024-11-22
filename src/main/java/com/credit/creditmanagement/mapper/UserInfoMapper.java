package com.credit.creditmanagement.mapper;

import com.credit.creditmanagement.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

public interface UserInfoMapper {

    /**
     * 新增用户信息
     */
    int insertUserInfo(@Param("userInfo") UserInfo userInfo);

}
