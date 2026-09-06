import java.util.ArrayList;
import java.util.Scanner;
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
    private static ArrayList<Task> loadTasks() {
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
            System.out.println("The task file could not be read.");
        }

        return tasks;
    }

    /**
     * Writes the current task list to the task file.
     */
    private static void saveTasks(ArrayList<Task> tasks) {
        try {
            List<String> lines = tasks.stream()
                    .map(Task::toFileFormat)
                    .collect(Collectors.toList());

            Files.write(
                    TASK_FILE,
                    lines
            );
        } catch (IOException e) {
            System.out.println("The task file could not be saved.");
        }
    }

    /**
     * Greets the user, stores entered tasks, and stops when the user enters {@code bye}.
     *
     * @param args command-line arguments; not used by this application
     */
    public static void main(String[] args) {
        String banner =
                "                                                                                      \n" +
                        "                                                                                      \n" +
                        "      db      `7MMF'      `7MMF'      `7MMM.     ,MMF'`7MMF'`7MN.   `7MF'`7MM\"\"\"Yb.   \n" +
                        "     ;MM:       MM          MM          MMMb    dPMM    MM    MMN.    M    MM    `Yb. \n" +
                        "    ,V^MM.      MM          MM          M YM   ,M MM    MM    M YMb   M    MM     `Mb \n" +
                        "   ,M  `MM      MM          MM          M  Mb  M' MM    MM    M  `MN. M    MM      MM \n" +
                        "   AbmmmqMA     MM      ,   MM      ,   M  YM.P'  MM    MM    M   `MM.M    MM     ,MP \n" +
                        "  A'     VML    MM     ,M   MM     ,M   M  `YM'   MM    MM    M     YMM    MM    ,dP' \n" +
                        ".AMA.   .AMMA..JMMmmmmMMM .JMMmmmmMMM .JML. `'  .JMML..JMML..JML.    YM  .JMMmmmdP'   \n" +
                        "                                                                                      \n" +
                        "                                                                                      ";


        String underscore = "_";

        String horizontalLine = underscore.repeat(100);

        String welcomeMessage = "This is ALLMIND, a primitive chatbot system. \n" +
                                "Welcome back, User";

        String exitMessage = "It appears that will be all. Goodbye.";

        ArrayList<Task> tasks = loadTasks();

        System.out.println(horizontalLine);
        System.out.println(banner);
        System.out.println(welcomeMessage);
        System.out.println(horizontalLine);

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {

            String command = scanner.nextLine();

            if (command.equals("bye")) {
                System.out.println(horizontalLine);
                System.out.println(exitMessage);
                System.out.println(horizontalLine);
                break;
            }

            System.out.println(horizontalLine);
            
            try {
                if (command.equals("list")) {
                    Task.printTasks(tasks);
                } else if (command.startsWith("mark ")) {
                    Task.markTask(command, tasks);
                    saveTasks(tasks);
                } else if (command.startsWith("unmark ")) {
                    Task.unmarkTask(command, tasks);
                    saveTasks(tasks);
                } else if (command.startsWith("delete ")) {
                    Task.deleteTask(command, tasks);
                    saveTasks(tasks);
                } else {
                    Task task = Task.createTask(command);
                    tasks.add(task);
                    saveTasks(tasks);
                    System.out.println("Understood. Your task has been added:");
                    System.out.println("  " + task);
                    System.out.println("There are now " + tasks.size() + " tasks in the list.");
                }
            } catch (InvalidCommandException | MissingFieldException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(horizontalLine);
        }
    }

}
