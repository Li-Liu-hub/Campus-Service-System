package com.jsyl.mapper;

import com.jsyl.entity.CampusInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CampusInfoMapper {

    @Select("select * from campus_info")
    List<CampusInfo> list();

    @Select("select campus_name from campus_info where id = #{id}")
    String getCampusNameById(Integer id);
}
