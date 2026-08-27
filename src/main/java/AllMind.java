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
        String[] tasks = new String[MAX_TASKS];
        Boolean[] taskIsMarkedChecks = new Boolean[MAX_TASKS];
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
            if (command.equals("list")) {
                printTasks(tasks, taskIsMarkedChecks, taskCount);
            } else if (command.startsWith("mark ")) {
                markTask(command, tasks, taskIsMarkedChecks, taskCount);
            } else if (command.startsWith("unmark ")) {
                unmarkTask(command, tasks, taskIsMarkedChecks, taskCount);
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = command;
                taskIsMarkedChecks[taskCount] = false;
                taskCount++;
                System.out.println("added: " + command);
            } else {
                System.out.println("Task list is full.");
            }
            System.out.println(horizontalLine);
        }
    }

    /**
     * Prints each saved task with a one-based number.
     *
     * @param tasks the array containing saved tasks
     * @param taskIsMarkedChecks the completion status for each saved task
     * @param taskCount the number of saved tasks in the array
     */
    private static void printTasks(String[] tasks, Boolean[] taskIsMarkedChecks, int taskCount) {
        for (int i = 0; i < taskCount; i++) {
            String status = Boolean.TRUE.equals(taskIsMarkedChecks[i]) ? "X" : " ";
            System.out.println((i + 1) + ".[" + status + "] " + tasks[i]);
        }
    }

    /** Marks the one-based task number supplied with a {@code mark} command as done. */
    private static void markTask(String command, String[] tasks, Boolean[] taskIsMarkedChecks,
                                 int taskCount) {
        String taskNumberText = command.substring("mark ".length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                System.out.println("That task number does not exist.");
                return;
            }

            int taskIndex = taskNumber - 1;
            taskIsMarkedChecks[taskIndex] = true;
            System.out.println("Affirmative. I have marked this task as done:");
            System.out.println("  [X] " + tasks[taskIndex]);
        } catch (NumberFormatException e) {
            System.out.println("Please provide a valid task number.");
        }
    }

    /** Reverses the done status of the one-based task number in a {@code unmark} command. */
    private static void unmarkTask(String command, String[] tasks, Boolean[] taskIsMarkedChecks,
                                   int taskCount) {
        String taskNumberText = command.substring("unmark ".length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                System.out.println("That task number does not exist.");
                return;
            }

            int taskIndex = taskNumber - 1;
            taskIsMarkedChecks[taskIndex] = false;
            System.out.println("Affirmative, I have marked this task as not done yet:");
            System.out.println("  [ ] " + tasks[taskIndex]);
        } catch (NumberFormatException e) {
            System.out.println("Please provide a valid task number.");
        }
    }
}
