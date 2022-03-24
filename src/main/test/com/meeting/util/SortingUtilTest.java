package com.meeting.util;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Topic;
import com.meeting.entitiy.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortingUtilTest {

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
    void shouldSortListByName() {
        meetingA.setName("a");
        meetingB.setName("b");
        meetingC.setName("c");

        List<Meeting> list = new ArrayList<>(Arrays.asList(meetingB, meetingA, meetingC));

        SortingUtil.sortByParameter(list, "name");

        List<Meeting> expectedList = new ArrayList<>(Arrays.asList(meetingA, meetingB, meetingC));

        assertEquals(expectedList, list);
    }

    @Test
    void shouldSortByNumberOfParticipants() {
        meetingA.setParticipants(new LinkedList<>(Arrays.asList(
                new User(Util.generateStringWithRandomChars(8)),
                new User(Util.generateStringWithRandomChars(8)),
                new User(Util.generateStringWithRandomChars(8)))));

        meetingB.setParticipants(new LinkedList<>(Arrays.asList(
                new User(Util.generateStringWithRandomChars(8)),
                new User(Util.generateStringWithRandomChars(8)),
                new User(Util.generateStringWithRandomChars(8)),
                new User(Util.generateStringWithRandomChars(8)),
                new User(Util.generateStringWithRandomChars(8)))));

        meetingC.setParticipants(new LinkedList<>(Arrays.asList(
                new User(Util.generateStringWithRandomChars(8)),
                new User(Util.generateStringWithRandomChars(8)),
                new User(Util.generateStringWithRandomChars(8)),
                new User(Util.generateStringWithRandomChars(8)))));

        List<Meeting> list = new LinkedList<>(Arrays.asList(meetingA, meetingB, meetingC));

        List<Meeting> expectedList = new LinkedList<>(Arrays.asList(meetingB, meetingC, meetingA));
        List<Meeting> actualList = SortingUtil.sortByParameter(list, "participants");

        assertEquals(expectedList, actualList);
    }

    @Test
    void shouldSortMeetingsByDate() {
        meetingA.setDate("22-04-30");
        meetingB.setDate("22-04-30");
        meetingC.setDate("22-09-30");
        meetingA.setTimeStart("19:40");
        meetingB.setTimeStart("20:00");

        List<Meeting> list = new LinkedList<>(Arrays.asList(meetingA, meetingB, meetingC));

        List<Meeting> expectedList = new LinkedList<>(Arrays.asList(meetingC, meetingB, meetingA));
        List<Meeting> actualList = SortingUtil.sortByParameter(list, "date");

        assertEquals(expectedList, actualList);
    }

    @Test
    void shouldSortByNumberOfTopics() {
        meetingA.setFreeTopics(new HashSet<>(Arrays.asList(
                new Topic(1L, Util.generateStringWithRandomChars(5)),
                new Topic(2L, Util.generateStringWithRandomChars(5))
        )));
        meetingA.setName("TheFirst");
        meetingB.setFreeTopics(new HashSet<>(Arrays.asList(
                new Topic(3L, Util.generateStringWithRandomChars(5)),
                new Topic(4L, Util.generateStringWithRandomChars(5))
        )));
        meetingB.setName("TheSecond");
        meetingC.setFreeTopics(new HashSet<>(Arrays.asList(
                new Topic(6L, Util.generateStringWithRandomChars(5)),
                new Topic(7L, Util.generateStringWithRandomChars(5)),
                new Topic(8L, Util.generateStringWithRandomChars(5)),
                new Topic(9L, Util.generateStringWithRandomChars(5))
        )));

        List<Meeting> list = new LinkedList<>(Arrays.asList(meetingA, meetingB, meetingC));

        List<Meeting> expectedList = new LinkedList<>(Arrays.asList(meetingC, meetingA, meetingB));
        List<Meeting> actualList = SortingUtil.sortByParameter(list, "topics");

        assertEquals(expectedList, actualList);

    }

    @Test
    void shouldDoNothing() {
        String unhandledSortMethod = "byProposedTopics";
        List<Meeting> meetings = new ArrayList<>();
        SortingUtil.sortByParameter(meetings, unhandledSortMethod);

        List<Meeting> expectedList = new ArrayList<>();

        assertEquals(expectedList, meetings);
    }
}