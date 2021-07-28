package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.web.controller.CatalogController;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = CatalogController.class)
public interface UniqueVinylMapper {

    List<UniqueVinylDto> uniqueVinylsToUniqueVinylDtoList(List<UniqueVinyl> uniqueVinyls);

}
