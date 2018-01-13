package com.example.aser93.organisernewdesign;

import java.util.Date;

/**
 * Encapsulates information about a news entry
 */
public final class EventsListItem {

    private final String title;
    private final String description;
    private String colour="#FF0000";


    public EventsListItem(final String title, final String description,Date date,String colour) {
        this.title = title;
        this.description = description;
        this.colour=colour;
    }


    public String getColor(){return colour;}
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


}
