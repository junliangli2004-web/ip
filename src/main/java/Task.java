import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
            return new Deadline(description, parseDateIfPresent(by));
        }
        if (command.startsWith("event ")) {
            String details = command.substring("event ".length()).trim();
            int fromIndex = details.indexOf(" /from ");
            int toIndex = details.indexOf(" /to ", fromIndex + 6);
            if (fromIndex <= 0 || toIndex <= fromIndex + 6 || toIndex + 5 >= details.length()) {
                throw new MissingFieldException("An event needs a description, /from date, and /to date.");
            }
            String description = details.substring(0, fromIndex).trim();
            String from = details.substring(fromIndex + 6, toIndex).trim();
            String to = details.substring(toIndex + 5).trim();
            if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new MissingFieldException("An event needs a description, /from date, and /to date.");
            }
            return new Event(
                    description,
                    parseDateIfPresent(from),
                    parseDateIfPresent(to)
            );
        }
        throw new InvalidCommandException("I could not understand that command.");
    }

    /**
     * Converts a task into a readable tab-separated file format.
     */
    public String toFileFormat() {
        String status = isDone ? "1" : "0";

        if (this instanceof ToDo) {
            return String.join("\t",
                    "TODO",
                    status,
                    escape(description));
        }

        if (this instanceof Deadline deadline) {
            return String.join("\t",
                    "DEADLINE",
                    status,
                    escape(description),
                    escape(valueForFile(deadline.by))
            );
        }

        if (this instanceof Event event) {
            return String.join("\t",
                    "EVENT",
                    status,
                    escape(description),
                    escape(valueForFile(event.from)),
                    escape(valueForFile(event.to))
            );
        }

        throw new IllegalArgumentException("Unknown task type.");
    }

    /**
     * Creates a task from one line in the task file.
     */
    public static Task fromFileFormat(String line) throws InvalidCommandException {
        String[] fields = line.split("\t", -1);

        if (fields.length < 3) {
            throw new IllegalArgumentException("Invalid task format.");
        }

        String type = fields[0];
        boolean isDone = fields[1].equals("1");

        Task task;

        switch (type) {
            case "TODO":
                task = new ToDo(unescape(fields[2]));
                break;

            case "DEADLINE":
                if (fields.length != 4) {
                    throw new IllegalArgumentException("Invalid deadline format.");
                }
                task = new Deadline(
                        unescape(fields[2]),
                        parseDateIfPresent(unescape(fields[3]))
                );
                break;

            case "EVENT":
                if (fields.length != 5) {
                    throw new IllegalArgumentException("Invalid event format.");
                }
                task = new Event(
                        unescape(fields[2]),
                        parseDateIfPresent(unescape(fields[3])),
                        parseDateIfPresent(unescape(fields[4]))
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown task type.");
        }

        if (isDone) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Escapes special characters before saving them to the file.
     */
    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n");
    }

    /**
     * Restores special characters after reading them from the file.
     */
    private static String unescape(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;

        for (char character : value.toCharArray()) {
            if (escaping) {
                if (character == 't') {
                    result.append('\t');
                } else if (character == 'n') {
                    result.append('\n');
                } else {
                    result.append(character);
                }
                escaping = false;
            } else if (character == '\\') {
                escaping = true;
            } else {
                result.append(character);
            }
        }

        if (escaping) {
            result.append('\\');
        }

        return result.toString();
    }

    /** Converts a user-supplied ISO date into a LocalDate. */
    private static LocalDate parseDateIfPresent(String value)
            throws InvalidCommandException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new InvalidCommandException("Please provide a valid date in yyyy-mm-dd format.");
        }
    }

    /** Converts a LocalDate to its ISO representation for task storage. */
    private static String valueForFile(LocalDate value) {
        return value.toString();
    }
}
