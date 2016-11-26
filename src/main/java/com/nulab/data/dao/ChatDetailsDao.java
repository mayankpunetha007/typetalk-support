package com.nulab.data.dao;

import com.nulab.data.dto.ChatDetails;
import com.nulab.data.dto.SupportTicket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Support db operations from Chat Details Table
 */
@Repository
@Transactional
public interface ChatDetailsDao extends CrudRepository<ChatDetails, Long> {
    @Override
    ChatDetails save(ChatDetails chatDetails);

    @Override
    ChatDetails findOne(Long aLong);

    @Override
    boolean exists(Long aLong);

    @Override
    List<ChatDetails> findAll();

    @Override
    long count();

    @Override
    void delete(Long aLong);

    @Override
    void delete(ChatDetails chatDetails);

    @Override
    void deleteAll();

    List<ChatDetails> findAllBySupportTicket(SupportTicket supportTicket);
}
