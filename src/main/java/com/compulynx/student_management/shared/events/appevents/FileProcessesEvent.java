package com.compulynx.student_management.shared.events.appevents;

import com.compulynx.student_management.shared.enums.FileProcesses;
import org.springframework.context.ApplicationEvent;

public class FileProcessesEvent extends ApplicationEvent {
    private final int value;
    private final FileProcesses process;
    public FileProcessesEvent(Object source, int count, FileProcesses process){
        super(source);
        this.value =count;
        this.process = process;
    }

    public int getValue() {
        return value;
    }

    public FileProcesses getProcess() {
        return process;
    }
}
