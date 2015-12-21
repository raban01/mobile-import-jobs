package com.safeway.app.emmd_batch.dao.cassandra;

import java.util.List;
import java.util.Set;

import com.safeway.app.emmd_batch.model.WeeklyAd;



public interface WeeklyAdCassandraDao {
	public Set<Integer> getOpenStores();
	public void saveStoreWeeklyAdData(WeeklyAd wd, List<WeeklyAd> offersAdList) throws Exception ;
	public List<String> getFiveDollarFridayText();
}
