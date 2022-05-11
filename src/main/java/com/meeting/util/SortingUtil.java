package com.meeting.util;

import com.meeting.entitiy.Meeting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SortingUtil {

    private static final Logger log = LogManager.getLogger(SortingUtil.class);

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
        meetingList.sort((o1, o2) -> ((o1.getParticipants().size() + o1.getSpeakerTopics().size()) - o2.getParticipants().size() + o2.getSpeakerTopics().size()));
        log.debug("Meetings were sorted by number of participants");

        return listBeforeSorting.equals(meetingList) ? reverseList(meetingList) : meetingList;
    }

    private static List<Meeting> sortByNumberOfTopic(List<Meeting> meetingList) {
        List<Meeting> listBeforeSorting = new LinkedList<>(meetingList);

        meetingList.sort((o1, o2) -> {
            if (o1.getFreeTopics().size() == o2.getFreeTopics().size()) {
                return o1.getName().compareTo(o2.getName());
            } else {
                return o1.getFreeTopics().size() - o2.getFreeTopics().size();
            }
        });
        log.debug("Meetings were sorted by number of topics");
        return listBeforeSorting.equals(meetingList) ? reverseList(meetingList) : meetingList;
    }

    private static List<Meeting> sortByDate(List<Meeting> meetingList) {
        List<Meeting> listBeforeSorting = new LinkedList<>(meetingList);

        meetingList.sort((o1, o2) -> {
            if (o1.getDate().equals(o2.getDate())) {
                return o1.getTimeStart().compareTo(o2.getTimeStart());
            } else {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        log.debug("Meetings were sorted by date");
        return listBeforeSorting.equals(meetingList) ? reverseList(meetingList) : meetingList;
    }

    private static List<Meeting> sortByName(List<Meeting> meetingList) {
        List<Meeting> listBeforeSorting = new LinkedList<>(meetingList);
        meetingList.sort((Comparator.comparing(Meeting::getName)));
        log.debug("Meetings were sorted by name");
        return listBeforeSorting.equals(meetingList) ? reverseList(meetingList) : meetingList;
    }

    private static List<Meeting> reverseList(List<Meeting> meetingList) {
        Collections.reverse(meetingList);
        log.debug("Meetings order was reversed");
        return meetingList;
    }
}
