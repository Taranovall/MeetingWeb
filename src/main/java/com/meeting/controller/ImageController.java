package com.meeting.controller;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@WebServlet(name = "image", urlPatterns = "/image/*")
public class ImageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String imageFileName = req.getPathInfo();
        InputStream is = getFileFromResourceAsStream(imageFileName);

        ServletContext cntx = req.getServletContext();
        // retrieve mimeType dynamically
        String mime = cntx.getMimeType(imageFileName);
        if (mime == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        resp.setContentType(mime);
        resp.setContentLength(is.available());

        Path temp = Files.createTempFile("temp-", ".ext");
        Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);

        try (FileInputStream in = new FileInputStream(temp.toFile());
             OutputStream out = resp.getOutputStream()) {

            // Copy the contents of the file to the output stream
            byte[] buf = new byte[1024];
            int count = 0;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
        }
    }


    /**
     * @param imgName name of the image
     * @return image as InputStream
     */
    private InputStream getFileFromResourceAsStream(String imgName) {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        // converts image which is located in target/classes/images and has name imgName into InputStream
        InputStream inputStream = classLoader.getResourceAsStream("images" + imgName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + imgName);
        } else {
            return inputStream;
        }

    }
}
