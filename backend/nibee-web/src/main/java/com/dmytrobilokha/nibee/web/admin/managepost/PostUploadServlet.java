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
import java.io.File;
import java.io.IOException;

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String blogBaseDir = configService.getAsString(ConfigProperty.POSTS_ROOT);
        String uploadPath = blogBaseDir + File.separator + UPLOAD_DIR;
        for (Part part : req.getParts()) {
            String fileName = part.getSubmittedFileName();
            part.write(uploadPath + File.separator + fileName);
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(MediaType.APPLICATION_JSON);
        PostMetadata postMetadata = new PostMetadata(
                "fileNameOnServer"
                , "dir"
                , "Title"
                , "webpath"
        );
        resp.getWriter().print(jsonb.toJson(postMetadata));
    }

}
