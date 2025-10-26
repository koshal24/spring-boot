package com.lms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "forum_posts")
public class ForumPost {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.DBRef
    private Course course;

    @org.springframework.data.mongodb.core.mapping.DBRef
    private User user;
    private String content;
    private Date timestamp;
    private List<Reply> replies;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reply {
        private String id;
        @org.springframework.data.mongodb.core.mapping.DBRef
        private User user;
        private String content;
        private Date timestamp;
    }
}
