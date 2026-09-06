import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {

    protected Object by; // LocalDate when yyyy-MM-dd, otherwise String

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    public Deadline(String description, Object by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        String displayedDate = by instanceof LocalDate
                ? ((LocalDate) by).format(DISPLAY_FORMAT)
                : by.toString();

        return "[D]" + super.toString() + " (by: " + displayedDate + ")";
    }
}
