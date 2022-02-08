package com.meeting.entitiy;

public enum Role {

    USER, SPEAKER, MODERATOR;

    public static Role getRoleByString(String s) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(s)) return role;
        }
        return null;
    }

    @Override
    public String toString() {
        String role = super.toString().toLowerCase();
        return Character.toString(role.charAt(0)).toUpperCase() + role.substring(1);
    }
}
