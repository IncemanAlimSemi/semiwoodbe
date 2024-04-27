package com.alseinn.semiwood.jobs.concrete;

import jakarta.annotation.PostConstruct;

import java.util.Calendar;
import java.util.Date;

public abstract class AbstractJob {
    @PostConstruct
    public abstract void process() throws InstantiationException, IllegalAccessException;

    public Date getDateByDay(int day) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return calendar.getTime();
    }
}


