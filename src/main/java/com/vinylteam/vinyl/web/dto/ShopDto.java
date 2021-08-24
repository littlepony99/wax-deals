package com.vinylteam.vinyl.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDto {
    private Integer id;
    private String mainPageLink;
    private String imageLink;
    private String smallImageLink;
    private String name;
}
