package controller;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import datamodel.ToDoData;
import datamodel.ToDoItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    private List<ToDoItem> toDoItems;

    @FXML
    private ListView<ToDoItem> todoListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<ToDoItem> filteredList;

    private Predicate<ToDoItem> wantAllItems;
    private Predicate<ToDoItem> wantTodaysItems;

    public void initialize() {

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);

        todoListView.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {

                    if (newValue != null) {
                        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                        itemDetailsTextArea.setText(item.getDetails());
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy"); // "d M yy");
                        deadlineLabel.setText(df.format(item.getDeadline()));
                    }
                });

        wantAllItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem toDoItem) {
                return true;
            }
        };

        wantTodaysItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem toDoItem) {
                return (toDoItem.getDeadline().equals(LocalDate.now()));
            }
        };

        filteredList = new FilteredList<ToDoItem>(ToDoData.getInstance().getToDoItems(),
                wantAllItems);

        SortedList<ToDoItem> sortedList = new SortedList<>(filteredList,
                new Comparator<>() {
                    @Override
                    public int compare(ToDoItem o1, ToDoItem o2) {
                        /* comparing the deadline of two items
                        * and then sorting their dates, with the earliest deadline being on
                        * the top of the list */
                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                });

//        todoListView.setItems(ToDoData.getInstance().getToDoItems());
        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            /*
             * Check notes.md */
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
                ListCell<ToDoItem> cell = new ListCell<>() {

                    @Override
                    protected void updateItem(ToDoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getShortDescription());

                            /*
                             * Using isBefore > equals() method because if
                             * the Date is past due, the subject name of the note will still be highlighted
                             * Red, where as if we used the equals() method, anything past due the deadline
                             * the subject of the ToDoApp will no longer be highlighted
                             * where for the User, they won't know if the made Note/ or ToDoItem
                             * has past the deadline or not */

                            if (item.getDeadline().isBefore(LocalDate.now())) {
                                //a warning on day of or anything past due
                                setTextFill(Color.rgb(204, 51, 0));
                                //if the To Do's Date is equaled to deadline date, text will be red
                            } else if (item.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                                //a warning one day before
                                setTextFill(Color.rgb(255, 153, 102)); //soft red
                            } else if (item.getDeadline().isBefore(LocalDate.now().plusDays(2))) {
                                //warning 2 days before
                                setTextFill(Color.rgb(255, 204, 0)); //yellow
                            } else if (item.getDeadline().isBefore(LocalDate.now().plusDays(4))) {
                                //a few days notice
                                setTextFill(Color.rgb(153, 204, 51)); //light green, fair warning
                            } else if (item.getDeadline().isBefore(LocalDate.now().plusDays(7))) {
                                //1 week before notice
                                setTextFill(Color.rgb(51, 153, 0)); //green color notification

                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );

                return cell;
            }
        });
    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("My Personal Notes"); //header of the window
        dialog.setHeaderText("Create a New Note"); //this is a simple header message
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml")); //file name

        /* a try/ catch because a new window can throw an error msg
         * so therefore we will catch that error msg here instead */
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
            //<- that is known as a node, what is inside of the parems ( )'s
        } catch (IOException e) {
            System.out.println("Couldn't load 🙁, try again or contact support \n" +
                    "support@mypersonalnotes.com 📧");
            e.printStackTrace();
            return;
        }

        /*
        * We want to add an OK and CANCEL function, so that the user can either submit or cancel
        * whatever they were intended in doing, and we can do so with the following below;
        * we want to show the dialogue and then we

        want our event handler
        to be suspended while the user interacts with the dialogue and when the user's finished */
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        //input
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            ToDoItem newItem = controller.processResults();
            todoListView.getSelectionModel().select(newItem);
        }
    }

    @FXML //make sure to annotate
    public void handleKeyPressed(KeyEvent keyEvent) {
        ToDoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                //if the selected key is the delete key from keyboard, delete the selected item
                deleteItem(selectedItem); //<< calling the method here, and then deleting the selected
            }
        }
    }

    @FXML
    public void handleClickListView() {
        /* this will attract what item has been selected from the user
         * if we don't cast the below, we will receive an error
         * but;
         *
         * with changing the above instance variable (field)
         * ListView toDoListView, to ->
         * ListView<ToDoItem> toDoListView;
         * we will no longer need to cast the below */
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
        /* and then will show the due date at the end of the description */
        itemDetailsTextArea.setText(item.getDetails());
        deadlineLabel.setText(item.getDeadline().toString());
    }

    public void deleteItem(ToDoItem item) {
        /* Create a pop up message to ask user if they're sure to delete */
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        /* title, and information about the selected item the user wants to delete */
        alert.setTitle("Delete item");
        alert.setHeaderText("Delete item: " + item.getShortDescription());
        alert.setContentText("Are you sure?");
        /* no need to create OK and CANCEL button as they come with Alert Class */
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            ToDoData.getInstance().deleteToDoItem(item);
        }
    }

    @FXML
    public void handleFilterButton() {
        ToDoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        /* when the toggle button is pressed, we want it to pop back up when the toggle button is deselected
        * we don't want the toggle button for "Todays Items" to stay selected
        * so therefore, we will retrieve the selectionModel, along with the selectedItem // button */

        if(filterToggleButton.isSelected()) {
            filteredList.setPredicate(wantTodaysItems);
            if (filteredList.isEmpty()) {
                itemDetailsTextArea.clear();
                deadlineLabel.setText("");
            } else if (filteredList.contains(selectedItem)) {
                todoListView.getSelectionModel().select(selectedItem);
            } else {
                todoListView.getSelectionModel().selectFirst();
            }
        } else {
            filteredList.setPredicate(wantAllItems);
            todoListView.getSelectionModel().select(selectedItem); // by doing this <-
            //we want the button to be unfiltered after selected
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
        //to exit application
    }
}
