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

    /** Saves tasks and reports file errors through the UI. */
    private static void saveTasks(Storage storage, TaskList tasks, Parser parser, Ui ui) {
        try {
            storage.save(tasks.asArrayList(), parser);
        } catch (IOException | IllegalArgumentException e) {
            ui.showSavingError();
        }
    }

}
