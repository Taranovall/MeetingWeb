package com.meeting.util;

import com.meeting.entitiy.Meeting;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SearchQueryUtil {

    public static List<Meeting> executeQuery(List<Meeting> meetingList, String query) {
        CopyOnWriteArrayList<Meeting> meetings = new CopyOnWriteArrayList<>(meetingList);
        // if search query was made then removes all meetings from list that don't contain search query ignoring case
        meetings.removeIf(meeting -> !meeting.getName().toLowerCase().contains(query.toLowerCase()));
        return meetings;
    }
}
