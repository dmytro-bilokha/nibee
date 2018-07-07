CREATE TABLE post_comment
( id BIGINT NOT NULL AUTO_INCREMENT
, post_id BIGINT NOT NULL
, author_nickname VARCHAR(25)
, content VARCHAR(5000)
, created_on DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)
, CONSTRAINT post_comment_pk PRIMARY KEY (id)
, CONSTRAINT post_comment_post_id_fk FOREIGN KEY (post_id) REFERENCES post (id)
);

