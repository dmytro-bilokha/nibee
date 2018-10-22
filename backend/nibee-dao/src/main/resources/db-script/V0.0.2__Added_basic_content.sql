INSERT INTO post (name, title, path) VALUES ('resources', 'resources', 'resources');
INSERT INTO post (name, title, path) VALUES ('about', 'About', 'about');
INSERT INTO post (name, title, path) VALUES ('terms-of-use', 'Terms of Use', 'terms-of-use');
INSERT INTO post (name, title, path) VALUES ('test', 'Blog Appearance Test', 'appearance-test');

INSERT INTO tag (name) VALUES ('Java');
INSERT INTO tag (name) VALUES ('FreeBSD');
INSERT INTO tag (name) VALUES ('Linux');
INSERT INTO tag (name) VALUES ('CI/CD');

INSERT INTO post_tag (post_id, tag_id) VALUES (4, 1);
INSERT INTO post_tag (post_id, tag_id) VALUES (4, 2);
INSERT INTO post_tag (post_id, tag_id) VALUES (4, 3);
INSERT INTO post_tag (post_id, tag_id) VALUES (4, 4);

