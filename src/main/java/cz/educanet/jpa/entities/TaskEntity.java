package cz.educanet.jpa.entities;

import jakarta.persistence.*;

@Entity
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long taskId;

    @Column
    private String task;

    @Column
    private int state;

    public void advance() {
        this.state++;
    }

    public void regress() {
        this.state--;
    }

    public long getTaskId() {
        return taskId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
