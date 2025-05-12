-- users_with_more_than_x_followers.sql
SELECT u.id, u.name, COUNT(uf.follower_id) AS followers
FROM User u
JOIN UserFollows uf ON u.id = uf.followed_id
GROUP BY u.id, u.name, u.bio
HAVING COUNT(uf.follower_id) > x;

-- total_posts_per_user.sql
SELECT u.id, u.name, COUNT(p.id) AS posts
FROM User u
JOIN Post p ON u.id = p.user_id
GROUP BY u.id, u.name;

-- comments_on_user_posts.sql
SELECT c.id AS comment_id, c.content AS comment_content, c.id_user AS commenter_id, u.name AS commenter_name, p.id AS post_id, p.user_id AS post_owner_id
FROM Post p
JOIN comment c ON p.id = c.id_post
JOIN User u ON c.id_user = u.id
WHERE p.user_id = :target_user_id;

-- top_x_most_liked_posts.sql
SELECT u.name, p.id AS posts, MAX(l.user_id) AS likes
FROM User u
JOIN Post p ON u.id = p.user_id
JOIN Like l ON p.id = l.post_id
GROUP BY u.name, p.id
LIMIT 5;

-- liked_posts_per_user.sql
SELECT u.name, COUNT(l.post_id) AS likedposts
FROM User u
JOIN Post p ON u.id = p.user_id
JOIN Like l ON p.id = l.post_id
GROUP BY u.name
ORDER BY likedposts DESC;

-- users_with_no_posts.sql
SELECT u.id, u.name, u.bio
FROM User u
WHERE u.id NOT IN (
    SELECT user_id FROM Post
);

-- mutual_follows.sql
SELECT uf.follower_id AS User_1, uf.followed_id AS User_2
FROM UserFollows uf
JOIN UserFollows uf2 ON uf.follower_id = uf2.followed_id AND uf.followed_id = uf2.follower_id
WHERE uf.follower_id < uf.followed_id;

-- user_with_most_posts.sql
SELECT u.id AS user_id, u.name, COUNT(p.id) AS post_count
FROM User u
JOIN Post p ON u.id = p.user_id
GROUP BY u.id, u.name
ORDER BY post_count DESC
LIMIT 1;

-- top_x_users_with_most_followers.sql
SELECT u.id, u.name, COUNT(uf.follower_id) AS followers
FROM User u
JOIN UserFollows uf ON u.id = uf.followed_id
GROUP BY u.id, u.name, u.bio
ORDER BY followers DESC
LIMIT 5;

-- posts_liked_by_all_users.sql
SELECT p.id AS post_id, p.bio AS post_bio, p.image AS post_image, COUNT(DISTINCT l.user_id) AS total_likes
FROM Post p
JOIN Like l ON p.id = l.post_id
JOIN User u ON l.user_id = u.id
GROUP BY p.id
HAVING COUNT(DISTINCT l.user_id) = (SELECT COUNT(*) FROM User);

-- most_active_user.sql
SELECT u.id AS user_id, u.name, COALESCE(p.post_count, 0) AS total_posts, COALESCE(c.comment_count, 0) AS total_comments, COALESCE(l.like_count, 0) AS total_likes,
(COALESCE(p.post_count, 0) + COALESCE(c.comment_count, 0) + COALESCE(l.like_count, 0)) AS total_activity
FROM User u
LEFT JOIN (SELECT user_id, COUNT(*) AS post_count FROM Post GROUP BY user_id) p ON u.id = p.user_id
LEFT JOIN (SELECT id_user, COUNT(*) AS comment_count FROM comment GROUP BY id_user) c ON u.id = c.id_user
LEFT JOIN (SELECT user_id, COUNT(*) AS like_count FROM Like GROUP BY user_id) l ON u.id = l.user_id
ORDER BY total_activity DESC
LIMIT 1;

-- average_likes_per_user_post.sql
SELECT u.id AS user_id, u.name AS username, COUNT(l.user_id) / COUNT(DISTINCT p.id) AS avg_likes_per_post
FROM User u
LEFT JOIN Post p ON u.id = p.user_id
LEFT JOIN Like l ON p.id = l.post_id
GROUP BY u.id, u.name;

