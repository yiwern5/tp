package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.task.Task;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label task;
    @FXML
    private Label efficiency;

    @FXML
    private Label comment;

    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);
        efficiency.setText(person.getEfficiency().value + "%");
        comment.setText(person.getComment().comment);
        double efficiencyValue = Double.parseDouble(person.getEfficiency().value);
        if (efficiencyValue < 80 && efficiencyValue > 20) {
            efficiency.setStyle("-fx-text-fill: yellow;");
        } else if (efficiencyValue <= 20) {
            efficiency.setStyle("-fx-text-fill: red;");
        }
        Label departmentLabel = new Label(person.getDepartment().department);
        departmentLabel.setStyle("-fx-background-color: #3e7b91;");
        tags.getChildren().add(departmentLabel);
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> {
                    Label tagLabel = new Label(tag.tagName);
                    tags.getChildren().add(tagLabel);
                });
        if (person.getTask() != null && !person.getTask().isDone()) {
            task.setText(displayCurrentTask(person.getTask()));
        } else {
            task.setText("No current task");
        }
    }

    private String displayCurrentTask(Task t) {
        String taskTitle = t.getTaskTitle();
        String deadline = t.getDeadline().toString();
        return ("Current task: " + taskTitle + " (by " + deadline + ")");
    }
}
