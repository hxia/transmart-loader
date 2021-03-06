--
-- Create Schema Script 
--   Database Version          : 11.2.0.1.0 
--   Database Compatible Level : 11.2.0.0.0 
--   Script Compatible Level   : 11.2.0.0.0 
--   Toad Version              : 12.1.0.22 
--   DB Connect String         : 10.118.255.5:1521/ORCL5 
--   Schema                    : I2B2HIVE 
--   Script Created by         : BIOMART_USER 
--   Script Created at         : 7/17/2015 3:48:14 PM 
--   Physical Location         :  
--   Notes                     :  
--

-- Object Counts: 
--   Indexes: 13        Columns: 26         
--   Tables: 10         Columns: 72         Constraints: 43     


--
-- CRC_ANALYSIS_JOB  (Table) 
--
CREATE TABLE CRC_ANALYSIS_JOB
(
  JOB_ID          VARCHAR2(10 BYTE),
  QUEUE_NAME      VARCHAR2(50 BYTE),
  STATUS_TYPE_ID  INTEGER,
  DOMAIN_ID       VARCHAR2(255 BYTE),
  PROJECT_ID      VARCHAR2(500 BYTE),
  USER_ID         VARCHAR2(255 BYTE),
  REQUEST_XML     CLOB,
  CREATE_DATE     DATE,
  UPDATE_DATE     DATE,
  CONSTRAINT ANALSIS_JOB_PK
  PRIMARY KEY
  (JOB_ID)
  ENABLE VALIDATE
);


--
-- CRC_DB_LOOKUP  (Table) 
--
CREATE TABLE CRC_DB_LOOKUP
(
  C_DOMAIN_ID      VARCHAR2(255 BYTE)           NOT NULL,
  C_PROJECT_PATH   VARCHAR2(255 BYTE)           NOT NULL,
  C_OWNER_ID       VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_FULLSCHEMA  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_DATASOURCE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_SERVERTYPE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_NICENAME    VARCHAR2(255 BYTE),
  C_DB_TOOLTIP     VARCHAR2(255 BYTE),
  C_COMMENT        CLOB,
  C_ENTRY_DATE     DATE,
  C_CHANGE_DATE    DATE,
  C_STATUS_CD      CHAR(1 BYTE),
  CONSTRAINT CRC_DB_LOOKUP_PK
  PRIMARY KEY
  (C_DOMAIN_ID, C_PROJECT_PATH, C_OWNER_ID)
  ENABLE VALIDATE
);


--
-- IM_DB_LOOKUP  (Table) 
--
CREATE TABLE IM_DB_LOOKUP
(
  C_DOMAIN_ID      VARCHAR2(255 BYTE)           NOT NULL,
  C_PROJECT_PATH   VARCHAR2(255 BYTE)           NOT NULL,
  C_OWNER_ID       VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_FULLSCHEMA  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_DATASOURCE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_SERVERTYPE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_NICENAME    VARCHAR2(255 BYTE),
  C_DB_TOOLTIP     VARCHAR2(255 BYTE),
  C_COMMENT        CLOB,
  C_ENTRY_DATE     DATE,
  C_CHANGE_DATE    DATE,
  C_STATUS_CD      CHAR(1 BYTE),
  CONSTRAINT IM_DB_LOOKUP_PK
  PRIMARY KEY
  (C_DOMAIN_ID, C_PROJECT_PATH, C_OWNER_ID)
  ENABLE VALIDATE
);


--
-- JMS_MESSAGES  (Table) 
--
CREATE TABLE JMS_MESSAGES
(
  MESSAGEID    INTEGER                          NOT NULL,
  DESTINATION  VARCHAR2(255 BYTE)               NOT NULL,
  TXID         INTEGER,
  TXOP         CHAR(1 BYTE),
  MESSAGEBLOB  BLOB,
  PRIMARY KEY
  (MESSAGEID, DESTINATION)
  ENABLE VALIDATE
);


