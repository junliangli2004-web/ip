package allmind.model;

/** A task without a deadline or event date range. */
public class ToDo extends Task {

    /** Creates a to-do task with the supplied description. */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
