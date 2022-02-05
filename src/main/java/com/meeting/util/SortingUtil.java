package com.meeting.util;

import com.meeting.entitiy.Meeting;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SortingUtil {

    /**
     * Sorts meeting list
     *
     * @param meetingList meetings from current session.
     * @param sortMethod method of sorting.
     * @return sorted meeting list by {@code sortMethod}
     */
    public static List<Meeting> sortByParameter (List<Meeting> meetingList, String sortMethod) {
        switch (sortMethod) {
            case "name":
                return sortByName(meetingList);
            case "date" :
                return sortByDate(meetingList);
            case "topics" :
                return sortByNumberOfTopic(meetingList);
            case "participants" :
                return sortByNumberOfParticipants(meetingList);
            default:
                return meetingList;
        }
    }

    private static List<Meeting> sortByNumberOfParticipants(List<Meeting> meetingList) {
        List<Meeting> listBeforeSorting = new LinkedList<>(meetingList);
        meetingList.sort((o1, o2) -> o2.getParticipants().size() - o1.getParticipants().size());

        return listBeforeSorting.equals(meetingList) ? reverseList(meetingList) : meetingList;
    }

    private static List<Meeting> sortByNumberOfTopic(List<Meeting> meetingList) {
        List<Meeting> listBeforeSorting = new LinkedList<>(meetingList);

        meetingList.sort((o1, o2) -> {
            if (o1 == o2) {
                return o1.getName().compareTo(o2.getName());
            } else {
                return o2.getFreeTopics().size() - o1.getFreeTopics().size();
            }
        });
        return listBeforeSorting.equals(meetingList) ? reverseList(meetingList) : meetingList;
    }

    private static List<Meeting> sortByDate(List<Meeting> meetingList) {
        List<Meeting> listBeforeSorting = new LinkedList<>(meetingList);

        meetingList.sort((o1, o2) -> {
            if (o1.getDate().equals(o2.getDate())) {
                return o2.getTime().compareTo(o1.getTime());
            } else {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        return listBeforeSorting.equals(meetingList) ? reverseList(meetingList) : meetingList;
    }

    private static List<Meeting> sortByName(List<Meeting> meetingList) {
        List<Meeting> listBeforeSorting = new LinkedList<>(meetingList);
        meetingList.sort((Comparator.comparing(Meeting::getName)));

        return listBeforeSorting.equals(meetingList) ? reverseList(meetingList) : meetingList;
    }

    private static List<Meeting> reverseList(List<Meeting> meetingList) {
        Collections.reverse(meetingList);
        return meetingList;
    }
}
