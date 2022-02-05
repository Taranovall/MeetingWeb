package com.meeting.entitiy;

import java.io.Serializable;
import java.util.*;

public class Meeting implements Serializable {

    private Long id;
    private String name;
    private String date;
    private String time;
    private String place;
    private List<User> participants;
    private Set<Topic> freeTopics;
    private String photoPath;
    private Map<Speaker, Set<Topic>> speakerTopics;

    public Meeting(Long id, String name, String date, String time, String place, String photoPath) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.place = place;
        this.photoPath = photoPath;
        this.freeTopics = new HashSet<>();
        this.speakerTopics = new HashMap<>();
    }

    public Meeting() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Map<Speaker, Set<Topic>> getSpeakerTopics() {
        return speakerTopics;
    }

    public void setSpeakerTopics(Map<Speaker, Set<Topic>> speakerTopics) {
        this.speakerTopics = speakerTopics;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public Set<Topic> getFreeTopics() {
        return freeTopics;
    }

    public void setFreeTopics(Set<Topic> freeTopics) {
        this.freeTopics = freeTopics;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meeting meeting = (Meeting) o;

        if (id != null ? !id.equals(meeting.id) : meeting.id != null) return false;
        if (name != null ? !name.equals(meeting.name) : meeting.name != null) return false;
        if (date != null ? !date.equals(meeting.date) : meeting.date != null) return false;
        if (time != null ? !time.equals(meeting.time) : meeting.time != null) return false;
        if (place != null ? !place.equals(meeting.place) : meeting.place != null) return false;
        if (participants != null ? !participants.equals(meeting.participants) : meeting.participants != null)
            return false;
        if (freeTopics != null ? !freeTopics.equals(meeting.freeTopics) : meeting.freeTopics != null) return false;
        if (photoPath != null ? !photoPath.equals(meeting.photoPath) : meeting.photoPath != null) return false;
        return speakerTopics != null ? speakerTopics.equals(meeting.speakerTopics) : meeting.speakerTopics == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (place != null ? place.hashCode() : 0);
        result = 31 * result + (participants != null ? participants.hashCode() : 0);
        result = 31 * result + (freeTopics != null ? freeTopics.hashCode() : 0);
        result = 31 * result + (photoPath != null ? photoPath.hashCode() : 0);
        result = 31 * result + (speakerTopics != null ? speakerTopics.hashCode() : 0);
        return result;
    }
}
