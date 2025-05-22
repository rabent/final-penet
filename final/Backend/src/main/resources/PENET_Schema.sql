DROP TABLE IF EXISTS guguns;
DROP TABLE IF EXISTS sidos;
DROP TABLE IF EXISTS contenttypes;

CREATE TABLE sidos (
  no INT NOT NULL AUTO_INCREMENT,
  sido_code INT NOT NULL UNIQUE,
  sido_name VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (no)
);

CREATE TABLE guguns (
  no INT NOT NULL AUTO_INCREMENT,
  sido_code INT NOT NULL,
  gugun_code INT NOT NULL,
  gugun_name VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (no),
  CONSTRAINT fk_guguns_sido_code FOREIGN KEY (sido_code) REFERENCES sidos(sido_code)
);

CREATE INDEX guguns_sido_idx ON guguns(sido_code);
CREATE INDEX gugun_code_idx ON guguns(gugun_code);

CREATE TABLE contenttypes (
  content_type_id INT NOT NULL,
  content_type_name VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (content_type_id)
);
