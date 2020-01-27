/*
 * This is the program to get Currency Exchange Rates from Web URL and performs following
 * functions:
 * 1. Store all currency rates in MySQL Database
 * 2. Display currency rates in Descending Order
 * 3. Display Max. and Min. currency rate values e.g. HRK - 7.4415, NZD - 1.6684
 * 4. Store downloaded list to MD5 hash as fileName
 * 
 * Comments added by Ali Malik - 26.01.2020
 */

package com.currencies;

import com.currencies.entities.ExchangeRates;
import com.currencies.service.ExchangeRateManager;
import com.currencies.util.FileUtil;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppManager {

    public static void main(String[] args) {
        try {
            ExchangeRateManager exchangeRateFetcher = new ExchangeRateManager();
            
            exchangeRateFetcher.processExchangeRates("./conf/app.properties");
            exchangeRateFetcher.displayRateList();
            exchangeRateFetcher.displayRateMinMax(0);
            
            String fileName= FileUtil.getMd5(exchangeRateFetcher.getExchangeRateJson());
            FileUtil.exportJsonFile(exchangeRateFetcher.getExchangeRateJson(), fileName+".json", "./export/");
            
        } catch (Exception ex) {
            Logger.getLogger(ExchangeRateManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
