package com.vinylteam.vinyl.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@AllArgsConstructor
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

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Field(type = FieldType.Boolean)
    @EqualsAndHashCode.Exclude
    private Boolean offers;

    public boolean hasOffers() {
        return offers;
    }

    public void setHasOffers(boolean offers) {
        this.offers = offers;
    }
}
