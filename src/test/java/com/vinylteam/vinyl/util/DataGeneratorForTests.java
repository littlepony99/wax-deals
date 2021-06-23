
package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.*;
import com.vinylteam.vinyl.web.dto.UserChangeProfileInfoRequest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public UniqueVinyl getUniqueVinylWithNumber(int number) {
        if (number < 1) {
            throw new RuntimeException("Don't generate template unique vinyl from number < 1! number: " + number);
        }
        UniqueVinyl uniqueVinyl = new UniqueVinyl();
        uniqueVinyl.setId(number);
        uniqueVinyl.setRelease("release" + number);
        uniqueVinyl.setArtist("artist" + number);
        uniqueVinyl.setFullName(uniqueVinyl.getRelease() + " - " + uniqueVinyl.getArtist());
        uniqueVinyl.setImageLink("/image" + number);
        uniqueVinyl.setHasOffers(false);
        return uniqueVinyl;
    }

    public Offer getOfferWithUniqueVinylIdAndShopId(int uniqueVinylId, int shopId) {
        if (uniqueVinylId < 1 || shopId < 1) {
            throw new RuntimeException("Don't generate template offer from uniqueVinylId < 1 or shopId < 1! " +
                    "uniqueVinylId: " + uniqueVinylId + ", shopId: " + shopId);
        }
        Offer offer = new Offer();
        offer.setUniqueVinylId(uniqueVinylId);
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
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setId(userId);
        confirmationToken.setUserId(userId);
        confirmationToken.setToken(UUID.randomUUID());
        confirmationToken.setTimestamp(new Timestamp(userId));
        return confirmationToken;
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

    public List<UniqueVinyl> getUniqueVinylsList() {
        List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            UniqueVinyl uniqueVinyl = new UniqueVinyl();
            uniqueVinyl.setId(i + 1);
            uniqueVinyl.setRelease("release" + (i + 1));
            uniqueVinyl.setArtist("artist" + (i + 1));
            uniqueVinyl.setFullName(uniqueVinyl.getRelease() + " - " + uniqueVinyl.getArtist());
            uniqueVinyl.setImageLink("/image" + (i + 1));
            uniqueVinyl.setHasOffers(true);
            uniqueVinyls.add(uniqueVinyl);
        }
        uniqueVinyls.get(3).setHasOffers(false);
        return uniqueVinyls;
    }

    public List<Offer> getOffersList() {
        List<Offer> offers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                Offer offer = new Offer();
                offer.setId(i * 2 + j + 1);
                offer.setUniqueVinylId(i + 1);
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
            ConfirmationToken confirmationToken = new ConfirmationToken();
            confirmationToken.setId(i + 1L);
            confirmationToken.setUserId(i + 1L);
            confirmationToken.setToken(UUID.randomUUID());
            confirmationToken.setTimestamp(new Timestamp(i + 1L));
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
                offer.setUniqueVinylId(i + 1);
                offer.setShopId(rawOffer.getShopId());
                offer.setPrice(rawOffer.getPrice());
                offer.setCurrency(rawOffer.getCurrency());
                offer.setGenre(rawOffer.getGenre());
                offer.setCatNumber(rawOffer.getCatNumber());
                offer.setInStock(rawOffer.isInStock());
                offer.setOfferLink(rawOffer.getOfferLink());
                offers.add(offer);
            }
            UniqueVinyl uniqueVinyl = new UniqueVinyl();
            uniqueVinyl.setId(i + 1);
            uniqueVinyl.setRelease(rawOffers.get(i * 2).getRelease());
            uniqueVinyl.setArtist(rawOffers.get(i * 2).getArtist());
            uniqueVinyl.setFullName(uniqueVinyl.getRelease() + " - " + uniqueVinyl.getArtist());
            uniqueVinyl.setImageLink("/image" + (i + 1));
            uniqueVinyl.setHasOffers(true);
            uniqueVinyls.add(uniqueVinyl);
        }
    }

    public RecoveryToken getRecoveryTokenWithUserId(long userId) {
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setUserId(userId);
        recoveryToken.setToken(UUID.randomUUID());
        return recoveryToken;
    }

    public UserChangeProfileInfoRequest getUserChangeProfileInfo(){
        return UserChangeProfileInfoRequest.builder()
                .email("new@wax-deals.com")
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .confirmNewPassword("newPassword")
                .newDiscogsUserName("newDiscogsUserName")
                .build();
    }

}
