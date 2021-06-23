package com.vinylteam.vinyl.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "unique_vinyl_index", createIndex = true)
public class UniqueVinyl {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String release;

    @Field(type = FieldType.Text)
    private String artist;

    @Field(type = FieldType.Text)
    private String fullName;

    @Field(type = FieldType.Keyword)
    private String imageLink;

    @Field(type = FieldType.Boolean)
    private boolean hasOffers;

}
