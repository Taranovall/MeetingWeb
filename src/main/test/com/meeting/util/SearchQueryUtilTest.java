package com.meeting.util;

import com.meeting.entitiy.Meeting;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchQueryUtilTest {

    @Test
    void shouldReturnMeetingsWithSpecificNames() {
        Meeting firstMeeting = new Meeting("FirstMeeting");
        Meeting secondMeeting = new Meeting("secondMeeting");

        List<Meeting> meetingList = new ArrayList<>(Arrays.asList(firstMeeting, secondMeeting));
        String query = "first";

        SearchQueryUtil.executeQuery(meetingList, query);

        List<Meeting> expectedList = new ArrayList<>(Arrays.asList(firstMeeting));

        assertEquals(expectedList, meetingList);
    }
}