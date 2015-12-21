package com.safeway.app.emmd_batch.dao.cassandra.loader.jmx;

import java.io.IOException; 
import java.util.HashMap; 
import java.util.Map;

import javax.management.JMX; 
import javax.management.MBeanServerConnection; 
import javax.management.MalformedObjectNameException; 
import javax.management.ObjectName; 
import javax.management.remote.JMXConnector; 
import javax.management.remote.JMXConnectorFactory; 
import javax.management.remote.JMXServiceURL;

import org.apache.cassandra.service.StorageServiceMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//import com.safeway.app.emmd.jmx.EmmdSSTableLoaderMBean;

@Component("jmxBulkLoader_bk")
public class JmxBulkLoaderImpl2 /*implements JmxBulkLoader*/{
/*
	private static final Logger LOGGER = LoggerFactory.getLogger(JmxBulkLoader.class);

	private JMXConnector connector; 
	private EmmdSSTableLoaderMBean emmdSSTableLoaderMBean;
	
	@Value("${jmx.bulkloader.host}")	
    private String host;
	
	@Value("${jmx.bulkloader.port}")	
    private int port;

	public void connect() throws IOException,MalformedObjectNameException 
	{ 
		LOGGER.info("host="+host);
		LOGGER.info("port="+port);
		
			JMXServiceURL jmxUrl = new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port)); 
			Map<String,Object> env = new HashMap<String,Object>(); 
			connector = JMXConnectorFactory.connect(jmxUrl, env); 
			MBeanServerConnection mbeanServerConn =
			connector.getMBeanServerConnection(); 
			//ObjectName name = new ObjectName("org.apache.cassandra.db:type=StorageService"); 
			//storageBean = JMX.newMBeanProxy(mbeanServerConn, name, StorageServiceMBean.class); 
			String beanName="com.safeway.app.emmd.jmx:name=emmdSSTableLoaderMBean,type=EmmdSSTableLoader";
	    	
			ObjectName name = new ObjectName(beanName); 
			emmdSSTableLoaderMBean = JMX.newMBeanProxy(mbeanServerConn, name, EmmdSSTableLoaderMBean.class); 
			
	}

	public void close() throws IOException 
	{ 
			connector.close(); 
	}

	public void bulkLoad(String path) 
	{ 
		
		emmdSSTableLoaderMBean.bulkLoad(path); 
	}*/
} 
