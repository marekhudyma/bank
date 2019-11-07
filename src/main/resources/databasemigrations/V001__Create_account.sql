CREATE TABLE accounts (
    id            UUID      PRIMARY KEY,
    first_name    VARCHAR   NOT NULL,
    last_name     VARCHAR   NOT NULL,
    bank_balance  DECIMAL   NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);