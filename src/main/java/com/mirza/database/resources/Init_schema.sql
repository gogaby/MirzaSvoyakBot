
CREATE TABLE IF NOT EXISTS  questions (
  id uuid not null,
  question_text text,
  music_link text,
  image_link text,
  amswer_image_link text,
  comment text,
  answers text,
  wrong_answers text,
  cost int,
  theme_id uuid not null
)
WITH (
OIDS=FALSE
) ;

CREATE TABLE IF NOT EXISTS  topics (
  id uuid not null,
  name VARCHAR(48),
  author TEXT,
  package_id uuid not NULL
)
WITH (
OIDS=FALSE
) ;

CREATE TABLE IF NOT EXISTS  packages (
  id uuid not null,
  name text,
  short_name text
)
WITH (
OIDS=FALSE
) ;

CREATE TABLE IF NOT EXISTS users (
  id INT NOT NULL,
  first_name text,
  last_name text,
  wins int,
  played int,
  rating int
);



CREATE TABLE IF NOT EXISTS  user_played_topics(
  user_id INT not null,
  topic_id uuid not NULL
)
WITH (
OIDS=FALSE
) ;

CREATE TABLE IF NOT EXISTS user_not_played_package_topics(
  user_id INT NOT NULL,
  package_id uuid NOT NULL,
  not_played_count int
)



