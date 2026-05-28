CREATE TABLE refresh_tokens (
                                id NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                user_id NUMBER(19) NOT NULL,
                                token_hash VARCHAR2(255) NOT NULL,
                                expires_at TIMESTAMP NOT NULL,
                                revoked NUMBER(1) DEFAULT 0,
                                revoked_at TIMESTAMP,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                created_by VARCHAR2(255),
                                updated_by VARCHAR2(255),
                                CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

CREATE TABLE refresh_token_user_roles (
    refresh_token_id NUMBER(19) NOT NULL,
    user_roles VARCHAR2(255),
    CONSTRAINT fk_rt_user_roles FOREIGN KEY (refresh_token_id) REFERENCES refresh_tokens(id)
);