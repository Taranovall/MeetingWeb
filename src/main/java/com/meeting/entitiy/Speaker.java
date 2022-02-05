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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Speaker speaker = (Speaker) o;

        return speakerTopics != null ? speakerTopics.equals(speaker.speakerTopics) : speaker.speakerTopics == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (speakerTopics != null ? speakerTopics.hashCode() : 0);
        return result;
    }
}
