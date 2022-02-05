package com.meeting.entitiy;

public enum State {
    ACCEPTED, CANCELED, NOT_DEFINED;

    public static State getStateByString(String s) {
        for (State state : State.values()) {
            if (state.name().equalsIgnoreCase(s)) return state;
        }
        return null;
    }
}
