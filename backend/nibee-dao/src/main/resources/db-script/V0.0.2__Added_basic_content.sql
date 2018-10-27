INSERT INTO post (name, title, path, shareable, comment_allowed) VALUES ('resources', 'resources', 'resources', 0, 0);
INSERT INTO post (name, title, path, shareable, comment_allowed) VALUES ('about', 'About', 'about', 0, 0);
INSERT INTO post (name, title, path, shareable, comment_allowed) VALUES ('terms-of-use', 'Terms of Use', 'terms-of-use', 0, 0);
INSERT INTO post (name, title, path, shareable, comment_allowed) VALUES ('test', 'Blog Appearance Test', 'appearance-test', 1, 1);

INSERT INTO tag (name) VALUES ('Java');
INSERT INTO tag (name) VALUES ('FreeBSD');
INSERT INTO tag (name) VALUES ('Linux');
INSERT INTO tag (name) VALUES ('CI/CD');

INSERT INTO post_tag (post_id, tag_id) VALUES (4, 1);
INSERT INTO post_tag (post_id, tag_id) VALUES (4, 2);
INSERT INTO post_tag (post_id, tag_id) VALUES (4, 3);
INSERT INTO post_tag (post_id, tag_id) VALUES (4, 4);

