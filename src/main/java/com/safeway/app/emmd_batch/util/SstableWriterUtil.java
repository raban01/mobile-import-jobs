package com.safeway.app.emmd_batch.util;

import java.io.File;
import java.io.IOException;

import org.apache.cassandra.config.Config;
import org.apache.cassandra.dht.Murmur3Partitioner;
import org.apache.cassandra.io.sstable.CQLSSTableWriter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("sstableWriterUtil")
public class SstableWriterUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SstableWriterUtil.class);
	
	public CQLSSTableWriter getWriter(String path, String schema, String statment) 
	{
		
		LOGGER.debug("path: "+path);
		LOGGER.debug("schema: "+schema);
		LOGGER.debug("statment: "+statment);
		
		 // magic!
        Config.setClientMode(true);

        
        File outputDir = new File(path);
        if (outputDir.exists()){
        	try {
				FileUtils.cleanDirectory(outputDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        if (!outputDir.exists() && !outputDir.mkdirs())
        {
            throw new RuntimeException("Cannot create output directory: " + outputDir);
        }

        // Prepare SSTable writer
        CQLSSTableWriter.Builder builder = CQLSSTableWriter.builder();
        // set output directory
        builder.inDirectory(outputDir)
               // set target schema
               .forTable(schema).withBufferSizeInMB(32)
               // set CQL statement to put data
               .using(statment)
               // set partitioner if needed
               // default is Murmur3Partitioner so set if you use different one.
               .withPartitioner(new Murmur3Partitioner());
        CQLSSTableWriter writer = builder.build();

		return writer;
	}
	
	
}
