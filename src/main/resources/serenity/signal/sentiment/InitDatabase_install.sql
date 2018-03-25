CREATE TABLE sentiment_scores (
  id VARCHAR(32) PRIMARY KEY NOT NULL,
  polarity REAL NOT NULL,
  weight INTEGER NOT NULL,
  created_ts DATETIME NOT NULL
);