package com.meeting.util;

import com.meeting.entitiy.Meeting;

import java.util.List;

public class SearchQueryUtil {

    public static void executeQuery(List<Meeting> meetingList, String query) {
        // if search query was made then removes all meetings from list that don't contain search query ignoring case
            meetingList.removeIf(meeting -> !meeting.getName().toLowerCase().contains(query.toLowerCase()));
    }
}
