package com.nulab.data.dao;

import com.nulab.data.dto.SupportTicket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Support db operations from support ticket
 */
@Repository
@Transactional
public interface SupportTicketDao extends CrudRepository<SupportTicket, Long> {

    @Override
    SupportTicket save(SupportTicket supportTicket);

    @Override
    SupportTicket findOne(Long aLong);

    @Override
    boolean exists(Long aLong);

    @Override
    List<SupportTicket> findAll();

    @Override
    long count();

    @Override
    void delete(Long aLong);

    @Override
    void delete(SupportTicket supportTicket);

    SupportTicket findByTopicId(Long topicId);

    SupportTicket findByTopic(String topic);
}
