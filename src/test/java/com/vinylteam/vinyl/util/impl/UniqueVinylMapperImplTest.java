package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class UniqueVinylMapperImplTest {

    @Autowired
    private UniqueVinylMapper uniqueVinylMapper;

    @Test
    public void testMapperList() {
        List<UniqueVinyl> vinylList = new ArrayList<>();
        UniqueVinyl vinyl = UniqueVinyl.builder()
                .release("RELEASE")
                .imageLink("imageLine")
                .id("123")
                .hasOffers(true)
                .fullName("funn lame")
                .artist("artist")
                .build();
        vinylList.add(vinyl);
        List<UniqueVinylDto> dto = uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(vinylList);
        Assertions.assertEquals(dto.get(0).getId(), vinylList.get(0).getId());
        Assertions.assertEquals(dto.get(0).getRelease(), vinylList.get(0).getRelease());
        Assertions.assertEquals(dto.get(0).getImageLink(), vinylList.get(0).getImageLink());
        Assertions.assertEquals(dto.get(0).getArtist(), vinylList.get(0).getArtist());
    }

    @Test
    public void testMapper() {
        UniqueVinyl vinyl = UniqueVinyl.builder()
                .release("RELEASE")
                .imageLink("imageLine")
                .id("123")
                .hasOffers(true)
                .artist("artist")
                .build();
        UniqueVinylDto dto = uniqueVinylMapper.uniqueVinylToDto(vinyl);
        Assertions.assertEquals(dto.getId(), vinyl.getId());
        Assertions.assertEquals(dto.getArtist(), vinyl.getArtist());
        Assertions.assertEquals(dto.getImageLink(), vinyl.getImageLink());
        Assertions.assertEquals(dto.getRelease(), vinyl.getRelease());
    }

}