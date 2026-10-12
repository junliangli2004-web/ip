import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Starts the ALLMIND command-line chatbot and processes user commands.
 */
public class AllMind {

    private static final Path TASK_FILE = Paths.get("tasks.txt");

    /**
     * Loads all tasks from the task file.
     * Creates an empty file if it does not already exist.
     */
    private static ArrayList<Task> loadTasks(Ui ui) {
        ArrayList<Task> tasks = new ArrayList<>();

        try {
            if (Files.notExists(TASK_FILE)) {
                Files.createFile(TASK_FILE);
                return tasks;
            }

            List<String> lines = Files.readAllLines(TASK_FILE);

            for (String line : lines) {
                if (!line.isBlank()) {
                    Task task = Task.fromFileFormat(line);
                    tasks.add(task);
                }
            }
        } catch (IOException | IllegalArgumentException | InvalidCommandException e) {
            ui.showLoadingError();
        }

        return tasks;
    }

    /**
     * Writes the current task list to the task file.
     */
    private static void saveTasks(ArrayList<Task> tasks, Ui ui) {
        try {
            List<String> lines = tasks.stream()
                    .map(Task::toFileFormat)
                    .collect(Collectors.toList());

            Files.write(
                    TASK_FILE,
                    lines
            );
        } catch (IOException e) {
            ui.showSavingError();
        }
    }

    /**
     * Greets the user, stores entered tasks, and stops when the user enters {@code bye}.
     *
     * @param args command-line arguments; not used by this application
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ArrayList<Task> tasks = loadTasks(ui);

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
                    saveTasks(tasks, ui);
                } else if (command.startsWith("unmark ")) {
                    Task.unmarkTask(command, tasks);
                    saveTasks(tasks, ui);
                } else if (command.startsWith("delete ")) {
                    Task.deleteTask(command, tasks);
                    saveTasks(tasks, ui);
                } else {
                    Task task = Task.createTask(command);
                    tasks.add(task);
                    saveTasks(tasks, ui);
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

}
