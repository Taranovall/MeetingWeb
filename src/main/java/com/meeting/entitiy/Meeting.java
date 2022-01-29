package com.meeting.entitiy;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Meeting implements Serializable {

    private Long id;
    private String name;
    private String date;
    private String time;
    private String place;
    private Set<Speaker> speakers;
    private List<User> participants;
    private Set<Topic> topics;
    private String photoPath;

    public Meeting(Long id, String name, String date, String time, String place, String photoPath) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.place = place;
        this.photoPath = photoPath;
        this.topics = new HashSet<>();
    }

    public Meeting(Long id, String name, String date, String time, String place) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.place = place;
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

    public Set<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(Set<Speaker> speakers) {
        this.speakers = speakers;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
