package com.alseinn.semiwood.jobs.contact;

import com.alseinn.semiwood.dao.contact.ContactRepository;
import com.alseinn.semiwood.entity.contact.Contact;
import com.alseinn.semiwood.jobs.concrete.AbstractJob;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DeleteContactsFromDBJob extends AbstractJob {

    private final ContactRepository contactRepository;

    @Value("${job.delete.contact.core.pool.size}")
    private int corePoolSize;
    @Value("${job.delete.contact.period}")
    private int period;
    @Value("${job.delete.contact.date}")
    private int day;

    @Override
    public void process() {
        System.out.println("initial");
        ScheduledExecutorService service = Executors.newScheduledThreadPool(corePoolSize);
        service.scheduleAtFixedRate(() -> {
            System.out.println("work");
            List<Contact> contacts = contactRepository
                    .findAllByIsOkayAndTimeModifiedIsBefore(true, getDateByDay(day));

            if (!contacts.isEmpty()) {
                System.out.println("find");
                contactRepository.deleteAll(contacts);
            }
            System.out.println("success");
        }, 0, period, TimeUnit.DAYS);
        System.out.println("end");
    }
}
