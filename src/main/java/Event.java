import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Event extends Task {
    protected Object from;
    protected Object to;

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    public Event(String description, Object from, Object to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    private String formatDate(Object value) {
        return value instanceof LocalDate
                ? ((LocalDate) value).format(DISPLAY_FORMAT)
                : value.toString();
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + formatDate(from)
                + " to: " + formatDate(to) + ")";
    }
}