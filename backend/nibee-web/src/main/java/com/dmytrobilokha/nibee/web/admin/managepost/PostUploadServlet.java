package com.dmytrobilokha.nibee.web.admin.managepost;

import com.dmytrobilokha.nibee.service.post.IllegalPostDataException;
import com.dmytrobilokha.nibee.service.post.PostService;
import com.dmytrobilokha.nibee.web.errorhandling.InvalidClientDataException;
import com.dmytrobilokha.nibee.web.errorhandling.StatusMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/admin/post-upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2
        , maxFileSize = 1024 * 1024 * 50
        , maxRequestSize = 1024 * 1024 * 60
)
public class PostUploadServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostUploadServlet.class);

    private final Jsonb jsonb;
    private final PostService postService;
    private final StatusMessageSender statusMessageSender;

    @Inject
    public PostUploadServlet(
            Jsonb jsonb
            , PostService postService
            , StatusMessageSender statusMessageSender
    ) {
        this.jsonb = jsonb;
        this.postService = postService;
        this.statusMessageSender = statusMessageSender;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        PostFormStreamsContainer streamsContainer = extractFormStreamsOrSendError(req, resp);
        if (streamsContainer == null) {
            return;
        }
        PostCreateDto postData = deserializeAndValidatePostCreateDto(streamsContainer.jsonStream, resp);
        if (postData == null) {
            return;
        }
        try {
            postService.createPost(streamsContainer.fileStream, postData.toPost(), postData.tagIdSet());
        } catch (IllegalPostDataException ex) {
            LOGGER.warn("Failed to create a post because of invalid data provided: {}", postData, ex);
            statusMessageSender.sendClientErrorStatus(ex.getMessage(), resp);
            return;
        } catch (Exception ex) {
            //No need to log exception from the EJB, it will be logged by the EJB container
            statusMessageSender.sendServerProblemStatus("Internal server error", resp);
            return;
        }
        statusMessageSender.sendOkStatus("New post has been created", resp);
    }

    private PostFormStreamsContainer extractFormStreamsOrSendError(HttpServletRequest req, HttpServletResponse resp) {
        Part filePart;
        Part postDataPart;
        try {
            filePart = req.getPart("file");
            postDataPart = req.getPart("postData");
        } catch (ServletException ex) {
            LOGGER.warn("Failed to extract form parts from http request because of invalid request, rejecting it", ex);
            statusMessageSender.sendClientErrorStatus(
                    "Request should be of type multipart/form-data, but it is not"
                    , resp
            );
            return null;
        } catch (IllegalStateException ex) {
            LOGGER.warn("Got request with too big size, rejecting it", ex);
            statusMessageSender.sendClientErrorStatus("Request of file size is too big", resp);
            return null;
        } catch (IOException ex) {
            LOGGER.error("Failed to get form data from the request", ex);
            statusMessageSender.sendServerProblemStatus("Server is unable to extract data from the request", resp);
            return null;
        }
        if (filePart == null) {
            LOGGER.warn("Got request without post zip file part, rejecting it");
            statusMessageSender.sendClientErrorStatus("PostWithTags file part is mandatory, but got none", resp);
            return null;
        }
        if (postDataPart == null) {
            LOGGER.warn("Got request without post data part, rejecting it");
            statusMessageSender.sendClientErrorStatus("PostWithTags data part is mandatory, but got none", resp);
            return null;
        }
        InputStream postDataInputStream;
        InputStream postFileInputStream;
        try {
            postDataInputStream = postDataPart.getInputStream();
            postFileInputStream = filePart.getInputStream();
        } catch (IOException ex) {
            LOGGER.error("Failed to get input stream from the form post request part", ex);
            statusMessageSender.sendServerProblemStatus(
                    "Server is failed to extract data from the request"
                    , resp
            );
            return null;
        }
        return new PostFormStreamsContainer(postFileInputStream, postDataInputStream);
    }

    private PostCreateDto deserializeAndValidatePostCreateDto(
            InputStream postDataInputStream, HttpServletResponse resp) {
        PostCreateDto postData;
        try {
            postData = jsonb.fromJson(postDataInputStream, PostCreateDto.class);
        } catch (JsonbException ex) {
            LOGGER.warn("Failed to deserialize post data from json, rejecting request", ex);
            statusMessageSender.sendClientErrorStatus("Server unable to parse json message with post data", resp);
            return null;
        }
        try {
            postData.validate();
        } catch (InvalidClientDataException ex) {
            LOGGER.warn("Got invalid {}, rejecting request with {}", postData, ex.getStatusMessage());
            statusMessageSender.sendClientErrorStatus(ex, resp);
            return null;
        }
        return postData;
    }

    private static class PostFormStreamsContainer {
        private final InputStream fileStream;
        private final InputStream jsonStream;

        private PostFormStreamsContainer(InputStream filePart, InputStream dataPart) {
            this.fileStream = filePart;
            this.jsonStream = dataPart;
        }
    }

}
