package com.vinylteam.vinyl.entity;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

public class ConfirmationToken {

    private long id;
    private long userId;
    private UUID token;
    private Timestamp timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserToken{" +
                "id=" + id +
                ", user_id=" + userId +
                ", token=" + token +
                ", timestamp" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfirmationToken)) return false;
        ConfirmationToken confirmationToken = (ConfirmationToken) o;
        return id == confirmationToken.id && userId == confirmationToken.userId && Objects.equals(token, confirmationToken.token) && Objects.equals(timestamp, confirmationToken.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, token, timestamp);
    }

}
