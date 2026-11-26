CREATE TABLE
    users (
        id            VARCHAR(36) PRIMARY KEY,
        username      VARCHAR(20) NOT NULL UNIQUE,
        password_hash VARCHAR(255) NOT NULL,
        display_name  VARCHAR(50) NOT NULL,
        roles         VARCHAR(50) NOT NULL,
        created_at    TIMESTAMP NOT NULL DEFAULT NOW()
    );
