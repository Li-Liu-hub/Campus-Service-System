package com.jsyl.model.forum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostPublishDTO {

    @NotBlank(message = "帖子标题不能为空")
    private String title;

    @NotBlank(message = "帖子内容不能为空")
    private String content;
}
