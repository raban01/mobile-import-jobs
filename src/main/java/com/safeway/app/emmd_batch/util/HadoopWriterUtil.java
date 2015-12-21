package com.safeway.app.emmd_batch.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.io.orc.CompressionKind;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Writer;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("hadoopWriterUtil")
public class HadoopWriterUtil<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HadoopWriterUtil.class);

	public Writer getWriter(FileSystem hadoopFs, Configuration hadoopConfiguration, String filePath, Class<T> clas) throws Exception {
		
		LOGGER.debug("filePath: "+filePath);
		LOGGER.debug("clas: "+clas);
		
		Path path =new Path(filePath);
		if(hadoopFs.exists(path)){
			hadoopFs.delete(path, false);
			LOGGER.debug("File deleted.");
		}
		ObjectInspector inspector = ObjectInspectorFactory
				.getReflectionObjectInspector(clas, 
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
		hadoopConfiguration.set(HiveConf.ConfVars.HIVE_ORC_ENCODING_STRATEGY.varname,"COMPRESSION");

		Writer writer = OrcFile.createWriter(hadoopFs, path,
				hadoopConfiguration, inspector, EmmdConstants.STRIPE_SIZE,
				CompressionKind.SNAPPY, EmmdConstants.BUFFER_SIZE,
				EmmdConstants.INDEX_STRIPE_SIZE);
		
		return writer;
	}
	
	
}
