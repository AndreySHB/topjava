package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.JdbcValidationUtil;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final DataSourceTransactionManager transactionManager;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate, DataSourceTransactionManager transactionManager) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.transactionManager = transactionManager;
    }

    @Override
    public User get(int id) {
        User user = DataAccessUtils.singleResult(jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id));
        if (user == null) {
            return null;
        }
        user.setRoles(jdbcTemplate.queryForList("SELECT ur.role FROM user_roles ur WHERE ur.user_id=?", Role.class, id));
        return user;
    }

    @Override
    public User getByEmail(String email) {
        User user = DataAccessUtils.singleResult(jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email));
        if (user == null) {
            return null;
        }
        user.setRoles(jdbcTemplate.queryForList("SELECT ur.role FROM user_roles ur WHERE ur.user_id=?", Role.class, user.id()));
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", ROW_MAPPER);
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT ur.user_id, ur.role  FROM user_roles ur");
        Map<Integer, Set<Role>> rolesMap = new HashMap<>();
        users.forEach(user -> rolesMap.computeIfAbsent(user.id(), integer -> new HashSet<>()));
        while (sqlRowSet.next()) {
            int userId = sqlRowSet.getInt("user_id");
            Role role = Role.valueOf(sqlRowSet.getString("role"));
            rolesMap.get(userId).add(role);
        }
        users.forEach(user -> user.setRoles(rolesMap.get(user.id())));
        return users;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        boolean deleted = jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", id);
        return deleted;
    }

    @Override
    @Transactional
    public User save(User user) {
        JdbcValidationUtil.Validate(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password,
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) != 0) {
            jdbcTemplate.update("DELETE FROM user_roles r WHERE user_id=?", user.id());
        }
        int userId = user.id();
        List<Role> roles = user.getRoles().stream().toList();
        jdbcTemplate.batchUpdate("INSERT INTO user_roles VALUES (?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, userId);
                ps.setString(2, roles.get(i).toString());
            }

            @Override
            public int getBatchSize() {
                return user.getRoles().size();
            }
        });
        return user;
    }
}
