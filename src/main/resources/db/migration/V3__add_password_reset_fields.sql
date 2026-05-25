Alter TABLE users
ADD COLUMN reset_password_token VARCHAR(255) UNIQUE,
ADD COLUMN reset_password_expires TIMESTAMP;