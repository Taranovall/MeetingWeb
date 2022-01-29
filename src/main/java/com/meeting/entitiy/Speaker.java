package com.meeting.entitiy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Speaker extends User {

    Map<Meeting, Set<Topic>> meetingsMap;

    public Speaker(String login) {
        super(login);
        this.meetingsMap = new HashMap<>();
    }

    public Speaker(Long id, String login) {
        super(id, login);
    }

    public Map<Meeting, Set<Topic>> getMeetingsMap() {
        return meetingsMap;
    }

    public void setMeetingsMap(Map<Meeting, Set<Topic>> meetingsMap) {
        this.meetingsMap = meetingsMap;
    }
}
