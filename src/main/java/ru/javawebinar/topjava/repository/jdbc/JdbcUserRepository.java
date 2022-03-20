package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.JdbcValidationUtil;
import ru.javawebinar.topjava.repository.UserRepository;

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
    @Transactional
    public User save(User user) {
        TransactionStatus txStatus = getTransactionStatus();
        try {
            User returnUser = simpleSave(user);
            transactionManager.commit(txStatus);
            return returnUser;
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        TransactionStatus txStatus = getTransactionStatus();
        try {
            boolean deleted = simpleDelete(id);
            transactionManager.commit(txStatus);
            return deleted;
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
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
        while (sqlRowSet.next()){
            int userId=sqlRowSet.getInt("user_id");
            Role role = Role.valueOf(sqlRowSet.getString("role"));
            rolesMap.get(userId).add(role);
        }
        users.forEach(user -> user.setRoles(rolesMap.get(user.id())));
        return users;
    }

    private boolean simpleDelete(int id) {
        boolean deleted = jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", id);
        return deleted;
    }

    private User simpleSave(User user) {
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
        user.getRoles().forEach(role -> jdbcTemplate.update("INSERT INTO user_roles VALUES (?,?)", user.id(), role.name()));
        return user;
    }

    private TransactionStatus getTransactionStatus() {
        TransactionDefinition txDef = new DefaultTransactionDefinition();
        return transactionManager.getTransaction(txDef);
    }
}
