INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN');

-- senha: 123456 (bcrypt 12 rounds) = $2a$12$...
INSERT INTO users (id, username, password) VALUES (1, 'john', '$2a$12$D4G5f18o7aMMfwasBlabla');
INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);
