-- Default admin user (password: Admin@123 — BCrypt hashed)
INSERT INTO users (name, email, password, role)
VALUES (
    'Super Admin',
    'admin@education.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN'
);

-- Seed default categories
INSERT INTO categories (name, description) VALUES
    ('GCSE',         'General Certificate of Secondary Education'),
    ('University',   'University level courses'),
    ('Medical',      'Medical and healthcare studies'),
    ('Data Science', 'Data Science and Machine Learning');
