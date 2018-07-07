INSERT INTO post_comment (post_id, author_nickname, content, created_on) VALUES
  (2, 'Batman', 'I am Batman', PARSEDATETIME('2018-04-13 12:00:00', 'yyyy-MM-dd hh:mm:ss'))
, (2, 'Superman', 'I am Superman', PARSEDATETIME('2018-04-13 12:03:00', 'yyyy-MM-dd hh:mm:ss'))
, (2, 'Monster', 'I am Monster', PARSEDATETIME('2018-04-12 14:00:00', 'yyyy-MM-dd hh:mm:ss'))
;