package com.jsyl.mapper;

import com.jsyl.entity.OperationLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    @Insert("INSERT INTO operation_log (operator_id, operator_name, operation_type, operation, " +
            "request_method, request_url, request_params, response_result, ip_address, user_agent, " +
            "execution_time, status, error_msg) " +
            "VALUES (#{operatorId}, #{operatorName}, #{operationType}, #{operation}, " +
            "#{requestMethod}, #{requestUrl}, #{requestParams}, #{responseResult}, #{ipAddress}, #{userAgent}, " +
            "#{executionTime}, #{status}, #{errorMsg})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OperationLog log);

    @Select("<script>" +
            "SELECT * FROM operation_log WHERE 1=1 " +
            "<if test='operatorName != null and operatorName != \"\"'> AND operator_name LIKE CONCAT('%', #{operatorName}, '%')</if>" +
            "<if test='operationType != null and operationType != \"\"'> AND operation_type = #{operationType}</if>" +
            "<if test='startTime != null'> AND create_time &gt;= #{startTime}</if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime}</if>" +
            "ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<OperationLog> list(@Param("operatorName") String operatorName,
                           @Param("operationType") String operationType,
                           @Param("startTime") String startTime,
                           @Param("endTime") String endTime,
                           @Param("offset") int offset,
                           @Param("pageSize") int pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM operation_log WHERE 1=1 " +
            "<if test='operatorName != null and operatorName != \"\"'> AND operator_name LIKE CONCAT('%', #{operatorName}, '%')</if>" +
            "<if test='operationType != null and operationType != \"\"'> AND operation_type = #{operationType}</if>" +
            "<if test='startTime != null'> AND create_time &gt;= #{startTime}</if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime}</if>" +
            "</script>")
    int count(@Param("operatorName") String operatorName,
              @Param("operationType") String operationType,
              @Param("startTime") String startTime,
              @Param("endTime") String endTime);

    @Select("SELECT * FROM operation_log WHERE id = #{id}")
    OperationLog getById(Long id);

    @Delete("DELETE FROM operation_log")
    void deleteAll();

    @Delete("DELETE FROM operation_log WHERE id IN (${ids})")
    void batchDelete(@Param("ids") String ids);

    @Select("<script>" +
            "SELECT * FROM operation_log WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<OperationLog> getByIds(@Param("ids") List<Long> ids);

}
