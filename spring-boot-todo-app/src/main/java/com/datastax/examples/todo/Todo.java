package com.datastax.examples.todo;

import java.io.Serializable;
import java.util.UUID;

/**
 * Object to be exposed in the REST API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Todo implements Serializable, Comparable<Todo> {

    /** Serial. */
    private static final long serialVersionUID = -7930662827102682933L;
    
    /** Unique id for the task. */
    private UUID uuid;
    
    /** Title. */
    private String title;
    
    /** Status. */
    private boolean completed = false;
    
    /** order field. */
    private int order = 0;
    
    /**
     * Constructors.
     */
    public Todo() {}
    
    /**
     * Constructor helping for tests
     */
    public Todo(String title) {
        this(UUID.randomUUID(), title, false, -1);
    }

    /**
     * Full constructor.
     */
    public Todo(UUID uuid, String title, boolean completed, int order) {
        super();
        this.uuid = uuid;
        this.title = title;
        this.completed = completed;
        this.order = order;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Todo other) {
        if (other == null) return 1;
        return order - other.getOrder();
    }
    /**
     * Getter accessor for attribute 'uuid'.
     *
     * @return
     *       current value of 'uuid'
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Setter accessor for attribute 'uuid'.
     * @param uuid
     *      new value for 'uuid '
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Getter accessor for attribute 'title'.
     *
     * @return
     *       current value of 'title'
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter accessor for attribute 'title'.
     * @param title
     *      new value for 'title '
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter accessor for attribute 'completed'.
     *
     * @return
     *       current value of 'completed'
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Setter accessor for attribute 'completed'.
     * @param completed
     *      new value for 'completed '
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Getter accessor for attribute 'order'.
     *
     * @return
     *       current value of 'order'
     */
    public int getOrder() {
        return order;
    }

    /**
     * Setter accessor for attribute 'order'.
     * @param order
     *      new value for 'order '
     */
    public void setOrder(int order) {
        this.order = order;
    }
}
