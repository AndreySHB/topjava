package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        meal.setUser(em.getReference(User.class, userId));
        if (meal.isNew()) {
            em.persist(meal);
            return meal;
        }
        if (em.getReference(Meal.class, meal.id()).getUser().id() == userId) {
            em.merge(meal);
            return meal;
        }
        return null;
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return em.createQuery("delete from Meal where id=:id and user.id=:user_id")
                .setParameter("id", id)
                .setParameter("user_id", userId)
                .executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = em.find(Meal.class, id);
        return meal != null ? (meal.getUser().getId() == userId ? meal : null) : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createQuery("select e from Meal e where e.user.id =:userid ORDER BY e.dateTime DESC", Meal.class)
                .setParameter("userid", userId).getResultList();
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return em.createQuery("select e from Meal e where e.user.id = :user_id and e.dateTime >= :startdatetime and e.dateTime< :enddatetime order by e.dateTime desc", Meal.class)
                .setParameter("user_id", userId).setParameter("startdatetime", startDateTime)
                .setParameter("enddatetime", endDateTime).getResultList();
    }
}