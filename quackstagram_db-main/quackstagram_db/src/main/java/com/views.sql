CREATE VIEW user_like_behavior AS
SELECT  u.id AS user_id,u.name,COUNT(l.post_id) AS total_likes
FROM  User u
JOIN  `Like` l ON u.id = l.user_id
GROUP BY u.id
HAVING COUNT(l.post_id) > 5;

CREATE VIEW popular_posts_view AS
SELECT  p.id AS post_id,u.name AS author,COUNT(l.user_id) AS like_count
FROM  Post p
JOIN User u ON p.user_id = u.id
LEFT JOIN `Like` l ON p.id = l.post_id
GROUP BY p.id
HAVING like_count >= 3;


CREATE VIEW posts_above_avg_notifications AS
SELECT p.id AS post_id,u.name AS author,(SELECT COUNT(*) FROM Notification WHERE Notification.post_id = p.id) AS notif_count
FROM  Post p
JOIN User u ON p.user_id = u.id
GROUP BY p.id
HAVING 
    notif_count > (
        SELECT AVG(cnt) FROM (
            SELECT COUNT(*) AS cnt FROM Notification GROUP BY post_id
        ) AS subquery
    );


CREATE INDEX idx_like_user_post ON `Like` (user_id, post_id);

CREATE INDEX idx_notification_postid ON Notification(post_id);
