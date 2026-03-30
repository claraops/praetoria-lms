
INSERT INTO roles (role_name) VALUES ('ROLE_STUDENT');
INSERT INTO roles (role_name) VALUES ('ROLE_TEACHER');
INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN');

INSERT INTO organizations (name, registration_key, contact_email, max_students, is_active, license_expires_at, created_at, updated_at)
VALUES ('École Test', 'PRE-ABCD1234', 'contact@ecoletest.fr', 100, true, '2027-12-31 23:59:59', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);