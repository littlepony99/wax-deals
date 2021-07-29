package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.web.controller.OneVinylOfferController;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = OneVinylOfferController.class)
public interface UniqueVinylMapper {

    UniqueVinylDto map(UniqueVinyl vinyl);

    List<UniqueVinylDto> listVinylsToListVinylsDto(List<UniqueVinyl> vinyls);

}
