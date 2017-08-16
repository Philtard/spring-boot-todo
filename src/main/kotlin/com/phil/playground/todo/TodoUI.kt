package com.phil.playground.todo

import com.vaadin.data.Binder
import com.vaadin.event.ShortcutAction
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.VaadinRequest
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import org.springframework.beans.factory.annotation.Autowired
import javax.annotation.PostConstruct

@SpringUI
class TodoUI : UI() {

    @Autowired
    lateinit var todoList: TodoList

    val layout = VerticalLayout()

    override fun init(request: VaadinRequest?) {
        content = layout
        layout.defaultComponentAlignment = Alignment.MIDDLE_CENTER

        addHeader()
        addForm()
        addTodoList()
        addDeleteButton()
    }

    private fun addHeader() {
        val label = Label("TODOs")
        label.addStyleName(ValoTheme.LABEL_H1)
        layout.addComponent(label)
    }

    private fun addForm() {
        val formLayout = HorizontalLayout()
        formLayout.setWidth("80%")

        val taskField = TextField()
        taskField.focus()
        val addButton = Button("")

        formLayout.addComponentsAndExpand(taskField)
        formLayout.addComponent(addButton)
        layout.addComponent(formLayout)

        addButton.addStyleName(ValoTheme.BUTTON_PRIMARY)
        addButton.icon = VaadinIcons.PLUS


        addButton.addClickListener {
            todoList.addTodo(Todo(taskField.value))
            taskField.clear()
            taskField.focus()
        }
        addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER)
    }

    private fun addTodoList() {
        layout.addComponent(todoList)
    }

    private fun addDeleteButton() {
        layout.addComponent(Button("Delete completed",Button.ClickListener { todoList.deleteCompleted() }))
    }
}

@UIScope
@SpringComponent
class TodoList : VerticalLayout(), TodoChangeListener {

    @Autowired lateinit var repository: TodoRepository

    @PostConstruct
    fun init() {
        setWidth("80%")
        update()
    }

    private fun update() {
        setTodos(repository.findAll())
    }

    private fun setTodos(todos: List<Todo>) {
        removeAllComponents()
        todos.forEach { addComponent(TodoLayout(it, this)) }
    }

    fun addTodo(todo: Todo) {
        repository.save(todo)
        update()
    }

    override fun todoChanged(todo: Todo) {
        addTodo(todo)
    }

    fun deleteCompleted() {
        repository.deleteByDone(true)
        update()
    }
}

interface TodoChangeListener {
    fun todoChanged(todo: Todo)
}

class TodoLayout(todo: Todo, changeListener: TodoChangeListener) : HorizontalLayout() {

    val done = CheckBox()
    val text = TextField()

    init {
        setWidth("100%")
        defaultComponentAlignment = Alignment.MIDDLE_LEFT

        text.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS)
        text.valueChangeMode = ValueChangeMode.BLUR

        val binder = Binder(Todo::class.java)
        binder.bindInstanceFields(this)

        binder.bean = todo

        addComponent(done)
        addComponentsAndExpand(text)

        binder.addValueChangeListener { changeListener.todoChanged(todo) }
    }
}
