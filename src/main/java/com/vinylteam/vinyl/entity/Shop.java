package com.vinylteam.vinyl.entity;

import java.util.Objects;

public class Shop {

    private int id;
    private String mainPageLink;
    private String imageLink;
    private String smallImageLink;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMainPageLink() {
        return mainPageLink;
    }

    public void setMainPageLink(String mainPageLink) {
        this.mainPageLink = mainPageLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmallImageLink() {
        return smallImageLink;
    }

    public void setSmallImageLink(String smallImageLink) {
        this.smallImageLink = smallImageLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shop shop = (Shop) o;
        return id == shop.id &&
                Objects.equals(mainPageLink, shop.mainPageLink) &&
                Objects.equals(imageLink, shop.imageLink) &&
                Objects.equals(smallImageLink, shop.smallImageLink) &&
                Objects.equals(name, shop.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mainPageLink, imageLink, smallImageLink, name);
    }

    @Override
    public String toString() {
        return "\nShop{" +
                "id=" + id +
                ", mainPageLink='" + mainPageLink + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", smallImageLink='" + smallImageLink + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
