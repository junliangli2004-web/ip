package allmind.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** A task that occurs between two specific dates. */
public class Event extends Task {
    /** The date on which the event starts. */
    protected LocalDate from;
    /** The date on which the event ends. */
    protected LocalDate to;

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    /**
     * Creates a new {@code Event} object
     * @param description Description of the task
     * @param from Start date
     * @param to End date
     * */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the date on which this event starts. */
    public LocalDate getFrom() {
        return from;
    }

    /** Returns the date on which this event ends. */
    public LocalDate getTo() {
        return to;
    }

    private String formatDate(LocalDate value) {
        return value.format(DISPLAY_FORMAT);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + formatDate(from)
                + " to: " + formatDate(to) + ")";
    }
}
