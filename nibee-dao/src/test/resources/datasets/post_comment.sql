INSERT INTO post_comment (post_id, author_nickname, content, created_on) VALUES
  (2, 'Batman', 'I am Batman', PARSEDATETIME('2018-04-13 12:00:00', 'yyyy-MM-dd hh:mm:ss'))
, (2, 'Superman', 'I am Superman', PARSEDATETIME('2018-04-13 12:03:00', 'yyyy-MM-dd hh:mm:ss'))
, (2, 'Monster', 'I am Monster', PARSEDATETIME('2018-04-12 14:00:00', 'yyyy-MM-dd hh:mm:ss'))
;
INSERT INTO post_comment (id, post_id, parent_comment_id, author_nickname, content, created_on) VALUES
   (100, 1, null, 'Newman', 'I am Newman', PARSEDATETIME('2018-05-13 12:30:00', 'yyyy-MM-dd hh:mm:ss'))
,  (101, 1, 100, 'Newman2', 'I am Newman2', PARSEDATETIME('2018-05-14 12:30:00', 'yyyy-MM-dd hh:mm:ss'))
;
