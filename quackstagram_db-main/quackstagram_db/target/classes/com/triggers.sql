DELIMITER $$

CREATE TRIGGER trg_create_notification_after_like
AFTER INSERT ON `Like`
FOR EACH ROW
BEGIN
    INSERT INTO Notification (post_id, dateTime)
    VALUES (NEW.post_id, NOW());
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER trg_prevent_self_follow
BEFORE INSERT ON UserFollows
FOR EACH ROW
BEGIN
    IF NEW.follower_id = NEW.followed_id THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Un utente non può seguire se stesso';
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER trg_log_post_creation
AFTER INSERT ON Post
FOR EACH ROW
BEGIN
    INSERT INTO PostLog (post_id, user_id, created_at)
    VALUES (NEW.id, NEW.user_id, NOW());
END$$

DELIMITER ;
