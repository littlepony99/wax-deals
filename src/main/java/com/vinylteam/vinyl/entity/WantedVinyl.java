package com.vinylteam.vinyl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@Document(indexName = "wanted_vinyl_index")
public class WantedVinyl {

    @Id
    private String id;

    @Field(type = FieldType.Integer)
    private Long userId;

    @Field(type = FieldType.Text)
    private String vinylId;

    @Field(type = FieldType.Date)
    private Date addedAt;

    @Field(type = FieldType.Keyword)
    private String release;

    @Field(type = FieldType.Text)
    private String artist;

    @Field(type = FieldType.Keyword)
    private String imageLink;

}
