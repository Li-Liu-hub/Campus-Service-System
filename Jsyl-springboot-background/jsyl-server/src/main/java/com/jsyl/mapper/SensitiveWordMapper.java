package com.jsyl.mapper;

import com.jsyl.entity.SensitiveWord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SensitiveWordMapper {

    @Select("SELECT * FROM sensitive_word WHERE status = 1")
    List<SensitiveWord> getAllEnabled();

    @Select("SELECT * FROM sensitive_word ORDER BY create_time DESC")
    List<SensitiveWord> getAll();

    @Select("SELECT * FROM sensitive_word WHERE id = #{id}")
    SensitiveWord getById(@Param("id") Long id);

    @Insert("INSERT INTO sensitive_word (word, category, level, status) " +
            "VALUES (#{word}, #{category}, #{level}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SensitiveWord word);

    @Update("UPDATE sensitive_word SET word = #{word}, category = #{category}, " +
            "level = #{level}, status = #{status} WHERE id = #{id}")
    void update(SensitiveWord word);

    @Delete("DELETE FROM sensitive_word WHERE id = #{id}")
    void delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM sensitive_word WHERE word = #{word}")
    Integer countByWord(@Param("word") String word);
}
