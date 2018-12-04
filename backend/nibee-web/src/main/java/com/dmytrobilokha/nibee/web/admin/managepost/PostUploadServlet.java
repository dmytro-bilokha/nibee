package com.dmytrobilokha.nibee.web.admin.managepost;

import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet("/admin/post-upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2
        , maxFileSize = 1024 * 1024 * 50
        , maxRequestSize = 1024 * 1024 * 60
)
public class PostUploadServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "_UPLOADS_";
    private static final Logger LOGGER = LoggerFactory.getLogger(PostUploadServlet.class);

    private final ConfigService configService;
    private final Jsonb jsonb;
    private final FileService fileService;
    private final StatusMessageSender statusMessageSender;

    @Inject
    public PostUploadServlet(
            ConfigService configService
            , Jsonb jsonb
            , FileService fileService
            , StatusMessageSender statusMessageSender
    ) {
        this.configService = configService;
        this.jsonb = jsonb;
        this.fileService = fileService;
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
        Path postUploadDir = unzipPostFileToUploadDir(streamsContainer.fileStream, postData.getFsPath(), resp);
        if (postUploadDir == null) {
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
            statusMessageSender.sendClientErrorStatus("Post file part is mandatory, but got none", resp);
            return null;
        }
        if (postDataPart == null) {
            LOGGER.warn("Got request without post data part, rejecting it");
            statusMessageSender.sendClientErrorStatus("Post data part is mandatory, but got none", resp);
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

    private Path unzipPostFileToUploadDir(InputStream postFileInputStream, String fsPath, HttpServletResponse resp) {
        Path uploadPostDir = Paths.get(
                configService.getAsString(ConfigProperty.POSTS_ROOT)
                , UPLOAD_DIR
                , fsPath + '_' + UUID.randomUUID());
        try {
            fileService.unzipStreamToDir(postFileInputStream, uploadPostDir);
        } catch (IOException ex) {
            LOGGER.warn("Failed to unzip post content file, rejecting request", ex);
            statusMessageSender.sendClientErrorStatus(
                    "Failed to unpack post content zip file. Check if it is valid and try again"
                    , resp
            );
            return null;
        }
        return uploadPostDir;
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
