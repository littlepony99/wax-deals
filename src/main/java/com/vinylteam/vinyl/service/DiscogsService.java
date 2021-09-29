package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.exception.DiscogsBadRequestException;
import org.json.simple.parser.ParseException;

import java.util.List;

public interface DiscogsService {

    List<UniqueVinyl> getDiscogsMatchList(String discogsUserName, List<UniqueVinyl> allUniqueVinyl) throws DiscogsBadRequestException;

    String getDiscogsLink(String artist, String release, String fullName) throws ParseException;

}
