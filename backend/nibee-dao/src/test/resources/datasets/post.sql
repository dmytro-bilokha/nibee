INSERT INTO post (id, name, path, shareable, comment_allowed, created_on, modified_on) VALUES
  (1000, 'post-about-rest', '2018/01/REST', 1, 1, PARSEDATETIME('2018-04-13 12:00:00', 'yyyy-MM-dd hh:mm:ss'), NULL)
, (2000, 'post-about-soap', '2018/02/SOAP', 0, 1, PARSEDATETIME('2018-04-11 16:42:00', 'yyyy-MM-dd hh:mm:ss'), PARSEDATETIME('2018-04-15 10:02:09', 'yyyy-MM-dd hh:mm:ss'))
, (3000, 'resources2', 'resources2', 0, 0, PARSEDATETIME('2018-01-01 14:02:40', 'yyyy-MM-dd hh:mm:ss'), PARSEDATETIME('2018-02-10 00:20:49', 'yyyy-MM-dd hh:mm:ss'))
, (4000, 'testpost', 'testpost', 1, 1, PARSEDATETIME('2018-01-01 14:02:40', 'yyyy-MM-dd hh:mm:ss'), PARSEDATETIME('2018-02-10 00:20:49', 'yyyy-MM-dd hh:mm:ss'))
;

INSERT INTO tag (id, name) VALUES
  (1000, 'web service')
, (2000, 'REST')
, (3000, 'SOAP')
;

INSERT INTO post_tag (post_id, tag_id) VALUES
  (1000, 1000)
, (1000, 3000)
, (2000, 1000)
, (1000, 2000)
, (2000, 3000)
, (4000, 2000)
;

