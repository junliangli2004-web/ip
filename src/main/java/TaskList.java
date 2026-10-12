import java.util.ArrayList;

/** Owns the application's collection of tasks and its task operations. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /** Returns the tasks for persistence. */
    public ArrayList<Task> asArrayList() {
        return tasks;
    }

    /** Adds a task to the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Prints all tasks with their one-based list numbers. */
    public void printTasks() {
        System.out.println("The tasks in your list are as follows:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /** Marks the task selected by the command as done. */
    public void markTask(String command) throws InvalidCommandException {
        updateTaskStatus(command, true);
    }

    /** Marks the task selected by the command as not done. */
    public void unmarkTask(String command) throws InvalidCommandException {
        updateTaskStatus(command, false);
    }

    /** Deletes the task selected by the command. */
    public void deleteTask(String command) throws InvalidCommandException {
        try {
            int taskNumber = Integer.parseInt(command.substring("delete ".length()).trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                System.out.println("That task number does not exist.");
                return;
            }
            Task deletedTask = tasks.remove(taskNumber - 1);
            System.out.println("Noted. I've removed this task:");
            System.out.println("  " + deletedTask);
            System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Please provide a valid task number.");
        }
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Updates the completion status of the task selected by a mark command. */
    private void updateTaskStatus(String command, boolean done) throws InvalidCommandException {
        try {
            String prefix = done ? "mark " : "unmark ";
            int taskNumber = Integer.parseInt(command.substring(prefix.length()).trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                System.out.println("That task number does not exist.");
                return;
            }
            Task task = tasks.get(taskNumber - 1);
            if (done) {
                task.markAsDone();
                System.out.println("Affirmative. I have marked this task as done:");
            } else {
                task.markAsNotDone();
                System.out.println("Affirmative, I have marked this task as not done yet:");
            }
            System.out.println(task);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Please provide a valid task number.");
        }
    }
}
