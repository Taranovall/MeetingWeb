package com.meeting.entitiy;

import java.io.Serializable;
import java.util.*;

public class Meeting implements Serializable {

    private Long id;
    private String name;
    private String date;
    private String timeStart;
    private String timeEnd;
    private String place;
    private List<User> participants;
    private Set<Topic> freeTopics;
    private String photoPath;
    double percentageAttendance;
    private Map<Speaker, Set<Topic>> speakerTopics;
    private Map<Topic, Set<Speaker>> sentApplicationsMap;
    private Map<Topic, Speaker> proposedTopicsMap;
    private boolean isStarted;
    private boolean isGoingOnNow;
    private boolean isPassed;

    public Meeting(Long id, String name, String date, String timeStart, String timeEnd, String place, String photoPath) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.place = place;
        this.photoPath = photoPath;
        this.freeTopics = new HashSet<>();
        this.speakerTopics = new HashMap<>();
        this.sentApplicationsMap = new HashMap<>();
        this.proposedTopicsMap = new HashMap<>();
        this.participants = new LinkedList<>();
    }

    public Meeting(String name, String date, String timeStart, String timeEnd, String place) {
        this.name = name;
        this.date = date;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.place = place;
        this.freeTopics = new HashSet<>();
        this.speakerTopics = new HashMap<>();
        this.sentApplicationsMap = new HashMap<>();
        this.proposedTopicsMap = new HashMap<>();
        this.participants = new LinkedList<>();
    }

    public Meeting() {
        this.freeTopics = new HashSet<>();
        this.speakerTopics = new HashMap<>();
        this.sentApplicationsMap = new HashMap<>();
        this.proposedTopicsMap = new HashMap<>();
        this.participants = new LinkedList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPercentageAttendance() {
        return percentageAttendance;
    }

    public void setPercentageAttendance(double percentageAttendance) {
        this.percentageAttendance = percentageAttendance;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public boolean isGoingOnNow() {
        return isGoingOnNow;
    }

    public void setGoingOnNow(boolean goingOnNow) {
        isGoingOnNow = goingOnNow;
    }

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
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


    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Map<Speaker, Set<Topic>> getSpeakerTopics() {
        return speakerTopics;
    }

    public Map<Topic, Set<Speaker>> getSentApplicationsMap() {
        return sentApplicationsMap;
    }

    public void setSentApplicationsMap(Map<Topic, Set<Speaker>> sentApplicationsMap) {
        this.sentApplicationsMap = sentApplicationsMap;
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

    public Map<Topic, Speaker> getProposedTopicsMap() {
        return proposedTopicsMap;
    }

    public void setProposedTopicsMap(Map<Topic, Speaker> proposedTopicsMap) {
        this.proposedTopicsMap = proposedTopicsMap;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meeting meeting = (Meeting) o;

        if (id != null ? !id.equals(meeting.id) : meeting.id != null) return false;
        if (name != null ? !name.equals(meeting.name) : meeting.name != null) return false;
        if (date != null ? !date.equals(meeting.date) : meeting.date != null) return false;
        if (timeStart != null ? !timeStart.equals(meeting.timeStart) : meeting.timeStart != null) return false;
        if (timeEnd != null ? !timeEnd.equals(meeting.timeEnd) : meeting.timeEnd != null) return false;
        if (place != null ? !place.equals(meeting.place) : meeting.place != null) return false;
        return photoPath != null ? photoPath.equals(meeting.photoPath) : meeting.photoPath == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (timeStart != null ? timeStart.hashCode() : 0);
        result = 31 * result + (timeEnd != null ? timeEnd.hashCode() : 0);
        result = 31 * result + (place != null ? place.hashCode() : 0);
        result = 31 * result + (photoPath != null ? photoPath.hashCode() : 0);
        return result;
    }
}
