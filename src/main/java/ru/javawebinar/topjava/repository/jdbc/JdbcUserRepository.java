package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.model.Role;

import javax.sql.DataSource;
import java.util.*;

@Repository
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

    //TODO
    private User simpleSave(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            for (Role r : user.getRoles()) {
                jdbcTemplate.update("INSERT INTO user_roles VALUES (?,?)", user.id(), r.name());
            }
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password,
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
        }
        return user;
    }

    public static void main(String[] args) {
        try (GenericXmlApplicationContext appCtx = new GenericXmlApplicationContext()) {
            appCtx.getEnvironment().setActiveProfiles(Profiles.getActiveDbProfile(),Profiles.JDBC);
            appCtx.load("spring/spring-db.xml");
            appCtx.refresh();
            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
            DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
            //jdbcTemplate.update("INSERT INTO user_roles VALUES (?,?)", "user.id()", "r.name()");
        }
    }

    @Override
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
        user.setRoles(jdbcTemplate.queryForList("SELECT ur.role FROM user_roles ur WHERE ur.user_id=?", Role.class, user.id()));
        return user;
    }

    @Override
    public User getByEmail(String email) {
        User user = DataAccessUtils.singleResult(jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email));
        user.setRoles(jdbcTemplate.queryForList("SELECT ur.role FROM user_roles ur WHERE ur.user_id=?", Role.class, user.id()));
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", ROW_MAPPER);
        List<Role> listRoles = jdbcTemplate.queryForList("SELECT ur.role FROM user_roles ur", Role.class);
        List<Integer> listSerials = jdbcTemplate.queryForList("SELECT ur.user_id FROM user_roles ur", Integer.class);
        Map<Integer, Set<Role>> rolesMap = new HashMap<>();
        users.forEach(user -> rolesMap.computeIfAbsent(user.id(), integer -> new HashSet<>()));
        for (int i = 0; i < listSerials.size(); i++) {
            rolesMap.get(listSerials.get(i)).add(listRoles.get(i));
        }
        for (User u : users) {
            u.setRoles(rolesMap.get(u.id()));
        }
        return users;
    }

    private TransactionStatus getTransactionStatus() {
        TransactionDefinition txDef = new DefaultTransactionDefinition();
        return transactionManager.getTransaction(txDef);
    }

    private boolean simpleDelete(int id) {
        boolean deleted = jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
        jdbcTemplate.update("DELETE FROM user_role ur WHERE ur.user_id=?", id);
        return deleted;
    }
}
