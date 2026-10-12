import java.util.ArrayList;
import java.io.IOException;

/**
 * Starts the ALLMIND command-line chatbot and processes user commands.
 */
public class AllMind {

    /**
     * Greets the user, stores entered tasks, and stops when the user enters {@code bye}.
     *
     * @param args command-line arguments; not used by this application
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("tasks.txt");
        ArrayList<Task> tasks;
        try {
            tasks = storage.load();
        } catch (IOException | IllegalArgumentException | InvalidCommandException e) {
            ui.showLoadingError();
            tasks = new ArrayList<>();
        }

        ui.showWelcome();

        String command;
        while ((command = ui.readCommand()) != null) {

            if (command.equals("bye")) {
                ui.showGoodbye();
                break;
            }

            ui.showLine();
            
            try {
                if (command.equals("list")) {
                    Task.printTasks(tasks);
                } else if (command.startsWith("mark ")) {
                    Task.markTask(command, tasks);
                    saveTasks(storage, tasks, ui);
                } else if (command.startsWith("unmark ")) {
                    Task.unmarkTask(command, tasks);
                    saveTasks(storage, tasks, ui);
                } else if (command.startsWith("delete ")) {
                    Task.deleteTask(command, tasks);
                    saveTasks(storage, tasks, ui);
                } else {
                    Task task = Task.createTask(command);
                    tasks.add(task);
                    saveTasks(storage, tasks, ui);
                    System.out.println("Understood. Your task has been added:");
                    System.out.println("  " + task);
                    System.out.println("There are now " + tasks.size() + " tasks in the list.");
                }
            } catch (InvalidCommandException | MissingFieldException e) {
                System.out.println(e.getMessage());
            }
            ui.showLine();
        }
    }

    /** Saves tasks and reports file errors through the UI. */
    private static void saveTasks(Storage storage, ArrayList<Task> tasks, Ui ui) {
        try {
            storage.save(tasks);
        } catch (IOException | IllegalArgumentException e) {
            ui.showSavingError();
        }
    }

}
