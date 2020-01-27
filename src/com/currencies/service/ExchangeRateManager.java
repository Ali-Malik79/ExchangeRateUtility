/*
 * This is Exchange Rate Manager Service program to provide implementation of 
 * getting URL response and Data Display functions. 
 * 
 * Comments added by Ali Malik - 25.01.2020
 */
package com.currencies.service;

import com.currencies.entities.ExchangeRates;
import com.currencies.util.DbManager;
import com.currencies.util.PropertyManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

public class ExchangeRateManager {

    private String exchangeRateJson;

	public ExchangeRateManager() {
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

/*
 * readRatesFromUrl method gets data from URL and set ArrayList as per table
 * structure. 
 * Comments added by Ali Malik - 25.01.2020
 */
    public ArrayList<ExchangeRates> readRatesFromUrl(String url) throws IOException, JSONException, ParseException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            this.setExchangeRateJson(readAll(rd));
            JSONObject jsData = new JSONObject(this.getExchangeRateJson());
            String fromCurrency = (String) jsData.get("base");
            Date validStartDate = new SimpleDateFormat("yyyy-mm-dd").parse((String) jsData.get("date"));
            JSONObject rateData = (JSONObject) jsData.get("rates");
            Iterator<String> keysItr = rateData.keys();
            ArrayList<ExchangeRates> exchangeRateList = new ArrayList<ExchangeRates>();
            while (keysItr.hasNext()) {
                String toCurrency = keysItr.next();
                double rate = (double) rateData.get(toCurrency);
                ExchangeRates exchangeRateRow = new ExchangeRates();
                exchangeRateRow.setFromCurrency(fromCurrency);
                exchangeRateRow.setToCurrency(toCurrency);
                exchangeRateRow.setValidityStartDate(validStartDate);
                exchangeRateRow.setRate(rate);
                exchangeRateList.add(exchangeRateRow);
              //System.out.println(exchangeRateRow.toString());
            }
            return exchangeRateList;
        } finally {
            is.close();
        }
    }

