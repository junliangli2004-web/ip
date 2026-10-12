import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
}
