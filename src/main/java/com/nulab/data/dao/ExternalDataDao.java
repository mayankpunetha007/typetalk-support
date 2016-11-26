package com.nulab.data.dao;

import com.nulab.data.dto.ExternalData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Support DB operations from externalData table
 */
@Repository
@Transactional
public interface ExternalDataDao extends CrudRepository<ExternalData, Long> {

    @Override
    ExternalData save(ExternalData externalData);

    @Override
    ExternalData findOne(Long aLong);

    @Override
    boolean exists(Long aLong);

    @Override
    List<ExternalData> findAll();

    @Override
    long count();

    @Override
    void delete(Long aLong);

    @Override
    void delete(ExternalData externalData);

    @Override
    void deleteAll();
}

