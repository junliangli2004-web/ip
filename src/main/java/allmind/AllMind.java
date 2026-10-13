package allmind;

import java.io.IOException;
import java.util.Locale;

import allmind.model.Task;
import allmind.model.TaskList;
import allmind.parser.InvalidCommandException;
import allmind.parser.MissingFieldException;
import allmind.parser.Parser;
import allmind.storage.Storage;
import allmind.ui.Ui;

/**
 * Starts the ALLMIND command-line chatbot and processes user commands.
 */
public class AllMind {

    private final Parser parser = new Parser();
    private final Storage storage = new Storage("tasks.txt");
    private TaskList tasks;

    /** Creates an AllMind instance and loads its saved tasks. */
    public AllMind() {
        try {
            tasks = new TaskList(storage.load(parser));
        } catch (IOException | IllegalArgumentException | InvalidCommandException e) {
            tasks = new TaskList();
        }
    }

    /**
     * Greets the user, stores entered tasks, and stops when the user enters {@code bye}.
     *
     * @param args command-line arguments; not used by this application
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("tasks.txt");
        Parser parser = new Parser();
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load(parser));
        } catch (IOException | IllegalArgumentException | InvalidCommandException e) {
            ui.showLoadingError();
            tasks = new TaskList();
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
                    tasks.printTasks();
                } else if (command.equals("find") || command.startsWith("find ")) {
                    tasks.findTasks(command);
                } else if (command.startsWith("mark ")) {
                    tasks.markTask(command);
                    saveTasks(storage, tasks, parser, ui);
                } else if (command.startsWith("unmark ")) {
                    tasks.unmarkTask(command);
                    saveTasks(storage, tasks, parser, ui);
                } else if (command.startsWith("delete ")) {
                    tasks.deleteTask(command);
                    saveTasks(storage, tasks, parser, ui);
                } else {
                    Task task = parser.parseTask(command);
                    tasks.add(task);
                    saveTasks(storage, tasks, parser, ui);
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

    /**
     * Generates a response for the user's chat message.
     */
    public String getResponse(String input) {
        try {
            if (input.equals("bye")) {
                return "Goodbye!";
            }
            if (input.equals("list")) {
                if (tasks.size() == 0) {
                    return "There are no tasks in your list.";
                }
                StringBuilder response = new StringBuilder("The tasks in your list are as follows:\n");
                for (int i = 0; i < tasks.asArrayList().size(); i++) {
                    response.append(i + 1).append(". ").append(tasks.asArrayList().get(i)).append("\n");
                }
                return response.toString().trim();
            }
            if (input.equals("find") || input.startsWith("find ")) {
                String keyword = input.substring("find".length()).trim();
                if (keyword.isEmpty()) {
                    throw new InvalidCommandException("Please provide a keyword to search for.");
                }
                StringBuilder response = new StringBuilder("Here are the matching tasks in your list:\n");
                for (int i = 0; i < tasks.asArrayList().size(); i++) {
                    Task task = tasks.asArrayList().get(i);
                    if (task.getDescription().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))) {
                        response.append(i + 1).append(". ").append(task).append("\n");
                    }
                }
                return response.toString().trim();
            }
            if (input.startsWith("mark ")) {
                tasks.markTask(input);
                saveTasks(storage, tasks, parser, new Ui());
                return "Task marked as done.";
            }
            if (input.startsWith("unmark ")) {
                tasks.unmarkTask(input);
                saveTasks(storage, tasks, parser, new Ui());
                return "Task marked as not done.";
            }
            if (input.startsWith("delete ")) {
                tasks.deleteTask(input);
                saveTasks(storage, tasks, parser, new Ui());
                return "Task deleted.";
            }

            Task task = parser.parseTask(input);
            tasks.add(task);
            saveTasks(storage, tasks, parser, new Ui());
            return "Understood. Your task has been added:\n  " + task;
        } catch (InvalidCommandException | MissingFieldException e) {
            return e.getMessage();
        }
    }

    /** Saves tasks and reports file errors through the UI. */
    private static void saveTasks(Storage storage, TaskList tasks, Parser parser, Ui ui) {
        try {
            storage.save(tasks.asArrayList(), parser);
        } catch (IOException | IllegalArgumentException e) {
            ui.showSavingError();
        }
    }

}
