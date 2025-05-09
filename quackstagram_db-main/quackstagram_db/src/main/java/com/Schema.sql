CREATE TABLE IF NOT EXISTS User (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    bio VARCHAR(100)
);


CREATE TABLE IF NOT EXISTS UserFollows (
    follower_id INT,
    followed_id INT,
    PRIMARY KEY (follower_id, followed_id),
    FOREIGN KEY (follower_id) REFERENCES User(id),
    FOREIGN KEY (followed_id) REFERENCES User(id)
);

CREATE TABLE IF NOT EXISTS Post (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    bio TEXT,
    image VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE TABLE IF NOT EXISTS `Like` (
    user_id INT,
    post_id INT,
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (post_id) REFERENCES Post(id)
);

CREATE TABLE IF NOT EXISTS Notification (
    id INT PRIMARY KEY AUTO_INCREMENT,
    post_id INT NOT NULL,
    dateTime DATETIME NOT NULL,
    FOREIGN KEY (post_id) REFERENCES Post(id)
);

CREATE TABLE IF NOT EXISTS `comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_user` int NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `id_post` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `comment_user_FK` (`id_user`),
  KEY `comment_post_FK` (`id_post`),
  CONSTRAINT `comment_post_FK` FOREIGN KEY (`id_post`) REFERENCES `post` (`id`),
  CONSTRAINT `comment_user_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
