INSERT INTO post (id, name, path) VALUES
  (1, 'post-about-rest', '2018/01/REST')
, (2, 'post-about-soap', '2018/02/SOAP')
, (3, 'resources', 'resources')
;

INSERT INTO tag (id, name) VALUES
  (1, 'web service')
, (2, 'REST')
, (3, 'SOAP')
;

INSERT INTO post_tag (post_id, tag_id) VALUES
  (1, 1)
, (2, 1)
, (1, 2)
, (2, 3)
;

