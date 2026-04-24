INSERT INTO users (name, email, password, role, created_at)
VALUES ('Dev User', 'dev@taskflow.com', 'placeholder', 'USER', NOW())
    ON CONFLICT (email) DO NOTHING;