-- posts_more_comments_than_likes.sql
SELECT p.id AS post_id, p.postName AS post_name, p.user_id AS author_id, u.name AS author_name,
(SELECT COUNT(*) FROM comment c WHERE c.id_post = p.id) AS total_comments,
(SELECT COUNT(*) FROM Like l WHERE l.post_id = p.id) AS total_likes
FROM Post p
INNER JOIN User u ON u.id = p.user_id
WHERE (SELECT COUNT(*) FROM comment c WHERE c.id_post = p.id) > 
      (SELECT COUNT(*) FROM Like l WHERE l.post_id = p.id);

-- users_liked_all_posts_of_user.sql
SELECT u.id AS user_id, u.name AS username
FROM User u
INNER JOIN Like l ON u.id = l.user_id
INNER JOIN Post p ON l.post_id = p.id
WHERE p.user_id = 1
GROUP BY u.id, u.name
HAVING COUNT(l.post_id) = (SELECT COUNT(*) FROM Post WHERE user_id = 1);

-- most_popular_post_per_user.sql
SELECT u.name AS username, p.id AS post_id, p.postName AS post_name, p.bio AS post_bio, p.image AS post_image, COUNT(l.user_id) AS like_count
FROM Post p
INNER JOIN User u ON u.id = p.user_id
LEFT JOIN Like l ON l.post_id = p.id
GROUP BY p.user_id, p.id
HAVING like_count = (
    SELECT MAX(like_sub.like_count)
    FROM (SELECT post_id, COUNT(user_id) AS like_count FROM Like GROUP BY post_id) AS like_sub
    INNER JOIN Post sub_p ON sub_p.id = like_sub.post_id
    WHERE sub_p.user_id = p.user_id
);

-- highest_follower_following_ratio.sql
SELECT u.id AS user_id, u.name AS username, IFNULL(f.follower_count, 0) AS total_followers, IFNULL(g.following_count, 0) AS total_following,
IF(IFNULL(g.following_count, 0) = 0, IFNULL(f.follower_count, 0), f.follower_count / g.following_count) AS ratio
FROM User u
LEFT JOIN (SELECT followed_id, COUNT(follower_id) AS follower_count FROM UserFollows GROUP BY followed_id) f ON u.id = f.followed_id
LEFT JOIN (SELECT follower_id, COUNT(followed_id) AS following_count FROM UserFollows GROUP BY follower_id) g ON u.id = g.follower_id
ORDER BY ratio DESC
LIMIT 1;

-- month_with_most_posts.sql
SELECT DATE_FORMAT(n.dateTime, '%Y-%m') AS post_month, COUNT(*) AS total_posts
FROM Notification n
INNER JOIN Post p ON n.post_id = p.id
WHERE n.type = 'post'
GROUP BY DATE_FORMAT(n.dateTime, '%Y-%m')
ORDER BY total_posts DESC
LIMIT 1;

-- users_not_interacted_with_user_posts.sql
SELECT u.id AS user_id, u.name AS username
FROM User u
LEFT JOIN (
    SELECT DISTINCT l.user_id FROM Like l
    INNER JOIN Post p ON l.post_id = p.id
    WHERE p.user_id = 3
    UNION
    SELECT DISTINCT c.id_user FROM Comment c
    INNER JOIN Post p ON c.id_post = p.id
    WHERE p.user_id = 3
) AS interactions ON u.id = interactions.user_id
WHERE interactions.user_id IS NULL;

-- user_with_most_follower_growth.sql
SELECT u.id AS user_id, u.name AS username, COUNT(f.follower_id) AS follower_growth
FROM User u
INNER JOIN UserFollows f ON u.id = f.followed_id
WHERE f.follower_id >= DATE_SUB(NOW(), INTERVAL :days DAY)
GROUP BY u.id, u.name
ORDER BY follower_growth DESC
LIMIT 1;

-- users_followed_by_more_than_x.sql
SELECT u.id AS user_id, u.name AS username, COUNT(f.follower_id) AS follower_count
FROM User u
INNER JOIN UserFollows f ON u.id = f.followed_id
GROUP BY u.id, u.name
HAVING COUNT(f.follower_id) > 10
ORDER BY follower_count DESC;
