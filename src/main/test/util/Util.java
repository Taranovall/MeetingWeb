package util;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Speaker;
import com.meeting.entitiy.User;
import com.meeting.service.MeetingService;
import com.meeting.service.impl.MeetingServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.meeting.entitiy.Role.MODERATOR;
import static com.meeting.entitiy.Role.SPEAKER;
import static com.meeting.entitiy.Role.USER;

public class Util {

    private Util() {
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
        User user = new User(new Random().nextLong(), generateStringWithRandomChars(15), generateStringWithRandomChars(15));
        user.setRegistrationDate("14.02.2022");
        user.setRole(USER);
        return user;
    }

    public static Meeting createMeeting() {
        Long id = Math.round(Math.random() * 100);
        String name = generateStringWithRandomChars(5);
        String date = generateRandomData();
        String timeStart = generateRandomTime(true);
        String timeEnd = generateRandomTime(false);
        String place = generateStringWithRandomChars(15);
        String photoPath = generateStringWithRandomChars(10);

        Meeting meeting = new Meeting(id, name, date, timeStart, timeEnd, place, photoPath);

        MeetingService meetingService = new MeetingServiceImpl();
        meeting.setGoingOnNow(meetingService.isMeetingGoingOnNow(meeting));
        meeting.setPassed(meetingService.isMeetingPassed(meeting));
        meeting.setStarted(meetingService.isMeetingStarted(meeting));

        return meeting;
    }


    public static User createUserWithRoleSpeaker() {
        User user = createUser();
        user.setRole(SPEAKER);
        return user;
    }

    public static User createUserWithRoleModerator() {
        User user = createUser();
        user.setRole(MODERATOR);
        return user;
    }

    public static Speaker createSpeaker() {
        return new Speaker(new Random().nextLong(), generateStringWithRandomChars(15));
    }

    public static List<Meeting> createListWithMeetings() {
        return Arrays.asList(createMeeting(), createMeeting());
    }

    private static String generateRandomTime(boolean isBeforeMidday) {
        Random rnd = new Random();
        int secInHalfDay = 12 * 60 * 59;
        long randomSecond = rnd.nextInt(secInHalfDay);
        LocalTime time = isBeforeMidday ? LocalTime.MIN.plusSeconds(randomSecond) : LocalTime.MAX.minusSeconds(randomSecond);DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        return dtf.format(time);
    }

    private static String generateRandomData() {
        Random rnd = new Random();
        int minDay = (int) LocalDate.of(2022, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2022, 12, 31).toEpochDay();

        long randomDay = minDay + rnd.nextInt(maxDay - minDay);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.ofEpochDay(randomDay);

        return dtf.format(date);
    }
}
