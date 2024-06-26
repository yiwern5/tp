package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import seedu.address.commons.util.InvalidationListenerManager;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.task.Task;
import seedu.address.model.task.UniqueTaskList;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .isSamePerson comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons;
    private final UniqueTaskList tasks;
    private final InvalidationListenerManager invalidationListenerManager = new InvalidationListenerManager();


    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    //    {
    //        persons = new UniquePersonList();
    //        tasks = new UniqueTaskList();
    //    }

    /**
     * Constructs the {@code AddressBook}
     */
    public AddressBook() {
        persons = new UniquePersonList();
        tasks = new UniqueTaskList();
    }

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
        indicateModified();
    }

    /**
     * Replaces the contents of the task list with {@code tasks}.
     * {@code tasks} must not contain duplicate tasks.
     */
    public void setTasks(List<Task> tasks) {
        this.tasks.setTasks(tasks);
        indicateModified();
    }
    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setTasks(newData.getTaskList());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
        indicateModified();
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
        indicateModified();
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
        Task task = key.getTask();
        if (task != null) {
            tasks.remove(key.getTask());
        }
        indicateModified();
    }

    /**
     * Gets {@code Person} from {@code AddressBook} using {@code Name}
     * {@code name} of the person
     */
    public Person getPerson(Name name) {
        requireNonNull(name);

        return persons.getPerson(name);
    }

    //// task-level operations

    /**
     * Returns true if a task with the same identity as {@code task} exists in the address book.
     */
    public boolean hasTask(Task task) {
        requireNonNull(task);
        return tasks.contains(task);
    }

    /**
     * Adds a task to the address book.
     * The task must not already exist in the address book.
     */
    public void addTask(Task t) {
        tasks.add(t);
        indicateModified();
    }

    /**
     * Replaces the given task {@code target} in the list with {@code editedTask}.
     * {@code target} must exist in the address book.
     * The task identity of {@code editedTask} must not be the same as another existing task in the address book.
     */
    public void setTask(Task target, Task editedTask) {
        requireNonNull(editedTask);

        tasks.setTask(target, editedTask);
        indicateModified();
    }

    /**
     * Assign a task to a person by adding task to the address book,
     * setting the person's task field and adding the task's personInCharge.
     */
    public void assignTask(Task task, Person pic) {
        Person editedPerson = new Person(pic.getName(), pic.getPhone(), pic.getEmail(),
                pic.getAddress(), pic.getDepartment(), pic.getTags(), pic.getEfficiency(), pic.getComment());

        addTask(task);
        task.setPersonInCharge(editedPerson);
        editedPerson.setTask(task);

        setPerson(pic, editedPerson);
        indicateModified();
    }

    /**
     * Reassigns a task from one person to another person.
     */
    public void reassignTask(Task task, Person oldPic, Person pic) {
        Person addTaskTo = new Person(pic.getName(), pic.getPhone(), pic.getEmail(),
                pic.getAddress(), pic.getDepartment(), pic.getTags(),
                pic.getEfficiency(), pic.getComment());
        Person removeTaskFrom = new Person(oldPic.getName(), oldPic.getPhone(), oldPic.getEmail(),
                oldPic.getAddress(), oldPic.getDepartment(), oldPic.getTags(),
                oldPic.getEfficiency(), oldPic.getComment());
        setPerson(oldPic, removeTaskFrom);
        Task editedTask = task.changePersonInCharge(pic);
        setTask(task, editedTask);
        addTaskTo.setTask(editedTask);
        setPerson(pic, addTaskTo);
        indicateModified();
    }

    /**
     * Marks an assigned task as done.
     */
    public void markTask(Task task) {
        Person target = task.getPersonInCharge();
        Task editedTask = task.markDone();
        Person editedPerson = new Person(target.getName(), target.getPhone(), target.getEmail(),
                target.getAddress(), target.getDepartment(), target.getTags(),
                target.updateEfficiency(), target.getComment());
        setPerson(target, editedPerson);
        setTask(task, editedTask);
        indicateModified();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        invalidationListenerManager.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListenerManager.removeListener(listener);
    }

    /**
     * Notifies listeners that the address book has been modified.
     */
    protected void indicateModified() {
        invalidationListenerManager.callListeners(this);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("tasks", tasks)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Task> getTaskList() {
        return tasks.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook)) {
            return false;
        }

        AddressBook otherAddressBook = (AddressBook) other;
        return persons.equals(otherAddressBook.persons) && tasks.equals(otherAddressBook.tasks);
    }

    @Override
    public int hashCode() {
        return persons.hashCode();
    }

}
