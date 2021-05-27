package com.vinylteam.vinyl.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserPost {

    private long id;
    private String name;
    private String email;
    private String theme;
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserPost() {
    }

    public UserPost(String name, String email, String theme, String message, LocalDateTime createdAt) {
        this.name = name;
        this.email = email;
        this.theme = theme;
        this.message = message;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPost that = (UserPost) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                Objects.equals(theme, that.theme) &&
                Objects.equals(message, that.message) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, theme, message, createdAt);
    }

    @Override
    public String toString() {
        return "UserPost{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", subject='" + theme + '\'' +
                ", message='" + message + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

}