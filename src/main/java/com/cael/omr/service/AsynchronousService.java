package com.cael.omr.service;

import com.cael.omr.thread.ImportThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class AsynchronousService {
    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    public void executeAsynchronously() {
        ImportThread myThread = applicationContext.getBean(ImportThread.class);
        taskExecutor.execute(myThread);
    }
}
