INSERT INTO post (id, name, path, created_on, modified_on) VALUES
  (1, 'post-about-rest', '2018/01/REST', PARSEDATETIME('2018-04-13 12:00:00', 'yyyy-MM-dd hh:mm:ss'), NULL)
, (2, 'post-about-soap', '2018/02/SOAP', PARSEDATETIME('2018-04-11 16:42:00', 'yyyy-MM-dd hh:mm:ss'), PARSEDATETIME('2018-04-15 10:02:09', 'yyyy-MM-dd hh:mm:ss'))
, (3, 'resources', 'resources', PARSEDATETIME('2018-01-01 14:02:40', 'yyyy-MM-dd hh:mm:ss'), PARSEDATETIME('2018-02-10 00:20:49', 'yyyy-MM-dd hh:mm:ss'))
, (4, 'testpost', 'testpost', PARSEDATETIME('2018-01-01 14:02:40', 'yyyy-MM-dd hh:mm:ss'), PARSEDATETIME('2018-02-10 00:20:49', 'yyyy-MM-dd hh:mm:ss'))
;

INSERT INTO tag (id, name) VALUES
  (1, 'web service')
, (2, 'REST')
, (3, 'SOAP')
;

INSERT INTO post_tag (post_id, tag_id) VALUES
  (1, 1)
, (1, 3)
, (2, 1)
, (1, 2)
, (2, 3)
, (4, 2)
;

