ALTER TABLE post_tag ADD CONSTRAINT post_tag_post_id_fk FOREIGN KEY (post_id) REFERENCES post (id);
ALTER TABLE post_tag ADD CONSTRAINT post_tag_tag_id_fk FOREIGN KEY (tag_id) REFERENCES tag (id);
