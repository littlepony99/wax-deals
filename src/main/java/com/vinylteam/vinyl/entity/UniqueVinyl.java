package com.vinylteam.vinyl.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "unique_vinyl_index")
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
    @EqualsAndHashCode.Exclude
    private boolean hasOffers;


    public boolean getHasOffers(){
        return hasOffers;
    }

    public boolean isHasOffers(){
        return hasOffers;
    }
}
