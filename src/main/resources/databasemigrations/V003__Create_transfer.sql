CREATE TABLE transfers (
    id                  UUID      PRIMARY KEY,
    debtor_change_id    UUID      NOT NULL,
    creditor_change_id  UUID      NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT transfers_debtor_change_id_fk   FOREIGN KEY (debtor_change_id)   REFERENCES balance_changes (id),
    CONSTRAINT transfers_creditor_change_id_fk FOREIGN KEY (creditor_change_id) REFERENCES balance_changes (id)
);