package com.marekhudyma.bank;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private static EmbeddedPostgres database = null;

    private Database() {
    }

    public synchronized static void startDatabase() {
        if (database == null) {
            database = startDatabaseInternal();
        }
    }

    private static EmbeddedPostgres startDatabaseInternal() {
        try {
            database = EmbeddedPostgres.builder()
                    .setPort(5433)
                    .start();
            createSchema(database);
            Runtime.getRuntime().addShutdownHook(new Thread(Database::stop));
            return database;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void createSchema(EmbeddedPostgres server) throws SQLException {
        try (Connection connection = server.getPostgresDatabase().getConnection()) {
            try (Statement statement = connection.createStatement()) {
                execute(statement, "CREATE ROLE bankuser WITH LOGIN SUPERUSER PASSWORD 'bankpassword'");
                execute(statement, "CREATE DATABASE bank OWNER bankuser ENCODING = 'utf8'");
            }
        }
    }

    private static void execute(Statement statement, String sql) throws SQLException {
        statement.execute(sql);
    }

    private static void stop() {
        if (database != null) {
            try {
                database.close();
                database = null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
