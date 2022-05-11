package com.meeting.util;

public class Constant {

    // paths
    public static final String PATH_TO_REGISTRATION_JSP = "/view/auth/registration.jsp";
    public static final String PATH_TO_LOGIN_JSP = "/view/auth/login.jsp";
    public static final String PATH_TO_MAIN_PAGE_JSP = "/view/meeting/mainPage.jsp";
    public static final String PATH_TO_CREATE_MEETING_FIRST_PAGE_JSP = "/view/meeting/createMeetingFirstPage.jsp";
    public static final String PATH_TO_CREATE_MEETING_SECOND_PAGE_JSP = "/view/meeting/createMeetingSecondPage.jsp";
    public static final String PATH_TO_MEETING_INFO_PAGE = "/view/meeting/meetingInformation.jsp";
    public static final String PATH_TO_PERSONAL_ACCOUNT_JSP = "/view/personalAccount.jsp";
    public static final String PATH_TO_ERROR_JSP = "/view/errorPage.jsp";

    // Attribute names
    public static final String MEETING_ATTRIBUTE_NAME = "meetings";
    public static final String SORT_METHOD_ATTRIBUTE_NAME = "sortMethod";
    public static final String IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME = "isFormHasBeenUsed";
    public static final String QUERY_IS_NOT_VALID_ATTRIBUTE_NAME = "queryIsNotValid";
    public static final String MESSAGE = "message";
    public static final String LOGIN_ERROR = "loginError";
    public static final String PASSWORD_ERROR = "passwordError";
    public static final String ERROR = "error";
    public static final String COUNT_OF_TOPICS = "countOfTopics";
    public static final String FIRST_PAGE_URL = "firstPageURL";
    public static final String SPEAKERS = "speakers";
    public static final String LAST_PAGE_URI = "lastPageURI";

    // errors
    public static final String COUNT_OF_TOPICS_IS_NULL = "error.countOfTopicsCannotBeNull";
    public static final String COUNT_OF_TOPICS_IS_NOT_VALID = "error.countOfTopicsMustContainOnlyNumbers";
    public static final String DATE_AND_TIME_NOT_VALID = "error.incorrectDateAndTime";
    public static final String DATE_NOT_VALID = "error.dateNotValid";
    public static final String INCORRECT_PASSWORD = "error.incorrectPassword";
    public static final String INVALID_EMAIL = "error.invalidEmail";
    public static final String INVALID_COUNT_OF_TOPICS = "error.invalidCountOfTopics";
    public static final String LOGIN_IS_NOT_VALID = "error.invalidLogin";
    public static final String MEETING_NAME_IS_NOT_VALID = "error.invalidMeetingName";
    public static final String OPTION_NOT_SELECTED = "error.optionNotSelected";
    public static final String PASSWORD_NOT_CONTAIN_ANY_DIGIT = "error.passwordNotContainAnyDigit";
    public static final String PASSWORD_LENGTH_NOT_VALID = "error.incorrectPasswordLength";
    public static final String PASSWORD_NOT_MATCH = "error.passwordsDoNotMatch";
    public static final String PHOTO_NOT_CHOSEN = "error.photoNotChosen";
    public static final String QUERY_IS_EMPTY = "error.queryEmpty";
    public static final String QUERY_IS_TOO_LONG = "error.queryTooLong";
    public static final String TIME_NOT_VALID = "error.invalidTime";
    public static final String TOPIC_IS_TOO_LONG = "error.incorrectTopicLength";
    public static final String TOPIC_NAME_NOT_UNIQUE = "error.topicNameNotUnique";
    public static final String USER_NOT_EXIST = "error.userNotExist";
    public static final String PASSWORD_NOT_CONTAIN_ANY_LETTER = "error.passwordNotContainAnyLetter";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String PASSWORD_CONFIRM = "passwordConfirm";
    public static final String TOPIC_IS_ALREADY_EXIST = "error.topicAlreadyExist";

    //exception messages
    public static final String CANNOT_GET_ALL_SPEAKERS = "Cannot get all speakers";
    public static final String CANNOT_GET_TOPIC_BY_NAME = "Cannot get topic by name: ";
    public static final String CANNOT_CREATE_MEETING = "Cannot create meeting";
    public static final String CANNOT_GET_TOPIC_BY_HIS_ID = "Cannot get topic by his ID: ";
    public static final String CANNOT_GET_ALL_FREE_TOPICS_BY_MEETING_ID = "Cannot get all free topics by meeting ID: ";
    public static final String MEETING_WITH_THIS_ID_NOT_EXIST = "Meeting doesn't exist. ID - ";
    public static final String CANNOT_PROPOSE_TOPIC = "Cannot propose topic";
    public static final String CANNOT_ACCEPT_PROPOSED_TOPIC = "Cannot accept proposed topic";
    public static final String CANNOT_CANCEL_PROPOSED_TOPICS = "Cannot cancel proposed topics";
    public static final String CANNOT_GET_ALL_MEETINGS = "Cannot get all meetings";
    public static final String CANNOT_GET_MEETINGS_IN_WHICH_SPEAKER_IS_INVOLVED = "Cannot get meetings in which speaker is involved";
    public static final String CANNOT_GET_SPEAKER_BY_ID = "Cannot get speaker by ID: ";
    public static final String CANNOT_GET_ACCEPTED_TOPICS_BY_MEETING_ID = "Cannot get accepted topics by meeting id ";
    public static final String CANNOT_GET_SENT_APPLICATIONS_BY_MEETING_ID = "Cannot get sent applications by meeting ID ";
    public static final String CANNOT_GET_PROPOSED_TOPICS_BY_SPEAKER_FOR_MEETING_WITH_ID = "Cannot get proposed topics by speaker for meeting with ID: ";
    public static final String CANNOT_ACCEPT_APPLICATION = "Cannot accept application";
    public static final String CANNOT_UPDATE_INFORMATION = "Cannot update information";
    public static final String CANNOT_GET_PARTICIPANTS_OF_THIS_MEETING_BY_HIS_ID = "Cannot get participants of this meeting by his ID";
    public static final String CANNOT_MARK_PRESENT_USERS = "Cannot mark present users";
    public static final String CANNOT_GET_PRESENT_USERS = "Cannot get present users";
    public static final String CANNOT_CALCULATE_PERCENTAGE = "Cannot calculate percentage";
    public static final String CANNOT_GET_SENT_APPLICATIONS_BY_SPEAKER_BY_HIS_ID = "Cannot get sent applications by speaker by his ID: ";
    public static final String CANNOT_GET_RECEIVED_BY_SPEAKER_APPLICATIONS_BY_HIS_ID = "Cannot get received by speaker applications by his ID: ";
    public static final String CANNOT_ACCEPT_INVITATION = "Cannot accept invitation";
    public static final String CANNOT_CANCEL_INVITATION = "Cannot cancel invitation";
    public static final String CANNOT_SEND_APPLICATION = "Cannot send application";
    public static final String CANNOT_REMOVE_APPLICATION = "Cannot remove application";
    public static final String CANNOT_GET_SPEAKER_BY_MEETING_ID = "Cannot get speaker by meeting ID ";
    public static final String CANNOT_SIGN_UP_USER = "Cannot sign up user";
    public static final String CANNOT_GET_USER_BY_LOGIN = "Cannot get user by login: ";
    public static final String CANNOT_GET_USER_BY_ID = "Cannot get user by ID: ";
    public static final String USER_CANNOT_PARTICIPATE_ID = "User cannot participate. ID - ";
    public static final String USER_CANNOT_STOP_PARTICIPATING_ID = "User cannot stop participating. ID: ";
}
