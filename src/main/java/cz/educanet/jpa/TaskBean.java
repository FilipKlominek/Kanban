package cz.educanet.jpa;

import cz.educanet.jpa.entities.TaskEntity;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class TaskBean {

    private String newTask;

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyTaskApp");

    public List<List<TaskEntity>> getTasks() {
        EntityManager em = emf.createEntityManager();

        TypedQuery<TaskEntity> query = em.createQuery("SELECT task FROM TaskEntity AS task", TaskEntity.class);
        List<TaskEntity> result = query.getResultList();

        em.close();

        ArrayList<List<TaskEntity>> sorted = new ArrayList<>();
        sorted.add(new ArrayList<TaskEntity>()); //0 backlog
        sorted.add(new ArrayList<TaskEntity>()); //1 in progress
        sorted.add(new ArrayList<TaskEntity>()); //2 in review
        sorted.add(new ArrayList<TaskEntity>()); //3 test
        sorted.add(new ArrayList<TaskEntity>()); //4 finished

        for (TaskEntity t : result) {
            sorted.get(t.getState()).add(t); //🧠💥
        }

        return sorted;
    }

    public void addTask() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();

        TaskEntity task = new TaskEntity();
        task.setTask(newTask);
        task.setState(0);
        em.persist(task);

        et.commit();
        em.close();
    }

    public void advanceTask(int id) {
        EntityManager em = emf.createEntityManager();

        EntityTransaction et = em.getTransaction();
        et.begin();

        TypedQuery<TaskEntity> query = em.createQuery("SELECT task FROM TaskEntity AS task WHERE task.id = :id", TaskEntity.class);
        query.setParameter("id", id);

        TaskEntity result = query.getSingleResult();

        if (result.getState() >= 4) {

            Query query2 = em.createQuery("DELETE FROM TaskEntity AS task WHERE task.id = :id");
            query2.setParameter("id", id);

            query2.executeUpdate();

            et.commit();
            em.close();

            return;
        }

        result.advance();
        em.persist(result);

        et.commit();
        em.close();
    }

    public void regressTask(int id) {
        EntityManager em = emf.createEntityManager();

        EntityTransaction et = em.getTransaction();
        et.begin();

        TypedQuery<TaskEntity> query = em.createQuery("SELECT task FROM TaskEntity AS task WHERE task.id = :id", TaskEntity.class);
        query.setParameter("id", id);

        TaskEntity result = query.getSingleResult();

        if (result.getState() <= 0) {

            Query query2 = em.createQuery("DELETE FROM TaskEntity AS task WHERE task.id = :id");
            query2.setParameter("id", id);

            query2.executeUpdate();

            et.commit();
            em.close();

            return;
        }

        result.regress();
        em.persist(result);

        et.commit();
        em.close();
    }

    @PreDestroy
    public void onDestroy() {
        emf.close();
    }

    public String getNewTask() {
        return newTask;
    }

    public void setNewTask(String newTask) {
        this.newTask = newTask;
    }
}
