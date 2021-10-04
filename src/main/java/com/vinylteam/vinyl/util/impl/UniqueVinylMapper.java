package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.WantedVinyl;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UniqueVinylMapper {

    UniqueVinylDto uniqueVinylToDto(UniqueVinyl uniqueVinyl);

    List<UniqueVinylDto> uniqueVinylsToUniqueVinylDtoList(List<UniqueVinyl> uniqueVinyls);

    @Mapping(target = "id", source = "vinylId")
    UniqueVinylDto wantedVinylToUniqueVinylDto(WantedVinyl wantedVinyls);
}
