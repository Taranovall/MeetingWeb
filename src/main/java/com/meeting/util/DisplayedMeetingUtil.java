package com.meeting.util;

import com.meeting.entitiy.Meeting;
import com.meeting.exception.DataBaseException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DisplayedMeetingUtil {

    /**
     * Displays meetings by option
     *
     * @param meetingList   meetings from current session.
     * @param displayOption display meeting with this option.
     * @return list with meeting which are matches {@code displayOption}
     */
    public static List<Meeting> displayMeetings(List<Meeting> meetingList, String displayOption) throws DataBaseException {
        switch (displayOption) {
            case "all":
                return meetingList;
            case "goingOnNow":
                return isGoingOnRightNow(meetingList);
            case "passed":
                return pastMeetings(meetingList);
            case "notStarted":
                return futureMeetings(meetingList);
            default:
                return null;
        }
    }

    /**
     * leaves in list only future meetings
     */
    private static List<Meeting> futureMeetings(List<Meeting> meetingList) {
        CopyOnWriteArrayList<Meeting> meetings = new CopyOnWriteArrayList<>(meetingList);
        meetings.forEach(meeting -> {
            if (meeting.isStarted() || meeting.isGoingOnNow()) meetings.remove(meeting);
        });
        return meetings;
    }

    /**
     * leaves in list only past meetings
     */
    private static List<Meeting> pastMeetings(List<Meeting> meetingList) {
        CopyOnWriteArrayList<Meeting> meetings = new CopyOnWriteArrayList<>(meetingList);
        meetings.forEach(meeting -> {
            if (!meeting.isPassed()) meetings.remove(meeting);
        });
        return meetings;
    }

    /**
     * leaves in list meetings which are going on right now
     */
    private static List<Meeting> isGoingOnRightNow(List<Meeting> meetingList) {
        CopyOnWriteArrayList<Meeting> meetings = new CopyOnWriteArrayList<>(meetingList);
        meetings.forEach(meeting -> {
            if (!meeting.isGoingOnNow()) meetings.remove(meeting);
        });
        return meetings;
    }
}
