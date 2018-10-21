CREATE TABLE post
( id BIGINT NOT NULL AUTO_INCREMENT
, name VARCHAR(120) NOT NULL
, title VARCHAR(120)
, path VARCHAR(512) NOT NULL
, created_on DATETIME DEFAULT CURRENT_TIMESTAMP
, modified_on DATETIME DEFAULT NULL
, CONSTRAINT post_pk PRIMARY KEY (id)
, CONSTRAINT post_path_uq UNIQUE (path)
, CONSTRAINT post_name_uq UNIQUE INDEX post_name_idx (name)
);

CREATE TABLE tag
( id BIGINT NOT NULL AUTO_INCREMENT
, name VARCHAR(64) NOT NULL
, CONSTRAINT tag_pk PRIMARY KEY (id)
, CONSTRAINT tag_name_uq UNIQUE INDEX tag_name_idx (name)
);

CREATE TABLE post_tag
( post_id BIGINT NOT NULL
, tag_id BIGINT NOT NULL
, CONSTRAINT post_tag_pk PRIMARY KEY (post_id, tag_id)
, CONSTRAINT post_tag_post_id_fk FOREIGN KEY (post_id) REFERENCES post (id)
, CONSTRAINT post_tag_tag_id_fk FOREIGN KEY (tag_id) REFERENCES tag (id)
);

CREATE TABLE post_comment
( id BIGINT NOT NULL AUTO_INCREMENT
, post_id BIGINT NOT NULL
, parent_comment_id BIGINT
, author_nickname VARCHAR(25)
, content VARCHAR(5000)
, created_on DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)
, CONSTRAINT post_comment_pk PRIMARY KEY (id)
, CONSTRAINT post_comment_post_id_fk FOREIGN KEY (post_id) REFERENCES post (id)
, CONSTRAINT post_comment_parent_comment_id_fk FOREIGN KEY (parent_comment_id) REFERENCES post_comment (id)
);

CREATE TABLE web_log
( id BIGINT NOT NULL AUTO_INCREMENT
, uuid BINARY(16)
, session_id VARCHAR(100)
, server_port SMALLINT UNSIGNED NOT NULL
, request_uri VARCHAR(1024) NOT NULL
, query_string VARCHAR(1024)
, referer VARCHAR(2048)
, user_agent VARCHAR(2048)
, accept_encoding VARCHAR(2048)
, created_on DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)
, client_port SMALLINT UNSIGNED NOT NULL
, client_ip VARBINARY(16) NOT NULL
, CONSTRAINT web_log_pk PRIMARY KEY (id)
);

