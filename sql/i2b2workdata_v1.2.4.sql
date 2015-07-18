--
-- Create Schema Script 
--   Database Version          : 11.2.0.1.0 
--   Database Compatible Level : 11.2.0.0.0 
--   Script Compatible Level   : 11.2.0.0.0 
--   Toad Version              : 12.1.0.22 
--   DB Connect String         : 10.118.255.5:1521/ORCL5 
--   Schema                    : I2B2WORKDATA 
--   Script Created by         : BIOMART_USER 
--   Script Created at         : 7/17/2015 3:49:12 PM 
--   Physical Location         :  
--   Notes                     :  
--

-- Object Counts: 
--   Indexes: 2         Columns: 2          
--   Tables: 2          Columns: 30         Constraints: 15     


--
-- WORKPLACE  (Table) 
--
CREATE TABLE WORKPLACE
(
  C_NAME                VARCHAR2(255 BYTE)      NOT NULL,
  C_USER_ID             VARCHAR2(255 BYTE)      NOT NULL,
  C_GROUP_ID            VARCHAR2(255 BYTE)      NOT NULL,
  C_SHARE_ID            VARCHAR2(255 BYTE),
  C_INDEX               VARCHAR2(255 BYTE)      NOT NULL,
  C_PARENT_INDEX        VARCHAR2(255 BYTE),
  C_VISUALATTRIBUTES    CHAR(3 BYTE)            NOT NULL,
  C_PROTECTED_ACCESS    CHAR(1 BYTE),
  C_TOOLTIP             VARCHAR2(255 BYTE),
  C_WORK_XML            CLOB,
  C_WORK_XML_SCHEMA     CLOB,
  C_WORK_XML_I2B2_TYPE  VARCHAR2(255 BYTE),
  C_ENTRY_DATE          DATE,
  C_CHANGE_DATE         DATE,
  C_STATUS_CD           CHAR(1 BYTE),
  CONSTRAINT WORKPLACE_PK
  PRIMARY KEY
  (C_INDEX)
  ENABLE VALIDATE
);


--
-- WORKPLACE_ACCESS  (Table) 
--
CREATE TABLE WORKPLACE_ACCESS
(
  C_TABLE_CD          VARCHAR2(255 BYTE)        NOT NULL,
  C_TABLE_NAME        VARCHAR2(255 BYTE)        NOT NULL,
  C_PROTECTED_ACCESS  CHAR(1 BYTE),
  C_HLEVEL            INTEGER                   NOT NULL,
  C_NAME              VARCHAR2(255 BYTE)        NOT NULL,
  C_USER_ID           VARCHAR2(255 BYTE)        NOT NULL,
  C_GROUP_ID          VARCHAR2(255 BYTE)        NOT NULL,
  C_SHARE_ID          VARCHAR2(255 BYTE),
  C_INDEX             VARCHAR2(255 BYTE)        NOT NULL,
  C_PARENT_INDEX      VARCHAR2(255 BYTE),
  C_VISUALATTRIBUTES  CHAR(3 BYTE)              NOT NULL,
  C_TOOLTIP           VARCHAR2(255 BYTE),
  C_ENTRY_DATE        DATE,
  C_CHANGE_DATE       DATE,
  C_STATUS_CD         CHAR(1 BYTE),
  CONSTRAINT WORKPLACE_ACCESS_PK
  PRIMARY KEY
  (C_INDEX)
  ENABLE VALIDATE
);