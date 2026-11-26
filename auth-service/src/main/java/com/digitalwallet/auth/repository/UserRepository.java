package com.digitalwallet.auth.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.digitalwallet.auth.model.User;

@Repository
public class UserRepository {

    private final JdbcClient jdbcClient;

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getString("id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("display_name"),
            User.parseRoles(rs.getString("roles")),
            rs.getTimestamp("created_at").toInstant());

    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void insert(User user) {
        jdbcClient.sql("""
                INSERT INTO users (id, username, password_hash, display_name, roles, created_at)
                VALUES (:id, :username, :passwordHash, :displayName, :roles, :createdAt)
                """)
                .param("id", user.id())
                .param("username", user.username())
                .param("passwordHash", user.passwordHash())
                .param("displayName", user.displayName())
                .param("roles", user.rolesAsString())
                .param("createdAt", Timestamp.from(user.createdAt()))
                .update();
    }

    public Optional<User> findByUsername(String username) {
        return jdbcClient.sql("SELECT * FROM users WHERE username = :username")
                .param("username", username)
                .query(USER_ROW_MAPPER)
                .optional();
    }

    public Optional<User> findById(String id) {
        return jdbcClient.sql("SELECT * FROM users WHERE id = :id")
                .param("id", id)
                .query(USER_ROW_MAPPER)
                .optional();
    }
}
