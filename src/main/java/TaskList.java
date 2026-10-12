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
        Task.printTasks(tasks);
    }

    /** Marks the task selected by the command as done. */
    public void markTask(String command) throws InvalidCommandException {
        Task.markTask(command, tasks);
    }

    /** Marks the task selected by the command as not done. */
    public void unmarkTask(String command) throws InvalidCommandException {
        Task.unmarkTask(command, tasks);
    }

    /** Deletes the task selected by the command. */
    public void deleteTask(String command) throws InvalidCommandException {
        Task.deleteTask(command, tasks);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }
}
