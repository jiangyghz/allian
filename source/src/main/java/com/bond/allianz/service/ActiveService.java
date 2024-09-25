package com.bond.allianz.service;

import com.bond.allianz.Dao.ActiveDao;
import com.bond.allianz.Dao.CarDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActiveService {
    @Autowired
    ActiveDao activeDao;
    @Autowired
    CarDao carDao;
    public String list_active_subject(String data){return activeDao.list_active_subject(data);}
    public String addorUpdateActive_subject(String data){return activeDao.addorUpdateActive_subject(data);}
    public String add_active(String data){return activeDao.add_active(data);}
    public String get_active(String data){return activeDao.get_active(data);}
    public String add_active_dealer(String data){return activeDao.add_active_dealer(data);}
    public String get_active_dealer(String data){return activeDao.get_active_dealer(data);}
    public String add_active_product(String data){return activeDao.add_active_product(data);}
    public String get_active_product(String data){return activeDao.get_active_product(data);}
    public String add_active_cars(String data){return activeDao.add_active_cars(data);}
    public String get_active_cars(String data){return activeDao.get_active_cars(data);}
    public String get_all_cars(String data){return carDao.get_all_cars(data);}
}
