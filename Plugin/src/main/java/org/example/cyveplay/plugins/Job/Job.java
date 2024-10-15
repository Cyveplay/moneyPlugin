package org.example.cyveplay.plugins.Job;

public class Job{

    private String title;
    private String description;

    public Job(String title, String description){
        this.title = title;
        this.description = description;
    }


    public String getTitle(){
        return title;
    }
    private String getDescription(){
        return description;
    }
}
