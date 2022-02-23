package com.meeting.util;

import com.meeting.entitiy.Meeting;
import com.meeting.exception.DataBaseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DisplayedMeetingUtilTest {

    private Meeting meetingA;
    private Meeting meetingB;
    private Meeting meetingC;

    @BeforeEach
    void setUp() {
        meetingA = new Meeting();
        meetingB = new Meeting();
        meetingC = new Meeting();
    }

    @AfterEach
    void tearDown() {
        meetingA = null;
        meetingB = null;
        meetingC = null;
    }

    @Test
    void shouldShowAllMeetings() throws DataBaseException {
        List<Meeting> meetingList = new LinkedList<>(Arrays.asList(meetingA, meetingB, meetingC));
        DisplayedMeetingUtil.displayMeetings(meetingList, "all");

        int expectedSize = 3;
        int actualSize = meetingList.size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void shouldShowFutureMeetings() throws DataBaseException {
        meetingA.setStarted(true);
        meetingB.setGoingOnNow(true);

        List<Meeting> meetingList = new LinkedList<>(Arrays.asList(meetingA, meetingB, meetingC));
        DisplayedMeetingUtil.displayMeetings(meetingList, "notStarted");

        int expectedSize = 1;
        int actualSize = meetingList.size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void shouldShowPastMeetings() throws DataBaseException {
        meetingA.setPassed(true);
        meetingC.setPassed(true);

        List<Meeting> meetingList = new LinkedList<>(Arrays.asList(meetingA, meetingB, meetingC));

        DisplayedMeetingUtil.displayMeetings(meetingList, "passed");

        int expectedSize = 2;
        int actualSize = meetingList.size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void showMeetingsWhichAreGoingOnRightNow() throws DataBaseException {
        meetingA.setGoingOnNow(true);

        List<Meeting> meetingList = new LinkedList<>(Arrays.asList(meetingA, meetingB, meetingC));

        DisplayedMeetingUtil.displayMeetings(meetingList, "goingOnNow");

        int expectedSize = 1;
        int actualSize = meetingList.size();

        assertEquals(expectedSize, actualSize);

    }

    @Test
    void shouldReturnNull() throws DataBaseException {
        List<Meeting> meetingList = new LinkedList<>(Arrays.asList(meetingA, meetingB, meetingC));
        String unhandledDisplayOption = "unhandled";

        List<Meeting> result = DisplayedMeetingUtil.displayMeetings(meetingList, unhandledDisplayOption);

        assertNull(result);
    }
}