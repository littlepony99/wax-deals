
package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.*;
import com.vinylteam.vinyl.web.dto.OneVinylOfferDto;
import com.vinylteam.vinyl.web.dto.OneVinylPageDto;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;

import java.sql.Timestamp;
import java.util.*;

public class DataGeneratorForTests {

    public Shop getShopWithNumber(int number) {
        if (number < 1) {
            throw new RuntimeException("Don't generate template shop from number < 1! number: " + number);
        }
        Shop shop = new Shop();
        shop.setId(number);
        shop.setName("shop" + number);
        shop.setMainPageLink(shop.getName() + "/main");
        shop.setImageLink(shop.getName() + "/image.png");
        return shop;
    }

    public User getUserWithNumber(int number) {
        if (number < 1) {
            throw new RuntimeException("Don't generate template user from number < 1! number: " + number);
        }
        User user = new User();
        user.setId((long) number);
        user.setEmail("user" + number + "@wax-deals.com");
        user.setPassword("hash" + number);
        user.setSalt("salt" + number);
        user.setIterations(number);
        user.setRole(Role.USER);
        user.setStatus(false);
        user.setDiscogsUserName("discogsUserName" + number);
        return user;
    }

    public UserInfoRequest getUserInfoRequestWithNumber(int number) {
        if (number < 1) {
            throw new RuntimeException("Don't generate template user from number < 1! number: " + number);
        }
        UserInfoRequest userInfoRequest = UserInfoRequest.builder()
                .email("user" + number + "@wax-deals.com")
                .password("Password123" + number)
                .passwordConfirmation("Password123" + number)
                .newPassword("NewPassword1234" + number)
                .newPassword("NewPassword1234" + number)
                .discogsUserName("discogsUserName" + number)
                .newDiscogsUserName("newDiscogsUserName" + number)
                .token(UUID.randomUUID().toString())
                .build();
        return userInfoRequest;
    }

    public UniqueVinyl getUniqueVinylWithNumber(int number) {
        if (number < 1) {
            throw new RuntimeException("Don't generate template unique vinyl from number < 1! number: " + number);
        }
        UniqueVinyl uniqueVinyl = generateUniqueVinyl("artist" + number, number, "release", false);
        return uniqueVinyl;
    }

    public Offer getOfferWithUniqueVinylIdAndShopId(int uniqueVinylId, int shopId) {
        if (uniqueVinylId < 1 || shopId < 1) {
            throw new RuntimeException("Don't generate template offer from uniqueVinylId < 1 or shopId < 1! " +
                    "uniqueVinylId: " + uniqueVinylId + ", shopId: " + shopId);
        }
        Offer offer = new Offer();
        offer.setUniqueVinylId(Integer.toString(uniqueVinylId));
        offer.setShopId(shopId);
        offer.setPrice(uniqueVinylId * 10. + shopId);
        offer.setCurrency(Optional.of(Currency.UAH));
        offer.setGenre("genre" + uniqueVinylId);
        offer.setCatNumber("SHOP" + shopId + "REL" + uniqueVinylId);
        offer.setInStock(true);
        offer.setOfferLink("shop" + offer.getShopId() + "/release" + offer.getUniqueVinylId());
        return offer;
    }

    public RawOffer getRawOfferWithShopIdAndNumber(int shopId, int number) {
        if (number < 1) {
            throw new RuntimeException("Don't generate template raw offer from shopId < 1 or number < 1! shopId: " +
                    shopId + " number: " + number);
        }
        RawOffer rawOffer = new RawOffer();
        rawOffer.setShopId(shopId);
        rawOffer.setRelease("release" + number);
        rawOffer.setArtist("artist" + number);
        rawOffer.setPrice(number * 10. + shopId);
        rawOffer.setCurrency(Optional.of(Currency.UAH));
        rawOffer.setGenre("genre" + number);
        rawOffer.setCatNumber("SHOP" + shopId + "REL" + number);
        rawOffer.setInStock(true);
        rawOffer.setOfferLink("shop" + rawOffer.getShopId() + "/" + rawOffer.getRelease());
        rawOffer.setImageLink("/image" + number);
        return rawOffer;
    }

    public ConfirmationToken getConfirmationTokenWithUserId(long userId) {
        return ConfirmationToken.builder()
                .id(userId)
                .userId(userId)
                .token(UUID.randomUUID())
                .timestamp(new Timestamp(userId))
                .build();
    }

