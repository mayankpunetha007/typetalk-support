package com.nulab.data.dao;

import com.nulab.data.dto.ChatDetails;
import com.nulab.data.dto.SupportInfo;
import com.nulab.data.dto.SupportTicket;
import com.nulab.data.util.GenericDao;
import org.springframework.stereotype.Service;

/**
 * Created by mayan on 11/19/2016.
 */
@Service("dataAccessService")
public class DataAccessService {

    GenericDao<SupportTicket, Long> supportTicketDao = new GenericDao<SupportTicket, Long>();

    GenericDao<ChatDetails, Long> chatDetailsDao = new GenericDao<ChatDetails, Long>();

    GenericDao<SupportTicket, Long> userDetailsDao = new GenericDao<SupportTicket, Long>();


    public SupportTicket getUSerDetailsById(long userId){
        SupportTicket supportTicket = supportTicketDao.get(SupportTicket.class, userId);
        return supportTicket;
    }

    public SupportTicket addnewsupportTicket(SupportTicket supportTicket){
        return userDetailsDao.save(supportTicket);
    }



}
