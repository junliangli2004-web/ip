package allmind.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import allmind.model.Deadline;
import allmind.model.Event;
import allmind.model.Task;
import allmind.model.ToDo;

/** Converts user commands that create tasks into task objects. */
public class Parser {
    /** Creates the task described by a {@code todo}, {@code deadline}, or {@code event} command. */
    public Task parseTask(String command) throws InvalidCommandException, MissingFieldException {
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
            return new Deadline(description, parseDate(by));
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
            return new Event(description, parseDate(from), parseDate(to));
        }
        throw new InvalidCommandException("I could not understand that command.");
    }

    /** Converts an ISO date in a task command into a date object. */
    private LocalDate parseDate(String value) throws InvalidCommandException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new InvalidCommandException("Please provide a valid date in yyyy-mm-dd format.");
        }
    }

    /** Converts a task into the tab-separated format used by task storage. */
    public String toFileFormat(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof ToDo) {
            return String.join("\t", "TODO", status, escape(task.getDescription()));
        }
        if (task instanceof Deadline deadline) {
            return String.join("\t", "DEADLINE", status, escape(task.getDescription()),
                    escape(deadline.getBy().toString()));
        }
        if (task instanceof Event event) {
            return String.join("\t", "EVENT", status, escape(task.getDescription()),
                    escape(event.getFrom().toString()), escape(event.getTo().toString()));
        }
        throw new IllegalArgumentException("Unknown task type.");
    }

    /** Creates a task from one line in the task file. */
    public Task fromFileFormat(String line) throws InvalidCommandException {
        String[] fields = line.split("\t", -1);
        if (fields.length < 3) {
            throw new IllegalArgumentException("Invalid task format.");
        }
        Task task;
        switch (fields[0]) {
            case "TODO":
                task = new ToDo(unescape(fields[2]));
                break;
            case "DEADLINE":
                if (fields.length != 4) {
                    throw new IllegalArgumentException("Invalid deadline format.");
                }
                task = new Deadline(unescape(fields[2]), parseDate(unescape(fields[3])));
                break;
            case "EVENT":
                if (fields.length != 5) {
                    throw new IllegalArgumentException("Invalid event format.");
                }
                task = new Event(unescape(fields[2]), parseDate(unescape(fields[3])),
                        parseDate(unescape(fields[4])));
                break;
            default:
                throw new IllegalArgumentException("Unknown task type.");
        }
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Escapes special characters before saving a task. */
    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n");
    }

    /** Restores special characters after reading a task. */
    private String unescape(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;
        for (char character : value.toCharArray()) {
            if (escaping) {
                result.append(character == 't' ? '\t' : character == 'n' ? '\n' : character);
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
}
