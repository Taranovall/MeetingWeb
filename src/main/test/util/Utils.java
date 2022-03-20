package util;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Speaker;
import com.meeting.entitiy.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.meeting.entitiy.Role.*;

public class Utils {

    private Utils() {
    }

    public static String generateStringWithRandomChars(int length) {
        Random r = new Random();
        return r.ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public static User createUser() {
        User user = new User(4L, "User", "1");
        user.setRegistrationDate("14.02.2022");
        user.setRole(USER);
        return user;
    }

    public static Meeting createMeeting() {
        return new Meeting(2L,
                "InvestPro Кипр Никосия 2022",
                "2022-06-20",
                "15:30",
                "19:30",
                "Hilton Nicosia",
                "/image/123qq.jpeg");
    }

    public static User createUserWithRoleSpeaker() {
        User user = new User(228L, "very talkative speaker", "1");
        user.setRegistrationDate("13.02.2022");
        user.setRole(SPEAKER);
        return user;
    }

    public static User createUserWithRoleModerator() {
        User user = createUser();
        user.setRole(MODERATOR);
        return user;
    }

    public static Speaker createSpeaker() {
        return new Speaker(228L, "very talkative speaker");
    }

    public static List<Meeting> createListWithMeetings() {
        List<Meeting> meetings = new LinkedList<>();
        Meeting meeting = createMeeting();

        meetings.add(meeting);

        meeting = createMeeting();
        meeting.setId(7L);
        meeting.setName("Meeting");
        meeting.setDate("11.02.2022");

        meetings.add(meeting);

        return meetings;
    }
}
