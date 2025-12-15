package org.Task;
import java.time.LocalDate;
public class Task {
    private String name;
    private String description;
    private int priority;
    private String status;
    private String category;
    private LocalDate dueDate;
    private LocalDate ceationDate;
    private double estimatedTime;
    private String tags;
    private String assignedTo;

    public Task() {
    }

    public Task(String name, String description, int priority, String status, String category, LocalDate dueDate, LocalDate ceationDate, double estimatedTime, String tags, String assignedTo) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.category = category;
        this.dueDate = dueDate;
        this.ceationDate = ceationDate;
        this.estimatedTime = estimatedTime;
        this.tags = tags;
        this.assignedTo = assignedTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getCeationDate() {
        return ceationDate;
    }

    public void setCeationDate(LocalDate ceationDate) {
        this.ceationDate = ceationDate;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}
