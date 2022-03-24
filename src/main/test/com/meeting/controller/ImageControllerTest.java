package com.meeting.controller;

import com.meeting.service.UserService;
import com.meeting.service.ValidationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageControllerTest {

    private ImageController imageController;
    @Mock
    UserService userService;
    @Mock
    ValidationService validationService;
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private HttpSession session;
    @Mock
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        imageController = new ImageController();
        userService = mock(UserService.class);
        validationService = mock(ValidationService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        imageController = null;
        validationService = null;
        userService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldDisplayImage() {
        try {
            String imgName = "/Image001.png";
            ServletOutputStream out = mock(ServletOutputStream.class);
            ServletContext cntx = mock(ServletContext.class);

            when(req.getServletContext()).thenReturn(cntx);
            when(cntx.getMimeType(anyString())).thenReturn("image/jpeg");
            when(req.getPathInfo()).thenReturn(imgName);
            when(resp.getOutputStream()).thenReturn(out);

            imageController.doGet(req, resp);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void shouldThrowIllegalArgumentException() throws IOException, ServletException {
        String imgName = "/document.png";

        when(req.getPathInfo()).thenReturn(imgName);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> imageController.doGet(req, resp));

        String expected = "File 'document.png' not found!";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }
}