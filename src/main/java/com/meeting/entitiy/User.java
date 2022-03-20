package com.meeting.entitiy;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class User implements Serializable {

    private static final long serialVersionUID = -4017985734240189107L;
    private Long id;
    private String login;
    private String password;
    private String registrationDate;
    private String email;
    private Role role;
    private List<Long> meetingIdsSetUserTakesPart = new LinkedList<>();

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Long> getMeetingIdsSetUserTakesPart() {
        return meetingIdsSetUserTakesPart;
    }

    public void setMeetingIdsSetUserTakesPart(List<Long> meetingIdsSetUserTakesPart) {
        this.meetingIdsSetUserTakesPart = meetingIdsSetUserTakesPart;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public User(Long id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public User(String email, String login, Long id) {
        this.id = id;
        this.login = login;
        this.email = email;
    }

    public User(Long id, String login) {
        this.id = id;
        this.login = login;
    }

    public User() {

    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password.trim();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (login != null ? !login.equals(user.login) : user.login != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        return role == user.role;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", meetingIdsSetUserTakesPart=" + meetingIdsSetUserTakesPart +
                '}';
    }
}
