package com.nulab.data.dao;

import com.nulab.data.dto.SupportTicket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mayan on 11/19/2016.
 */
@Repository
@Transactional
public interface SupportTicketDao extends CrudRepository<SupportTicket, Long>{

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

}
