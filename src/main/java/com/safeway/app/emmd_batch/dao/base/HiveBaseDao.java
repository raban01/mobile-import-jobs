package com.safeway.app.emmd_batch.dao.base;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

@Component("hiveBaseDao")
public class HiveBaseDao {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("hiveJdbcTemplate")
	protected JdbcTemplate hiveJdbcTemplate;
	
	@Autowired
	protected ResourceLoader resourceLoader;
	

	public void executeHiveScript(String scriptPath, Map<String, String> params) throws ScriptException, SQLException{	
		long ts1 = System.currentTimeMillis();
		long ts2 = System.currentTimeMillis();
		
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				hiveJdbcTemplate.execute("SET hiveconf:" + entry.getKey() + "=" + entry.getValue());
			}
		}
		
		ScriptUtils.executeSqlScript(hiveJdbcTemplate.getDataSource().getConnection(), resourceLoader.getResource(scriptPath));
		ts2 = System.currentTimeMillis();
		LOGGER.info("Time taken for saving data into HIVE : " + (ts2 - ts1) / 1000 + " seconds");
	}
	
	public List<Map<String, Object>> queryForList(String queryString) throws ScriptException, SQLException{		
		return hiveJdbcTemplate.queryForList(queryString);	
	}
	
	public JdbcTemplate getTemplate(){
		return hiveJdbcTemplate;
	}
}
