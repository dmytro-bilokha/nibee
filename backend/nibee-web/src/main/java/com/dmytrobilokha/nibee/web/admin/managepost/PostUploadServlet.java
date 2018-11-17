package com.dmytrobilokha.nibee.web.admin.managepost;

import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@WebServlet("/admin/post-upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2
        , maxFileSize = 1024 * 1024 * 50
        , maxRequestSize = 1024 * 1024 * 100
        , location = "/"
)
public class PostUploadServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "_UPLOADS_";

    private final ConfigService configService;
    private final Jsonb jsonb;

    @Inject
    public PostUploadServlet(ConfigService configService, Jsonb jsonb) {
        this.configService = configService;
        this.jsonb = jsonb;
    }

    //TODO: add parameters handling, validation (one file only, filetype, form fields, etc)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String blogBaseDir = configService.getAsString(ConfigProperty.POSTS_ROOT);
        for (Part part : req.getParts()) {
            String fileName = part.getSubmittedFileName();
            part.write(Paths.get(blogBaseDir, UPLOAD_DIR, fileName).toString());
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(MediaType.APPLICATION_JSON);
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PostMetadata postMetadata = new PostMetadata(
                "fileNameOnServer"
                , "dir"
                , "Title"
                , "webpath"
        );
        resp.getWriter().print(jsonb.toJson(postMetadata));
    }

}
