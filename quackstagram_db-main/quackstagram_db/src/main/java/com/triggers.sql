DELIMITER $$

CREATE PROCEDURE AddNotification(IN pid INT)
BEGIN INSERT INTO Notification (post_id, dateTime)
    VALUES (pid, NOW());
END$$

DELIMITER;

DELIMITER $$
CREATE FUNCTION GetLikeCount(postId INT) RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE likeCount INT;
    SELECT COUNT(*) INTO likeCount FROM `Like` WHERE post_id = postId;
    RETURN likeCount;
END$$

DELIMITER;

DELIMITER $$

CREATE TRIGGER after_like_adds_notification
AFTER INSERT ON `Like`
FOR EACH ROW
BEGIN DECLARE totalLikes INT;
CALL AddNotification(NEW.post_id);
    SET totalLikes = GetLikeCount(NEW.post_id);


END$$

DELIMITER;

DELIMITER $$
CREATE TRIGGER prevent_self_follow
BEFORE INSERT ON UserFollows
FOR EACH ROW
BEGIN
    IF NEW.follower_id = NEW.followed_id THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'A user cannot follow himself';
    END IF;
END$$

DELIMITER ;
