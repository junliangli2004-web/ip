import java.util.ArrayList;
import java.util.Scanner;

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
        ArrayList<Task> tasks = new ArrayList<Task>();

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
                } else if (command.startsWith("unmark ")) {
                    Task.unmarkTask(command, tasks);
                } else if (command.startsWith("delete ")) {
                    Task.deleteTask(command, tasks);
                } else {
                    Task task = Task.createTask(command);
                    tasks.add(task);
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
