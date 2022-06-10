package com.sf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.bean.UserBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

//有@MapperScan就不用@Mapper
public interface UserMapper extends BaseMapper<UserBean> {

    int delUserById(@Param("id") Long id);

    List<UserBean> queryUserList(Map<String, Object> params);

    void insertUser(UserBean userBean);
}
