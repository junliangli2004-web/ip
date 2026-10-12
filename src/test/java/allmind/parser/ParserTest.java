package allmind.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import allmind.model.Deadline;
import allmind.model.Event;
import allmind.model.Task;
import allmind.model.ToDo;

/** Tests the conversion of task commands into task objects. */
public class ParserTest {
    private final Parser parser = new Parser();

    /** A todo command should create an unfinished todo with the supplied description. */
    @Test
    public void parseTask_todoCommand_createsTodoTask() throws Exception {
        Task task = parser.parseTask("todo read lecture notes");

        ToDo todo = assertInstanceOf(ToDo.class, task);
        assertEquals("read lecture notes", todo.getDescription());
        assertFalse(todo.isDone());
    }

    /** A deadline command should parse and retain both its description and date. */
    @Test
    public void parseTask_deadlineCommand_createsDeadlineTask() throws Exception {
        Task task = parser.parseTask("deadline submit report /by 2026-10-20");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 10, 20), deadline.getBy());
    }

    /** An event command should parse and retain its description and date range. */
    @Test
    public void parseTask_eventCommand_createsEventTask() throws Exception {
        Task task = parser.parseTask("event team meeting /from 2026-10-21 /to 2026-10-22");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("team meeting", event.getDescription());
        assertEquals(LocalDate.of(2026, 10, 21), event.getFrom());
        assertEquals(LocalDate.of(2026, 10, 22), event.getTo());
    }

    /** Surrounding whitespace should not become part of a task's fields. */
    @Test
    public void parseTask_commandWithExtraWhitespace_trimsFields() throws Exception {
        Task task = parser.parseTask("todo   buy groceries   ");

        assertEquals("buy groceries", task.getDescription());
    }

    /** A todo without a description should be rejected with a missing-field error. */
    @Test
    public void parseTask_todoWithoutDescription_missingFieldExceptionThrown() {
        assertThrows(MissingFieldException.class, () -> parser.parseTask("todo   "));
    }

    /** A deadline without a date should be rejected with a missing-field error. */
    @Test
    public void parseTask_deadlineWithoutDate_missingFieldExceptionThrown() {
        assertThrows(MissingFieldException.class,
                () -> parser.parseTask("deadline submit report"));
    }

    /** An event with an invalid date should be rejected with an invalid-command error. */
    @Test
    public void parseTask_eventWithInvalidDate_invalidCommandExceptionThrown() {
        assertThrows(InvalidCommandException.class,
                () -> parser.parseTask("event team meeting /from tomorrow /to 2026-10-22"));
    }

    /** A command with no recognized task type should be rejected. */
    @Test
    public void parseTask_unknownCommand_invalidCommandExceptionThrown() {
        assertThrows(InvalidCommandException.class, () -> parser.parseTask("set meeting"));
    }
}
