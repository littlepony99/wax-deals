package com.vinylteam.vinyl.entity;

import java.sql.Timestamp;
import java.util.Objects;

public class RecoveryToken {
    private int id;
    private long userId;
    private String token;
    private Timestamp createdAt;
    private Timestamp lifeTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Timestamp lifeTime) {
        this.lifeTime = lifeTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecoveryToken that = (RecoveryToken) o;
        return id == that.id &&
                userId == that.userId &&
                Objects.equals(token, that.token) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(lifeTime, that.lifeTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, token, createdAt, lifeTime);
    }

    @Override
    public String toString() {
        return "RecoveryToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", token='" + token + '\'' +
                ", createdAt=" + createdAt +
                ", lifeTime=" + lifeTime +
                '}';
    }

}

