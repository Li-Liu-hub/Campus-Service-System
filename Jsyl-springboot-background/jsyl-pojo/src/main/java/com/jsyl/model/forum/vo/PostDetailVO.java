package com.jsyl.model.forum.vo;

import com.jsyl.model.user.vo.UserPostVO;
import com.jsyl.model.user.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailVO {

    private Long id;

    private String title;

    private String content;

    private Integer viewCount;

    private Integer replyCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private UserPostVO author;
}
