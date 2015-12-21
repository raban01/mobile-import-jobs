package com.safeway.app.emmd_batch.util;

import java.util.HashMap;
import java.util.Map;

public class EmmdEnum {

    public static enum EmmdAlertCode {

    	EMMDALERT001("EMMDALERT001", "BeanCreationException, cannot load spring context."),
        EMMDALERT002("EMMDALERT002", "Emmd Batch Job exit with error"),
        EMMDALERT003("EMMDALERT003", "SQL connection error "),
        EMMDALERT004("EMMDALERT004", "Could not create database "),
        EMMDALERT005("EMMDALERT005", "One of the jobs fail "),
        EMMDALERT006("EMMDALERT006", "No data extracted from input file "),
        EMMDALERT007("EMMDALERT007", "Error encountered while writing to MySQL "),
        EMMDALERT008("EMMDALERT008", "Failed to move input file to processed folder "),
        EMMDALERT009("EMMDALERT009", "Failed to restore backup table "),
        EMMDALERT010("EMMDALERT010", "Failed to backup table "),
		EMMDALERT011("EMMDALERT011", "Failed to save data to Hive "),
		EMMDALERT012("EMMDALERT012", "Failed to write data to SSTable "),
		EMMDALERT013("EMMDALERT013", "Failed to write data to Cassandra ");

        private String code;
        private String msg;

        private EmmdAlertCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public static enum EmmcErrorCode {

        EMMCERROR001("EMMCERROR001", "EMAIL FAIL");

        private String code;
        private String msg;

        private EmmcErrorCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
    
    public static enum JobMode {

        FULL("FULL"),
        HIVE("HIVE"),
        CASSANDRA("CASSANDRA");

        private String code;

        private JobMode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
        
    }
    
    public static enum WeeklyAdJobMode {

        FULL("FULL"),
        CASSANDRA("CASSANDRA"),
        REFRESH_EMMD("REFRESH_EMMD"),
        REFRESH_EMMD_LIVE("REFRESH_EMMD_LIVE");

        private String code;

        private WeeklyAdJobMode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
        
    }
    
    public static enum EmmdEnv {

    	LOCAL("localhost:8080"),
    	DEV("ngcp-dv.safeway.com"),
        QAI("ngcp-qi.safeway.com"),
        QA1("ngcp-qa1.safeway.com"),
        QA2("ngcp-qa2.safeway.com"),
        QAC("ngcp-qac.safeway.com"),
        PRF("ngcp-prf.safeway.com"),
        PROD("www.safeway.com");

        private String url;

        private EmmdEnv(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
        
    }
    
    public static enum JobStatus {

        START("START"),
        INPROGRESS("IN_PROGRESS"),
        SUCCESS("SUCCESS"),
        FAIL("FAIL");

        private String code;

        private JobStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
        
    }
}
