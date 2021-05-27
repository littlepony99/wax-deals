package com.vinylteam.vinyl.entity;

import java.util.Objects;

public class User {

    private Long id;
    private String email;
    private String password;
    private String discogsUserName;
    private String salt;
    private int iterations;
    private Role role;
    private boolean status;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDiscogsUserName() {
        return discogsUserName;
    }

    public void setDiscogsUserName(String discogsUserName) {
        this.discogsUserName = discogsUserName;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "\nUser{" +
                "email='" + email +
                ", role=" + role +
                ", status=" + status +
                ", discogsUserName=" + discogsUserName +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return iterations == user.iterations &&
                status == user.status &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(discogsUserName, user.discogsUserName) &&
                Objects.equals(salt, user.salt) &&
                role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, discogsUserName, salt, iterations, role, status);
    }

}