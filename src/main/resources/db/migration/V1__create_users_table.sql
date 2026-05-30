CREATE TABLE users (
                       id NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       username VARCHAR2(255) NOT NULL UNIQUE,
                       password VARCHAR2(255) NOT NULL,
                       enabled NUMBER(1) DEFAULT 1,
                       account_non_locked NUMBER(1) DEFAULT 1,
                       deleted NUMBER(1) DEFAULT 0 NOT NULL,
                       deleted_at TIMESTAMP NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       created_by VARCHAR2(255),
                       updated_by VARCHAR2(255)
);

CREATE TABLE user_roles (
    user_id NUMBER(19) NOT NULL,
    roles VARCHAR2(255),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id)
);
