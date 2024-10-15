package org.example.cyveplay.plugins.Job;

import org.bukkit.event.Listener;

import java.util.*;


public class JobManager implements Listener {

    private Map<String, Job> jobs;


    public JobManager(){
        this.jobs = new HashMap<>();
        jobs.put("Imker", new Job("Imker", "levele mit der Honigernte"));
        jobs.put("Miner", new Job("Miner", "levele mit dem Abbau von Erzen"));
        jobs.put("Metzger", new Job("Metzger", "levele mit dem Töten von Tieren"));
        jobs.put("Baumfäller", new Job("Baumfäller", "levele mit dem Fällen von Bäumen"));
    }

    public Set<String> getAvailableJobs() {
        return jobs.keySet();
    }

    public Job getJob(String arg) {
        return jobs.get(arg);
    }
}



