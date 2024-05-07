package tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected Duration duration;
    protected Instant startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description, Status status, Instant startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, Instant startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.status = Status.NEW;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public Optional<Instant> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public Optional<Instant> getEndTime() {
        return Optional.ofNullable(startTime).flatMap(st -> Optional.ofNullable(duration).map(st::plus));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return this.id == task.id &&
                this.status == task.status &&
                Objects.equals(this.name, task.name) &&
                Objects.equals(this.description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return id + "," +
                this.getClass().getSimpleName() + "," +
                name + "," +
                description + "," +
                status + "," +
                (startTime != null ? startTime : "") + "," +
                (duration != null ? duration.toMinutes() : "") + ",";
    }
}