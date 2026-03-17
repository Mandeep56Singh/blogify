CREATE
OR REPLACE VIEW auth_credentials AS
SELECT id,
       email,
       user_name,
       password,
       is_active,
       role
FROM users;