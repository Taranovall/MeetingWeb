package com.meeting.entitiy;

public enum Role {

    USER, SPEAKER, MODERATOR;

    public static Role getRoleByString(String s) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(s)) return role;
        }
        return null;
    }
}
