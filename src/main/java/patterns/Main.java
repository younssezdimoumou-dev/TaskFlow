package patterns;

public class Main {
    public static void main(String[] args) {
        // 1. Test Singleton
        DatabaseConnection db = DatabaseConnection.getInstance();

        // 2. Test Observer
        User user1 = new User("Younes");
        TaskFactory.createTask("Urgent", "Finir le projet UML");
        user1.update("Projet UML");

        System.out.println("Projet TaskFlow opérationnel avec 3 Design Patterns.");
    }
}
