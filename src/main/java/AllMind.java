import java.util.Scanner;

/**
 * Starts the ALLMIND command-line chatbot and processes user commands.
 */
public class AllMind {
    /** Maximum number of tasks kept during one run of the application. */
    private static final int MAX_TASKS = 100;

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

        String horizontalLine = "________________________________________________________________________________________________________________________";

        String welcomeMessage = "This is ALLMIND, a primitive chatbot system. \n" +
                                "Welcome back, User";

        String exitMessage = "It appears that will be all. Goodbye.";
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

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
                    printTasks(tasks, taskCount);
                } else if (command.startsWith("mark ")) {
                    markTask(command, tasks, taskCount);
                } else if (command.startsWith("unmark ")) {
                    unmarkTask(command, tasks, taskCount);
                } else if (taskCount < MAX_TASKS) {
                    Task task = createTask(command);
                    tasks[taskCount] = task;
                    taskCount++;
                    System.out.println("Understood. Your task has been added:");
                    System.out.println("  " + task);
                    System.out.println("There are now " + taskCount + " tasks in the list.");
                } else {
                    System.out.println("Task list is full.");
                }
            } catch (InvalidCommandException | MissingFieldException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(horizontalLine);
        }
    }

    /**
     * Prints each saved task with a one-based number.
     *
     * @param tasks the array containing saved tasks
     * @param taskCount the number of saved tasks in the array
     */
    private static void printTasks(Task[] tasks, int taskCount) {
        System.out.println("The tasks in your list are as follows:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i].toString());
        }
    }

    /** Creates the appropriate task subtype from a user's task command. */
    private static Task createTask(String command)
            throws InvalidCommandException, MissingFieldException {
        if (command.startsWith("todo ")) {
            String description = command.substring("todo ".length()).trim();
            if (description.isEmpty()) {
                throw new MissingFieldException("A todo task needs a description.");
            }
            return new ToDo(description);
        }

        if (command.startsWith("deadline ")) {
            String details = command.substring("deadline ".length()).trim();
            int byIndex = details.indexOf(" /by ");
            if (byIndex <= 0 || byIndex + 5 >= details.length()) {
                throw new MissingFieldException("A deadline needs a description and a /by date.");
            }
            String description = details.substring(0, byIndex).trim();
            String by = details.substring(byIndex + 5).trim();
            if (description.isEmpty() || by.isEmpty()) {
                throw new MissingFieldException("A deadline needs a description and a /by date.");
            }
            return new Deadline(description, by);
        }

        if (command.startsWith("event ")) {
            String details = command.substring("event ".length()).trim();
            int fromIndex = details.indexOf(" /from ");
            int toIndex = details.indexOf(" /to ", fromIndex + 6);
            if (fromIndex <= 0 || toIndex <= fromIndex + 6 || toIndex + 5 >= details.length()) {
                throw new MissingFieldException("An event needs a description, /from time, and /to time.");
            }
            String description = details.substring(0, fromIndex).trim();
            String from = details.substring(fromIndex + 6, toIndex).trim();
            String to = details.substring(toIndex + 5).trim();
            if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new MissingFieldException("An event needs a description, /from time, and /to time.");
            }
            return new Event(description, from, to);
        }

        throw new InvalidCommandException("I could not understand that command.");
    }

    /** Marks the one-based task number supplied with a {@code mark} command as done. */
    private static void markTask(String command, Task[] tasks, int taskCount) throws InvalidCommandException {
        String taskNumberText = command.substring("mark ".length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                System.out.println("That task number does not exist.");
                return;
            }

            int taskIndex = taskNumber - 1;
            tasks[taskIndex].markAsDone();
            System.out.println("Affirmative. I have marked this task as done:");
            System.out.println(tasks[taskIndex].toString());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Please provide a valid task number.");
        }
    }

    /** Reverses the done status of the one-based task number in a {@code unmark} command. */
    private static void unmarkTask(String command, Task[] tasks, int taskCount) throws InvalidCommandException {
        String taskNumberText = command.substring("unmark ".length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                System.out.println("That task number does not exist.");
                return;
            }

            int taskIndex = taskNumber - 1;
            tasks[taskIndex].markAsNotDone();
            System.out.println("Affirmative, I have marked this task as not done yet:");
            System.out.println(tasks[taskIndex].toString());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Please provide a valid task number.");
        }
    }
}
