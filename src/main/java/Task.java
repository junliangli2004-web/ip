import java.util.ArrayList;

/** Represents a task entered by the user and whether it has been completed. */
public class Task {
    /** The text describing this task. */
    protected String description;
    /** Whether this task has been marked as done. */
    protected boolean isDone;

    /** Creates a new unfinished task. */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] "
            + this.getDescription();
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as unfinished again. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns the display icon for this task's completion status. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns this task's description. */
    public String getDescription() {
        return description;
    }

    /** Prints each saved task with a one-based number. */
    public static void printTasks(ArrayList<Task> tasks) {
        System.out.println("The tasks in your list are as follows:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /** Creates the appropriate task subtype from a user's task command. */
    public static Task createTask(String command)
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
    public static void markTask(String command, ArrayList<Task> tasks) throws InvalidCommandException {
        updateTaskStatus(command, "mark ", tasks, true);
    }

    /** Reverses the done status of the one-based task number in an {@code unmark} command. */
    public static void unmarkTask(String command, ArrayList<Task> tasks) throws InvalidCommandException {
        updateTaskStatus(command, "unmark ", tasks, false);
    }

    private static void updateTaskStatus(String command, String prefix, ArrayList<Task> tasks, boolean done)
            throws InvalidCommandException {
        try {
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

    /** Deletes the one-based task number supplied with a {@code delete} command. */
    public static void deleteTask(String command, ArrayList<Task> tasks) throws InvalidCommandException {
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
}
