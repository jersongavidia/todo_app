-- Insert sample users
INSERT INTO users (username, email, password) VALUES
('john_doe', 'john.doe@example.com', '$2a$10$D9zM4GvRNo56vZHVtvnpaO8.lLKN4p1y5FcTLKgYuvZDN2Nxx8k5W'), -- Password: "password123"
('jane_smith', 'jane.smith@example.com', '$2a$10$R5IeYPq8TSTG6iVlpHzUuOpNo2G/51Qm0vGlBQsL20Xa4K1.2GhQW'); -- Password: "123secure"

-- Insert sample tasks for the first user (john_doe)
INSERT INTO tasks (title, description, completed, user_id) VALUES
('Buy groceries', 'Milk, eggs, bread, and coffee', FALSE, 1),
('Complete project report', 'Finalize the report by end of the day', FALSE, 1),
('Read book', 'Finish reading chapter 5 of "Clean Code"', TRUE, 1);

-- Insert sample tasks for the second user (jane_smith)
INSERT INTO tasks (title, description, completed, user_id) VALUES
('Plan vacation', 'Research destinations and book flights', FALSE, 2),
('Workout', 'Morning yoga and strength training', TRUE, 2);
