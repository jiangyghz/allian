package com.bond.allianz.service;

import com.bond.allianz.Dao.ActDao;
import com.bond.allianz.Dao.ContractDao;
import com.bond.allianz.Dao.invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class invoiceService {
    @Autowired
    private invoice invoiceDao;

    public String Register(){return invoiceDao.Register();}
    public String InvoiceBlueOneItemAndPreferentialPolicy(String data){return invoiceDao.InvoiceBlueOneItemAndPreferentialPolicy(data);}
    public void autoinvoicerequest(){

        try
        {
            invoiceDao.updateInvoiceNo();
        }catch (Exception ee)
        {

        }


    }
    public void AutoInvoice(){

        try
        {
            invoiceDao.AutoInvoice();
        }catch (Exception ee)
        {

        }


    }
}
