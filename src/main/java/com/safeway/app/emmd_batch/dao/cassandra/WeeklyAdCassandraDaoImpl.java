package com.safeway.app.emmd_batch.dao.cassandra;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cassandra.core.RowMapper;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.exceptions.DriverException;
import com.safeway.app.emmd_batch.dao.base.CassandraBaseDao;
import com.safeway.app.emmd_batch.model.WeeklyAd;

@Component("weeklyAdCassandraDao")
public class WeeklyAdCassandraDaoImpl extends CassandraBaseDao implements WeeklyAdCassandraDao {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WeeklyAdCassandraDaoImpl.class);
	
	@Value("#{emmdCassandraQuery['store_weekly_ad_insert']}")   
	private String storeWeeklyAdInsert;
	
	@Value("#{emmdCassandraQuery['offer_weekly_ad_insert']}")   
	private String offerWeeklyAdInsert;
	
	@Value("#{emmdCassandraQuery['open_stores_select']}")   
	private String openStoresSelect;
 
	@Value("#{emmdCassandraQuery['fivedollarfriday_select']}")   
	private String fivedollarfriday_Select;
	
	@Autowired
	@Qualifier("cassandraTemplate")
	private CassandraOperations cassandraOperations;
	
	private PreparedStatement storeWeeklyAdInsertPS;
	private PreparedStatement offerWeeklyAdInsertPS;
	
	@PostConstruct
	public void init(){
		storeWeeklyAdInsertPS=cassandraOperations.getSession().prepare(storeWeeklyAdInsert);
		offerWeeklyAdInsertPS=cassandraOperations.getSession().prepare(offerWeeklyAdInsert);
		
	}
	
	public Set<Integer> getOpenStores(){
		Set<Integer> set = new HashSet<Integer>();
		
	
		long ts1 = System.currentTimeMillis();
		long count=0;
		
		LOGGER.info("openStoresSelect="+openStoresSelect);
				
		try{
			List<Integer> results = cassandraOperations.query(openStoresSelect, new RowMapper<Integer>() {

				public Integer mapRow(Row row, int rowNum) throws DriverException {
					Integer store_id = row.getInt("store_id");
					return store_id;
				}
			});
			
			set = new HashSet<Integer>(results);
			count=set.size();
		}
        catch (Exception e) {
        	e.printStackTrace();
        }
		
		long ts2 = System.currentTimeMillis();		
		LOGGER.info("complete getOpenStores="+(ts2 - ts1) / 1000 + " seconds, storeCount="+count);
        
		return set;
	}

	public List<String> getFiveDollarFridayText() {
		LOGGER.info("fivedollarfriday_select="+fivedollarfriday_Select);
		
		List<String> lstText = new ArrayList<String>();
		ResultSet results=cassandraOperations.query(fivedollarfriday_Select);
		Row row = results.one();
		String text = "";
		
		if (row != null) {
			text = row.getString("text");
			LOGGER.debug("$5_Friday_search_strings=" + text);
			StringTokenizer st = new StringTokenizer(text, "|");
			
			while (st.hasMoreElements())
			{
				lstText.add(st.nextElement().toString());
			}
			
		}else{
			lstText=null;
		}
		
		return lstText;
	}
	
	public void saveStoreWeeklyAdData(WeeklyAd wd, List<WeeklyAd> offersAdList)
			throws Exception {
		
		
		BoundStatement bs;
		
		try {
			bs = new BoundStatement(storeWeeklyAdInsertPS);
			bs.bind(wd.getStoreId(),wd.getResponse());
			cassandraOperations.executeAsynchronously(bs);
			saveWeeklyOfferData(offersAdList);
			LOGGER.debug("StoreId="+wd.getStoreId()+", offerCount="+offersAdList.size()+" saved successfully");
			
		} catch (Exception e) {
			throw e;
		}

	}

	public void saveWeeklyOfferData(List<WeeklyAd> weeklyAdList) throws Exception {

		BoundStatement bs;
		
		try {
			bs = new BoundStatement(offerWeeklyAdInsertPS);
			for (WeeklyAd wd: weeklyAdList){				
				bs.bind(wd.getOfferId(),wd.getResponse());
				cassandraOperations.executeAsynchronously(bs);
			}
		} catch (Exception e) {
			throw e;
		}

	}

	
}
