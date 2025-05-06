CREATE VIEW popular_posts AS
SELECT 
    p.id AS post_id,
    p.user_id,
    u.name AS author_name,
    COUNT(l.user_id) AS like_count
FROM 
    Post p
JOIN 
    User u ON p.user_id = u.id
LEFT JOIN 
    `Like` l ON p.id = l.post_id
GROUP BY 
    p.id
HAVING 
    COUNT(l.user_id) >= 3;


CREATE VIEW post_notification_stats AS
SELECT 
    p.id AS post_id,
    u.name AS author_name,
    COUNT(n.id) AS total_notifications
FROM 
    Post p
JOIN 
    Notification n ON p.id = n.post_id
JOIN 
    User u ON p.user_id = u.id
GROUP BY 
    p.id
HAVING 
    COUNT(n.id) > 1;


CREATE VIEW post_notification_stats AS
SELECT 
    p.id AS post_id,
    u.name AS author_name,
    COUNT(n.id) AS total_notifications
FROM 
    Post p
JOIN 
    Notification n ON p.id = n.post_id
JOIN 
    User u ON p.user_id = u.id
GROUP BY 
    p.id
HAVING 
    COUNT(n.id) > 1;
