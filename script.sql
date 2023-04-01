DROP DATABASE IF EXISTS filesystem_db;

CREATE DATABASE filesystem_db;
USE filesystem_db;
CREATE TABLE users
(
	id       INT NOT NULL AUTO_INCREMENT,
	username VARCHAR(255) UNIQUE NOT NULL,
	password VARCHAR(255),
	PRIMARY KEY (id),
	index (username)
);

CREATE TABLE files
(
	id              INT      NOT NULL AUTO_INCREMENT,
	name            VARCHAR(255),
	file_content    LONGBLOB NOT NULL,
	create_date     DATETIME,
	PRIMARY KEY (id)
);

CREATE TABLE file_share
(
	id         INT NOT NULL AUTO_INCREMENT,
	user_id    INT,
	is_owner   BOOLEAN,
	file_id    INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES users (id),
	FOREIGN KEY (file_id) REFERENCES files (id)
);
