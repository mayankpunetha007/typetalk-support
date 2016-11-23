package com.nulab.data.dao;

import com.nulab.data.dto.ChatDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Created by mayan on 11/22/2016.
 */
public interface ChatDetailsDao extends CrudRepository<ChatDetails, Long>, Repository<ChatDetails, Long> {
    @Override
    ChatDetails save(ChatDetails externalData);

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
    void delete(ChatDetails externalData);

    @Override
    void deleteAll();
}
