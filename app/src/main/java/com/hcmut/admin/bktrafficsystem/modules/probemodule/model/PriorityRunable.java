package com.hcmut.admin.bktrafficsystem.modules.probemodule.model;

public class PriorityRunable implements Runnable {
    private Priority priority;

    public PriorityRunable(Priority priority) {
        this.priority = priority;
    }

    @Override
    public void run() {

    }

    public Priority getPriority() {
        return priority;
    }
}