--
-- JMS_ROLES  (Table) 
--
CREATE TABLE JMS_ROLES
(
  ROLEID  VARCHAR2(32 BYTE)                     NOT NULL,
  USERID  VARCHAR2(32 BYTE)                     NOT NULL,
  PRIMARY KEY
  (USERID, ROLEID)
  ENABLE VALIDATE
);


--
-- JMS_SUBSCRIPTIONS  (Table) 
--
CREATE TABLE JMS_SUBSCRIPTIONS
(
  CLIENTID  VARCHAR2(128 BYTE)                  NOT NULL,
  SUBNAME   VARCHAR2(128 BYTE)                  NOT NULL,
  TOPIC     VARCHAR2(255 BYTE)                  NOT NULL,
  SELECTOR  VARCHAR2(255 BYTE),
  PRIMARY KEY
  (CLIENTID, SUBNAME)
  ENABLE VALIDATE
);


--
-- JMS_TRANSACTIONS  (Table) 
--
CREATE TABLE JMS_TRANSACTIONS
(
  TXID  INTEGER,
  PRIMARY KEY
  (TXID)
  ENABLE VALIDATE
);


--
-- JMS_USERS  (Table) 
--
CREATE TABLE JMS_USERS
(
  USERID    VARCHAR2(32 BYTE)                   NOT NULL,
  PASSWD    VARCHAR2(32 BYTE)                   NOT NULL,
  CLIENTID  VARCHAR2(128 BYTE),
  PRIMARY KEY
  (USERID)
  ENABLE VALIDATE
);


--
-- ONT_DB_LOOKUP  (Table) 
--
CREATE TABLE ONT_DB_LOOKUP
(
  C_DOMAIN_ID      VARCHAR2(255 BYTE)           NOT NULL,
  C_PROJECT_PATH   VARCHAR2(255 BYTE)           NOT NULL,
  C_OWNER_ID       VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_FULLSCHEMA  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_DATASOURCE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_SERVERTYPE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_NICENAME    VARCHAR2(255 BYTE),
  C_DB_TOOLTIP     VARCHAR2(255 BYTE),
  C_COMMENT        CLOB,
  C_ENTRY_DATE     DATE,
  C_CHANGE_DATE    DATE,
  C_STATUS_CD      CHAR(1 BYTE),
  CONSTRAINT ONT_DB_LOOKUP_PK
  PRIMARY KEY
  (C_DOMAIN_ID, C_PROJECT_PATH, C_OWNER_ID)
  ENABLE VALIDATE
);


--
-- WORK_DB_LOOKUP  (Table) 
--
CREATE TABLE WORK_DB_LOOKUP
(
  C_DOMAIN_ID      VARCHAR2(255 BYTE)           NOT NULL,
  C_PROJECT_PATH   VARCHAR2(255 BYTE)           NOT NULL,
  C_OWNER_ID       VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_FULLSCHEMA  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_DATASOURCE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_SERVERTYPE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_NICENAME    VARCHAR2(255 BYTE),
  C_DB_TOOLTIP     VARCHAR2(255 BYTE),
  C_COMMENT        CLOB,
  C_ENTRY_DATE     DATE,
  C_CHANGE_DATE    DATE,
  C_STATUS_CD      CHAR(1 BYTE),
  CONSTRAINT WORK_DB_LOOKUP_PK
  PRIMARY KEY
  (C_DOMAIN_ID, C_PROJECT_PATH, C_OWNER_ID)
  ENABLE VALIDATE
);






--
-- CRC_IDX_AJ_QNSTID  (Index) 
--
CREATE INDEX CRC_IDX_AJ_QNSTID ON CRC_ANALYSIS_JOB
(QUEUE_NAME, STATUS_TYPE_ID);




--
-- JMS_MESSAGES_DESTINATION  (Index) 
--
CREATE INDEX JMS_MESSAGES_DESTINATION ON JMS_MESSAGES
(DESTINATION);


--
-- JMS_MESSAGES_TXOP_TXID  (Index) 
--
CREATE INDEX JMS_MESSAGES_TXOP_TXID ON JMS_MESSAGES
(TXOP, TXID);