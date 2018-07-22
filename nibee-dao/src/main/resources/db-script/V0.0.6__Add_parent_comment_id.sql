ALTER TABLE post_comment ADD parent_comment_id BIGINT;
ALTER TABLE post_comment ADD CONSTRAINT post_comment_parent_comment_id_fk FOREIGN KEY (parent_comment_id) REFERENCES post_comment (id);

