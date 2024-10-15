package org.example.cyveplay.plugins.Job;

public class PlayerJob {
    private final Job job;
    private int progress; // Fortschritt im Job (z.B. 10 von 100 Bäumen gefällt)
    private int jobLevel;

    public PlayerJob(Job job) {
        this.job = job;
        this.progress = 0;
    }

    public Job getJob() {
        return job;
    }

    public int getProgress() {
        return progress;
    }

    public int getJobLevel(){
        return jobLevel;
    }
    public void increaseJobLevel(){
        jobLevel++;
    }

    public void incrementProgress(int amount) {
        this.progress += amount;
    }

    public boolean isComplete() {
        // Beispiel: Job ist bei Fortschritt 100 abgeschlossen
        return this.progress >= 100;
    }
}
