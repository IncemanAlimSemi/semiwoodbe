package com.alseinn.semiwood.dao.contact;

import com.alseinn.semiwood.entity.contact.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findAllByIsOkayAndTimeModifiedIsBefore(Boolean isOkay, Date date);
}
