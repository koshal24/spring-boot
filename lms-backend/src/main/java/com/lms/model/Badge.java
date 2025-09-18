package com.lms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "badges")
public class Badge {
    @Id
    private String id;
    private String name;
    private String description;
    private String iconUrl;
}
