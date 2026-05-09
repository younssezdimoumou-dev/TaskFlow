package patterns;

public class User implements Observer {
    private String name;
    public User(String name) { this.name = name; }

    @Override
    public void update(String taskName) {
        System.out.println("Notification pour " + name + ": La tâche [" + taskName + "] a été mise à jour.");
    }
}
