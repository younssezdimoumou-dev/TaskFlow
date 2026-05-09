package patterns;

public class TaskFactory {
    public static void createTask(String type, String title) {
        System.out.println("Création d'une tâche de type [" + type + "] : " + title);

    }
}
