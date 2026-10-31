-- ============================================================
-- RBAC seed. Run ONCE on an empty database, after Hibernate
-- has created the tables (start the app once, then stop it).
-- ============================================================
-- Why created_at is written here: raw SQL bypasses BaseEntity's
-- @PrePersist, which normally fills it in, and the column is NOT NULL.
-- The id column is left out because MySQL generates it (IDENTITY).

-- Select the database. Backticks are needed because `default` is a reserved word in MySQL.
USE `default`;

-- ------------------------------------------------------------
-- 1. Permissions
-- One row per constant in Perm.java. The names must match exactly,
-- because setPermissions looks permissions up by name. A permission
-- missing from this table can never be assigned to a role.
-- ------------------------------------------------------------
INSERT INTO permissions (name, created_at)
VALUES

-- USER PERMISSIONS
    ('USER_READ',        CURRENT_TIMESTAMP),
    ('USER_UPDATE',      CURRENT_TIMESTAMP),
    ('USER_DELETE',      CURRENT_TIMESTAMP),
    ('USER_ASSIGN_ROLE', CURRENT_TIMESTAMP),

-- ROLE PERMISSIONS
    ('ROLE_READ',        CURRENT_TIMESTAMP),
    ('ROLE_CREATE',      CURRENT_TIMESTAMP),
    ('ROLE_UPDATE',      CURRENT_TIMESTAMP),
    ('ROLE_DELETE',      CURRENT_TIMESTAMP),

-- CATEGORY PERMISSIONS
    ('CATEGORY_READ',    CURRENT_TIMESTAMP),
    ('CATEGORY_CREATE',  CURRENT_TIMESTAMP),
    ('CATEGORY_UPDATE',  CURRENT_TIMESTAMP),
    ('CATEGORY_DELETE',  CURRENT_TIMESTAMP),

-- PRODUCT PERMISSIONS
    ('PRODUCT_READ',     CURRENT_TIMESTAMP),
    ('PRODUCT_CREATE',   CURRENT_TIMESTAMP),
    ('PRODUCT_UPDATE',   CURRENT_TIMESTAMP),
    ('PRODUCT_DELETE',   CURRENT_TIMESTAMP);

-- ------------------------------------------------------------
-- 2. USER role
-- The default role given to every newly registered user
-- (RoleNames.USER). It starts with no permissions; give it some
-- through the set-permissions endpoint. Registration breaks if
-- this row does not exist.
-- ------------------------------------------------------------
INSERT INTO roles (name, description, created_at)
VALUES ('USER', 'Default role for newly registered users.', CURRENT_TIMESTAMP);

-- ------------------------------------------------------------
-- 3. ADMIN role
-- Needed because on an empty database nobody holds any permission,
-- so nobody could call the role endpoints. It is an ordinary role:
-- it can be edited or deleted through the endpoints like any other.
-- ------------------------------------------------------------
INSERT INTO roles (name, description, created_at)
VALUES ('ADMIN', 'Full access.', CURRENT_TIMESTAMP);

-- ------------------------------------------------------------
-- 4. Give ADMIN every permission
-- The inner SELECT finds the ADMIN role's id by name, and the outer
-- SELECT returns all 16 permission ids. Each pair becomes one row in
-- the role_permissions join table (16 rows). Looking the id up by name
-- means this works whatever ids MySQL generated.
-- ------------------------------------------------------------
INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE name = 'ADMIN'), id
FROM permissions;

-- ------------------------------------------------------------
-- EXAMPLE ONLY, do not run with this seed.
-- This is how you add a single new permission later, after you
-- add its constant to Perm.java. Run it by itself on a database
-- that already has data, then assign it to roles through the
-- set-permissions endpoint.
-- ------------------------------------------------------------
INSERT INTO permissions (name, created_at)
VALUES ('REPORT_READ', CURRENT_TIMESTAMP);