/*
 * dumpRatesToDb method provides the functionality of storing data into DB table
 * after fetching from URL source. In this function, first records are deleted
 * (refreshed) in DB table, if validity start date and base currency matches. But
 * if the validity date doesn't match, new set of records will be inserted after 
 * updating validity end date of previous records as new date from source data.For 
 * new set of records, the validity start date will be set as new date from Json
 * response. This approach is used just to maintain temporal data for history. So,
 * whenever new rates are received in Json response, the program will backup old
 * rates and insert new rates with new validity date in batch mode.
 * Comments added by Ali Malik - 25.01.2020
*/
    
    public void dumpRatesToDb(ArrayList<ExchangeRates> exchangeRateList) throws SQLException {
        PropertyManager confManager = PropertyManager.getInstance();
        Connection connection = null;
        PreparedStatement pstmt0 = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        try {
        	connection = DbManager.getConnection(confManager.getProperty("db.user_name"), confManager.getProperty("db.password"), confManager.getProperty("db.url"));
            connection.setAutoCommit(false);

            // Delete Duplicate Existing Rates
            String sqlQuery = "DELETE FROM EXCHANGE_RATES WHERE from_currency=? AND  validity_start_date = ?";
            pstmt1 = connection.prepareStatement(sqlQuery);
            ExchangeRates firstExchangeRate = exchangeRateList.get(0);
            pstmt0 = connection.prepareStatement(sqlQuery);
            pstmt0.setString(1, firstExchangeRate.getFromCurrency());
            pstmt0.setDate(2, new java.sql.Date(firstExchangeRate.getValidityStartDate().getTime()));
            pstmt0.execute();

            // Expire Existing Rates
            sqlQuery = "UPDATE EXCHANGE_RATES SET validity_end_date=? WHERE from_currency=? AND ? BETWEEN validity_start_date AND validity_end_date";
            pstmt1 = connection.prepareStatement(sqlQuery);
            pstmt1 = connection.prepareStatement(sqlQuery);
            pstmt1.setDate(1, new java.sql.Date(firstExchangeRate.getValidityStartDate().getTime()));
            pstmt1.setString(2, firstExchangeRate.getFromCurrency());
            pstmt1.setDate(3, new java.sql.Date(firstExchangeRate.getValidityStartDate().getTime()));
            pstmt1.execute();

            // Add New Rates
            sqlQuery = "INSERT INTO EXCHANGE_RATES(from_currency, to_currency, rate, validity_start_date)VALUES(?, ?, ?, ?)";
            pstmt2 = connection.prepareStatement(sqlQuery);

            for (ExchangeRates exchangeRateRow : exchangeRateList) {
              //System.out.println(exchangeRateRow);
                pstmt2.setString(1, exchangeRateRow.getFromCurrency());
                pstmt2.setString(2, exchangeRateRow.getToCurrency());
                pstmt2.setDouble(3, exchangeRateRow.getRate());
                pstmt2.setDate(4, new java.sql.Date(exchangeRateRow.getValidityStartDate().getTime()));
                pstmt2.addBatch();
            }

            int[] result = pstmt2.executeBatch();
            System.out.println("The number of rows inserted: " + result.length);
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            if (pstmt0 != null) {
                pstmt0.close();
            }
            if (pstmt1 != null) {
                pstmt1.close();
            }
            if (pstmt2 != null) {
                pstmt2.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

/*
 * Following displayRateList method is implemented to show active currency rates in
 * descending order at IDE Console as output. 
 * 
 * Note: All display and DB Dump methods are currently creating separate DB connection
 * inside function itself and closed. However, this technique can be further improved
 * by adopting Connection Pooling or Common Connection approach which is usually 
 * required for Web based interfaces or parallel connections. 
 * 
 * Comments added by Ali Malik - 25.01.2020
 */    
    
    public void displayRateList() throws SQLException {
        PropertyManager confManager = PropertyManager.getInstance();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
        	//Class.forName("com.mysql.jdbc.Driver");
            connection = DbManager.getConnection(confManager.getProperty("db.user_name"), confManager.getProperty("db.password"), confManager.getProperty("db.url"));
            String sqlQuery = "SELECT TO_CURRENCY, RATE FROM EXCHANGE_RATES WHERE NOW() BETWEEN VALIDITY_START_DATE AND VALIDITY_END_DATE ORDER BY RATE DESC";
            statement = connection.prepareStatement(sqlQuery);
            ResultSet data = statement.executeQuery();
            System.out.println("\nList of Currencies are:" + "\n\n" + "CURRENCY" + "\tRATE");
            while(data.next()) {
            System.out.println(data.getObject(1) + "\t\t" + data.getObject(2));
            }
            statement.close();
            connection.close();
       
       } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

/* displayRateMinMax function is used to print Minimum and Maximum rate values. In 
 * this function, we are maintaining sortType value as 0, 1 or 2. "0" means both Min and Max 
 * rates will be displayed. "1" means only Min rates will be displayed. "2" means
 * only Max rates will be displayed.  
 *
 * Comments added by Ali Malik - 26.01.2020
 */
    public void displayRateMinMax(int sortType) throws SQLException {
        PropertyManager confManager = PropertyManager.getInstance();
        Connection connection = null;
        PreparedStatement statement = null;
        ExchangeRates exchangeRate=new ExchangeRates() ;
        try {
            String sortTypeWhere = "";

            if (sortType == 1 || sortType == 2) { // return all otherwise
                sortTypeWhere = " WHERE sortType=" + sortType + " ";
            }
            
            connection = DbManager.getConnection(confManager.getProperty("db.user_name"), confManager.getProperty("db.password"), confManager.getProperty("db.url"));
            String sqlQuery = "SELECT * \n"
                    + "FROM   (SELECT t1.exchange_rate_id, \n"
                    + "               t1.from_currency, \n"
                    + "               t1.to_currency, \n"
                    + "               t1.rate, \n"
                    + "               1 AS sortType \n"
                    + "        FROM   cr.exchange_rates t1 \n"
                    + "        WHERE  t1.from_currency = 'EUR' \n"
                    + "               AND Now() BETWEEN t1.validity_start_date AND t1.validity_end_date \n"
                    + "               AND t1.rate = (SELECT Min(t2.rate) \n"
                    + "                              FROM   cr.exchange_rates t2 \n"
                    + "                              WHERE  t2.from_currency = 'EUR' \n"
                    + "                                     AND Now() BETWEEN t2.validity_start_date \n"
                    + "                                                       AND \n"
                    + "                                                       t2.validity_end_date) \n"
                    + "        UNION ALL \n"
                    + "        SELECT t1.exchange_rate_id, \n"
                    + "               t1.from_currency, \n"
                    + "               t1.to_currency, \n"
                    + "               t1.rate, \n"
                    + "               2 AS sortType \n"
                    + "        FROM   cr.exchange_rates t1 \n"
                    + "        WHERE  t1.from_currency = 'EUR' \n"
                    + "               AND Now() BETWEEN t1.validity_start_date AND t1.validity_end_date \n"
                    + "               AND t1.rate = (SELECT Max(t2.rate) \n"
                    + "                              FROM   cr.exchange_rates t2 \n"
                    + "                              WHERE  t2.from_currency = 'EUR' \n"
                    + "                                     AND Now() BETWEEN t2.validity_start_date \n"
                    + "                                                       AND \n"
                    + "                                                       t2.validity_end_date)) t0 \n"
                    + sortTypeWhere
                    + " ORDER  BY sortType ";            
            statement = connection.prepareStatement(sqlQuery);
            ResultSet data = statement.executeQuery();
            System.out.println("\nMin and Max Currencies are:");
            while(data.next()) {
            System.out.println(data.getObject(3) + " - " + data.getObject(4));
            }
            statement.close();
            connection.close();
       } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
/* Following function is built based on PropertyManager singleton instance utility.
 * This program can be further improved to execute as background Job and instance 
 * execution can be recorded. 
 *
 * Comments added by Ali Malik - 25.01.2020
 */
    
    public ArrayList<ExchangeRates> processExchangeRates(String confFile) throws IOException, SQLException, JSONException, ParseException {
        PropertyManager confManager = PropertyManager.getInstance();
        confManager.loadProperties(confFile);
        ArrayList<ExchangeRates> exchangeRateList = this.readRatesFromUrl(confManager.getProperty("ex.url"));
        this.dumpRatesToDb(exchangeRateList);
        return exchangeRateList;
    }

	public String getExchangeRateJson() {
		return exchangeRateJson;
	}

	private void setExchangeRateJson(String exchangeRateJson) {
		this.exchangeRateJson = exchangeRateJson;
	}

}
