package com.meeting.entitiy;

import java.util.HashMap;
import java.util.Map;

public class Speaker extends User {

    Map<Meeting, Map<Topic, State>> speakerTopics = new HashMap<>();

    public Speaker(String login) {
        super(login);
    }

    public Speaker() {
    }

    public Speaker(Long id, String login) {
        super(id, login);
    }

    public Map<Meeting, Map<Topic, State>> getSpeakerTopics() {
        return speakerTopics;
    }

    public void setSpeakerTopics(Map<Meeting, Map<Topic, State>> speakerTopics) {
        this.speakerTopics = speakerTopics;
    }
}