    public List<Shop> getShopsList() {
        List<Shop> shops = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            Shop shop = new Shop();
            shop.setId(i);
            shop.setName("shop" + i);
            shop.setMainPageLink(shop.getName() + "/main");
            shop.setImageLink(shop.getName() + "/image.png");
            shop.setSmallImageLink(shop.getName() + "/smallImage.png");
            shops.add(shop);
        }
        return shops;
    }

    public Map<String, List<?>> getOneVinylOffersAndShopsMap() {
        Map<String, List<?>> offersAndShopsMap = new HashMap<>();
        offersAndShopsMap.put("offers", getOffersList().subList(0, 2));
        offersAndShopsMap.put("shops", getShopsList().subList(0, 2));
        return offersAndShopsMap;
    }

    public List<UniqueVinyl> getUniqueVinylsList() {
        List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            UniqueVinyl uniqueVinyl = generateUniqueVinyl("artist" + i, i, "release", true);
            uniqueVinyls.add(uniqueVinyl);
        }
        uniqueVinyls.get(3).setHasOffers(false);
        return uniqueVinyls;
    }

    public List<UniqueVinyl> getUniqueVinylsByArtistList(String artist) {
        List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            UniqueVinyl uniqueVinyl = generateUniqueVinyl(artist, i, "release" + (i + 1), true);
            uniqueVinyls.add(uniqueVinyl);
        }
        return uniqueVinyls;
    }

    private UniqueVinyl generateUniqueVinyl(String artist, int index, String releaseFullName, boolean hasOffers) {
        return UniqueVinyl.builder()
                .id(Integer.toString(index))
                .release("release" + index)
                .artist(artist)
                .fullName("release" + index + " - " + artist)
                .imageLink("/image" + index)
                .hasOffers(hasOffers)
                .build();
    }

    public List<Offer> getOffersList() {
        List<Offer> offers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                Offer offer = new Offer();
                offer.setId(Integer.toString(i * 2 + j + 1));
                offer.setUniqueVinylId(Integer.toString(i + 1));
                offer.setShopId(j + 1);
                offer.setPrice((i + 1) * 10. + j + 1);
                offer.setCurrency(Optional.of(Currency.UAH));
                offer.setGenre("genre" + (i + 1));
                offer.setCatNumber("SHOP" + offer.getShopId() + "REL" + (i + 1));
                offer.setInStock(true);
                offer.setOfferLink("shop" + offer.getShopId() + "/release" + (i + 1));
                offers.add(offer);
            }
        }
        return offers;
    }

    public List<User> getUsersList() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            User user = new User();
            user.setEmail("user" + (i + 1) + "@wax-deals.com");
            user.setPassword("hash" + (i + 1));
            user.setDiscogsUserName("discogsUserName" + (i + 1));
            user.setSalt("salt" + (i + 1));
            user.setIterations(i + 1);
            user.setRole(Role.USER);
            user.setStatus(false);
            user.setDiscogsUserName("discogsUserName" + (i + 1));
            users.add(user);
        }
        return users;
    }

    public List<ConfirmationToken> getConfirmationTokensList() {
        List<ConfirmationToken> confirmationTokens = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ConfirmationToken confirmationToken = ConfirmationToken.builder()
                    .id(i + 1L)
                    .userId(i + 1L)
                    .token(UUID.randomUUID())
                    .timestamp(new Timestamp(i + 1L))
                    .build();
            confirmationTokens.add(confirmationToken);
        }
        return confirmationTokens;
    }

    public void fillListsForRawOffersSorterTest(List<RawOffer> rawOffers, List<UniqueVinyl> uniqueVinyls, List<Offer> offers) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                RawOffer rawOffer = new RawOffer();
                rawOffer.setShopId(j + 1);
                rawOffer.setRelease("release" + (i + 1));
                rawOffer.setArtist("artist" + (i + 1));
                rawOffer.setPrice((i + 1) * 10. + j + 1);
                rawOffer.setCurrency(Optional.of(Currency.UAH));
                rawOffer.setGenre("genre" + i);
                rawOffer.setCatNumber("SHOP" + rawOffer.getShopId() + "REL" + (i + 1));
                rawOffer.setInStock(true);
                rawOffer.setOfferLink("shop" + rawOffer.getShopId() + "/" + rawOffer.getRelease());
                rawOffer.setImageLink("/image" + (i + 1));
                rawOffers.add(rawOffer);

                Offer offer = new Offer();
                offer.setUniqueVinylId(Integer.toString(i + 1));
                offer.setShopId(rawOffer.getShopId());
                offer.setPrice(rawOffer.getPrice());
                offer.setCurrency(rawOffer.getCurrency());
                offer.setGenre(rawOffer.getGenre());
                offer.setCatNumber(rawOffer.getCatNumber());
                offer.setInStock(rawOffer.isInStock());
                offer.setOfferLink(rawOffer.getOfferLink());
                offers.add(offer);
            }
            UniqueVinyl uniqueVinyl = UniqueVinyl.builder()
                    .id(Integer.toString(i + 1))
                    .release(rawOffers.get(i * 2).getRelease())
                    .artist(rawOffers.get(i * 2).getArtist())
                    .fullName(rawOffers.get(i * 2).getRelease() + " - " + rawOffers.get(i * 2).getArtist())
                    .imageLink("/image" + (i + 1))
                    .hasOffers(true)
                    .build();
            uniqueVinyls.add(uniqueVinyl);
        }
    }

    public RecoveryToken getRecoveryTokenWithUserId(long userId) {
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setUserId(userId);
        recoveryToken.setToken(UUID.randomUUID());
        return recoveryToken;
    }

    public UserInfoRequest getUserChangeProfileInfo() {
        return UserInfoRequest.builder()
                .email("new@wax-deals.com")
                .password("oldPassword")
                .newPassword("newPassword")
                .newPasswordConfirmation("newPassword")
                .discogsUserName("newDiscogsUserName")
                .build();
    }

    public UniqueVinylDto getUniqueVinylDtoFromUniqueVinyl(UniqueVinyl uniqueVinyl) {
        return UniqueVinylDto.builder()
                .id(uniqueVinyl.getId())
                .artist(uniqueVinyl.getArtist())
                .release(uniqueVinyl.getRelease())
                .imageLink(uniqueVinyl.getImageLink())
                .isWantListItem(false)
                .build();
    }

    public List<UniqueVinylDto> getUniqueVinylDtoListFromUniqueVinylList(List<UniqueVinyl> uniqueVinyls) {
        List<UniqueVinylDto> uniqueVinylDtoList = new ArrayList<>();
        for (UniqueVinyl uniqueVinyl : uniqueVinyls) {
            uniqueVinylDtoList.add(getUniqueVinylDtoFromUniqueVinyl(uniqueVinyl));
        }
        return uniqueVinylDtoList;
    }

    public OneVinylOfferDto getOneVinylOfferDtoFromOfferAndShop(Offer offer, Shop shop) {
        if (offer == null && shop == null) {
            return null;
        }

        OneVinylOfferDto.OneVinylOfferDtoBuilder oneVinylOfferDto = OneVinylOfferDto.builder();

        if (offer != null) {
            if (offer.getCurrency() != null) {
                oneVinylOfferDto.currency(offer.getCurrency().get().toString());
            }
            oneVinylOfferDto.id(offer.getId());
            oneVinylOfferDto.price(offer.getPrice());
            oneVinylOfferDto.catNumber(offer.getCatNumber());
            oneVinylOfferDto.inStock(offer.isInStock());
            oneVinylOfferDto.offerLink(offer.getOfferLink());
        }
        if (shop != null) {
            oneVinylOfferDto.shopImageLink(shop.getSmallImageLink());
        }

        return oneVinylOfferDto.build();
    }

    public OneVinylPageDto getOneVinylPageDto(String discogsLink, UniqueVinyl mainVinyl, Map<String, List<?>> offersAndShops, List<UniqueVinyl> otherVinylsByArtist) {
        if (mainVinyl == null || offersAndShops == null) {
            throw new RuntimeException("Unique vinyl or map of offers and shops is null!");
        }
        List<OneVinylOfferDto> offerDtoList = new ArrayList<>();
        List<Offer> offers = (List<Offer>) offersAndShops.get("offers");
        List<Shop> shops = (List<Shop>) offersAndShops.get("shops");
        for (Offer offer : offers) {
            Shop shopById = shops
                    .stream()
                    .filter(shop -> shop.getId() == offer.getShopId())
                    .findFirst()
                    .get();
            offerDtoList.add(getOneVinylOfferDtoFromOfferAndShop(offer, shopById));
        }
        return OneVinylPageDto.builder()
                .discogsLink(discogsLink)
                .mainVinyl(getUniqueVinylDtoFromUniqueVinyl(mainVinyl))
                .offersList(offerDtoList)
                .vinylsByArtistList(getUniqueVinylDtoListFromUniqueVinylList(otherVinylsByArtist))
                .build();
    }

}
