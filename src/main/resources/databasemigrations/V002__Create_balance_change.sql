CREATE TABLE balance_changes (
    id                 UUID      PRIMARY KEY,
    account_id         UUID      NOT NULL,
    amount             DECIMAL   NOT NULL,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT balance_changes_account_id_fk FOREIGN KEY (account_id) REFERENCES accounts (id)
);