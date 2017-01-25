package com.beeva.formacion.netty.model;

/**
 *
 * Created by Beeva Architecture Team
 */
public class Event {
    private String id;
    private String event;
    private String data;
    private Integer retry;
    private String comment;

    public Event() {
        this.id = "";
        this.event = "";
        this.data = "";
        this.retry = null;
        this.comment = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getRetry() {
        return retry;
    }

    public void setRetry(Integer retry) {
        this.retry = retry;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Event withId(String id){
        this.setId(id);
        return this;
    }
    public Event withEvent(String event){
        this.setEvent(event);
        return this;
    }
    public Event withData(String data){
        this.setData(data);
        return this;
    }
    public Event withRetry(Integer retry){
        this.setRetry(retry);
        return this;
    }
    public Event withComment(String comment){
        this.setComment(comment);
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        if (this.event != null && !this.event.isEmpty()){
            sb.append("event:").append(this.event).append("\n");
        }
        if (this.id != null && !this.id.isEmpty()){
            sb.append("id:").append(this.id).append("\n");
        }
        if (this.data != null && !this.data.isEmpty()){
            sb.append("data:").append(this.data).append("\n");
        }
        if (this.retry != null){
            sb.append("retry:").append(this.retry).append("\n");
        }

        sb.append("\n");

        return sb.toString();
    }
}
