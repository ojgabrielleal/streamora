ALTER TABLE users
    ADD COLUMN role VARCHAR(30) NOT NULL DEFAULT 'STANDARD';

UPDATE users
SET role = 'ADMIN'
WHERE username = 'admin';
