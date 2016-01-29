package com.myapplication.tas;

/**
 * Created by i7-3930 on 29/01/2016.
 */
public class EventDatasProvider {

    private int event_image_resource;
    private String event_name;
    private String event_time;
    private boolean event_active;

    public EventDatasProvider(int event_image_resource, String event_name, String event_time, Boolean event_active){
        this.setEvent_image_resource(event_image_resource);
        this.setEvent_name(event_name);
        this.setEvent_time(event_time);
        this.setEvent_active(event_active);
    }

    public int getEvent_image_resource() {
        return event_image_resource;
    }

    public void setEvent_image_resource(int event_image_resource) {
        this.event_image_resource = event_image_resource;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public boolean isEvent_active() {
        return event_active;
    }

    public void setEvent_active(boolean event_active) {
        this.event_active = event_active;
    }
}
