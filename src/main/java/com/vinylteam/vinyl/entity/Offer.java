package com.vinylteam.vinyl.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Optional;

@Data
@Document(indexName = "offer_index")
public class Offer {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String uniqueVinylId;

    @Field(type = FieldType.Integer)
    private int shopId;

    @Field(type = FieldType.Double)
    private double price;

    @Field(type = FieldType.Keyword)
    private Optional<Currency> currency;

    @EqualsAndHashCode.Exclude
    @Field(type = FieldType.Text)
    private String genre;

    @Field(type = FieldType.Keyword)
    private String catNumber;

    @Field(type = FieldType.Boolean)
    private boolean inStock;

    @Field(type = FieldType.Keyword)
    private String offerLink;

}
