JavaFX Notes:

When using `<BorderPane> ` over `<GridPane>`
`<HGap>` and `<VGap>` is no longer apply to `<BorderPane> ` properties
The `<AlignmentProperty>` also doesn't work with `<BorderPane> `

ToDoItem Class:
In this Class, the user will be able to the ability of;
what to do,
due date deadline, or when created and more

for Method

    @FXML
    public void handleClickedListView() {
        /* this will attract what item has been selected from the user
        * if we don't cast the below, we will receive an error
        * but;
        *
        * with changing the above instance variable (field)
        * ListView toDoListView, to ->
        * ListView<ToDoItem> toDoListView;
        * we will no longer need to cast the below */

        ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
        System.out.println("The selected item is " + item);

        StringBuilder sb = new StringBuilder(item.getDetails());
        sb.append("\n\n\n");
        sb.append("Due");
        sb.append(item.getDeadline().toString());
        showTextArea.setText(sb.toString());
        /* this code will create 2 enter spaces below the description of the note
        * and then will show the due date at the end of the description
        *
        * we're calling getDeadLine(); method from the TodoItem Class,
        * which the DeadLine, was hardcoded above, when we used LocalDate.of(); method
        *
        * this will print in the console, for testing purposes and us to keep track of the frontend
        * for the below, if we want the text to be shown on the right side, of the
        * shortDescription, the details/ long post of the subject of the ToDoItem
        * then we will have to do the follow, which is what you'll see below
        *
        * if you run the program, you will see the extended details of the subject */
        showTextArea.setText(item.getDetails());
    }
    
No need for StringBuilder anymore, because we hardcoded in FXML file
We've replaced the StringBuilder with;

                <Label text="Due:">
                    <font>
                        <!-- for due text -->
                        <Font name="Times New Roman bold" size="20"/>
                    </font>
                </Label>
                <Label fx:id="deadlineLabel">
                    <font>
                        <!-- for deadline -->
                        <Font name="Times New Roman bold" size="20";
                    </font>
                </Label>

The Singleton Class
Going to add a Singleton Class for the Controller and Main Class be able to access.

We're going to use a Singleton Class when we want there to be only one Instance of a Class
created over the entire run of the application. Because of this, the Singleton Class creates
one Instance of itself and it has a private Constructor.

Read more about Singleton Class here
https://www.geeksforgeeks.org/singleton-class-java/

CHANGE to Controller initialize() method

`public void initialize() {
        ToDoItem item1 = new ToDoItem("Finish UI Proposal",
                "Finish UI design for Mark. Deadline 1st of July",
                LocalDate.of(2020, Month.JULY, 1));`

        ToDoItem item2 = new ToDoItem("Take Bruno to Vet",
                "Bruno has to get nails cut and fur trimmed",
                LocalDate.of(2020, Month.JUNE, 8));

        ToDoItem item3 = new ToDoItem("Pick up dad from airport",
                "Visiting from Turkey, make sure to pick him up September 16, arrival time 1PM.",
                LocalDate.of(2020, Month.SEPTEMBER, 16));

        ToDoItem item4 = new ToDoItem("NBA Season Opener Party",
                "NBA season opener. Lakers vs Bucks Game 1, Warriors vs Clippers Game 2. " +
                        "Expecting Mark, Charles, Damian and James over.",
                LocalDate.of(2020, Month.OCTOBER, 28));

        ToDoItem item5 = new ToDoItem("Dry Cleaning",
                "Pick up dry cleaning on Friday at 5PM",
                LocalDate.of(2020, Month.JULY, 3));

        ToDoItem item6 = new ToDoItem("Mail B-Day Card",
                "Buy 30th B-Day Card for Charles",
                LocalDate.of(2020, Month.AUGUST, 15));

        toDoItems = new ArrayList<>();
        toDoItems.add(item1);
        toDoItems.add(item2);
        toDoItems.add(item3);
        toDoItems.add(item4);
        toDoItems.add(item5);
        toDoItems.add(item6);

        ToDoData.getInstance().setToDoItems(toDoItems);

        toDoListView.getSelectionModel().selectedItemProperty().addListener
                (new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observableValue,
                                ToDoItem toDoItem, ToDoItem newValue) {
                if (newValue != null) {
                    ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
                    showTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    deadlineLabel.setText(item.getDeadline().toString());
                }
            }
        });

        toDoListView.getItems().setAll(toDoItems);
        toDoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
                toDoListView.getItems().setAll(toDoItems);

        toDoListView.getItems().setAll(ToDoData.getInstance().getToDoItems());
        
Deleted what was hardcoded, we also changed `toDoListView.getItems().setall(toDoItems);`

to `toDoListView.getItems().setall(ToDoData.getInstance().getToDoItems());`

retrieving the data from our getInstance();

CHANGES

        try {
            Parent root = FXMLLoader.load(getClass().getResource("todoitemDialog.fxml"));
            dialog.getDialogPane().setContent(root);
            //<- that is known as a node, what is inside of the parems ( )'s
        } catch (IOException e) {
            System.out.println("Couldn't load window");
            e.printStackTrace();
            return;
        }
        
        No need for Parent root anymore, because we used FXMLLoader out of the try/ catch
        
        We've replaced it with;
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("todoitemDialog.fxml"));
                
        The following code now looks like;
        
        
