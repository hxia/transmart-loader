--
-- Create Schema Script 
--   Database Version          : 11.2.0.1.0 
--   Database Compatible Level : 11.2.0.0.0 
--   Script Compatible Level   : 11.2.0.0.0 
--   Toad Version              : 12.1.0.22 
--   DB Connect String         : 10.118.255.5:1521/ORCL5 
--   Schema                    : I2B2METADATA 
--   Script Created by         : BIOMART_USER 
--   Script Created at         : 7/17/2015 3:39:24 PM 
--   Physical Location         :  
--   Notes                     :  
--

-- Object Counts: 
--   Indexes: 6         Columns: 6          
--   Object Privileges: 14 
--   Procedures: 1      Lines of Code: 88 
--   Sequences: 5 
--   Tables: 10         Columns: 172        Constraints: 69     
--   Triggers: 1 


--
-- CUSTOM_META  (Table) 
--
CREATE TABLE CUSTOM_META
(
  C_HLEVEL            NUMBER(22)                NOT NULL,
  C_FULLNAME          VARCHAR2(700)        NOT NULL,
  C_NAME              VARCHAR2(2000)       NOT NULL,
  C_SYNONYM_CD        CHAR(1)              NOT NULL,
  C_VISUALATTRIBUTES  CHAR(3)              NOT NULL,
  C_TOTALNUM          NUMBER(22),
  C_BASECODE          VARCHAR2(50),
  C_METADATAXML       CLOB,
  C_FACTTABLECOLUMN   VARCHAR2(50)         NOT NULL,
  C_TABLENAME         VARCHAR2(50)         NOT NULL,
  C_COLUMNNAME        VARCHAR2(50)         NOT NULL,
  C_COLUMNDATATYPE    VARCHAR2(50)         NOT NULL,
  C_OPERATOR          VARCHAR2(10)         NOT NULL,
  C_DIMCODE           VARCHAR2(700)        NOT NULL,
  C_COMMENT           CLOB,
  C_TOOLTIP           VARCHAR2(900),
  M_APPLIED_PATH      VARCHAR2(700)        NOT NULL,
  UPDATE_DATE         DATE                      NOT NULL,
  DOWNLOAD_DATE       DATE,
  IMPORT_DATE         DATE,
  SOURCESYSTEM_CD     VARCHAR2(50),
  VALUETYPE_CD        VARCHAR2(50),
  M_EXCLUSION_CD      VARCHAR2(25),
  C_PATH              VARCHAR2(700),
  C_SYMBOL            VARCHAR2(50)
);


--
-- I2B2  (Table) 
--
CREATE TABLE I2B2
(
  C_HLEVEL            NUMBER(22)                NOT NULL,
  C_FULLNAME          VARCHAR2(700)        NOT NULL,
  C_NAME              VARCHAR2(2000)       NOT NULL,
  C_SYNONYM_CD        CHAR(1)              NOT NULL,
  C_VISUALATTRIBUTES  CHAR(3)              NOT NULL,
  C_TOTALNUM          NUMBER(22),
  C_BASECODE          VARCHAR2(50),
  C_METADATAXML       CLOB,
  C_FACTTABLECOLUMN   VARCHAR2(50)         NOT NULL,
  C_TABLENAME         VARCHAR2(50)         NOT NULL,
  C_COLUMNNAME        VARCHAR2(50)         NOT NULL,
  C_COLUMNDATATYPE    VARCHAR2(50)         NOT NULL,
  C_OPERATOR          VARCHAR2(10)         NOT NULL,
  C_DIMCODE           VARCHAR2(700)        NOT NULL,
  C_COMMENT           CLOB,
  C_TOOLTIP           VARCHAR2(900),
  M_APPLIED_PATH      VARCHAR2(700),
  UPDATE_DATE         DATE                      NOT NULL,
  DOWNLOAD_DATE       DATE,
  IMPORT_DATE         DATE,
  SOURCESYSTEM_CD     VARCHAR2(50),
  VALUETYPE_CD        VARCHAR2(50),
  M_EXCLUSION_CD      VARCHAR2(25),
  C_PATH              VARCHAR2(700),
  C_SYMBOL            VARCHAR2(50),
  I2B2_ID             NUMBER(22)
);

--
-- I2B2_SECURE  (Table) 
--
CREATE TABLE I2B2_SECURE
(
  C_HLEVEL            NUMBER(22),
  C_FULLNAME          VARCHAR2(900),
  C_NAME              VARCHAR2(2000),
  C_SYNONYM_CD        CHAR(1),
  C_VISUALATTRIBUTES  CHAR(3),
  C_TOTALNUM          NUMBER(22),
  C_BASECODE          VARCHAR2(450),
  C_METADATAXML       CLOB,
  C_FACTTABLECOLUMN   VARCHAR2(50),
  C_TABLENAME         VARCHAR2(50),
  C_COLUMNNAME        VARCHAR2(50),
  C_COLUMNDATATYPE    VARCHAR2(50),
  C_OPERATOR          VARCHAR2(10),
  C_DIMCODE           VARCHAR2(900),
  C_COMMENT           CLOB,
  C_TOOLTIP           VARCHAR2(900),
  UPDATE_DATE         DATE,
  DOWNLOAD_DATE       DATE,
  IMPORT_DATE         DATE,
  SOURCESYSTEM_CD     VARCHAR2(50),
  VALUETYPE_CD        VARCHAR2(50),
  SECURE_OBJ_TOKEN    VARCHAR2(50),
  M_APPLIED_PATH      VARCHAR2(700),
  M_EXCLUSION_CD      VARCHAR2(25)
);


--
-- I2B2_TAGS  (Table) 
--
CREATE TABLE I2B2_TAGS
(
  TAG_ID    NUMBER(18)                          NOT NULL,
  PATH      VARCHAR2(400),
  TAG       VARCHAR2(400),
  TAG_TYPE  VARCHAR2(400),
  TAGS_IDX  NUMBER(22)                          NOT NULL
);


--
-- I2B2_TRIAL_NODES  (Table) 
--
CREATE TABLE I2B2_TRIAL_NODES
(
  C_FULLNAME  VARCHAR2(700)                NOT NULL,
  TRIAL       VARCHAR2(50)
);


--
-- ONT_PROCESS_STATUS  (Table) 
--
CREATE TABLE ONT_PROCESS_STATUS
(
  PROCESS_ID         NUMBER(5),
  PROCESS_TYPE_CD    VARCHAR2(50),
  START_DATE         DATE,
  END_DATE           DATE,
  PROCESS_STEP_CD    VARCHAR2(50),
  PROCESS_STATUS_CD  VARCHAR2(50),
  CRC_UPLOAD_ID      NUMBER(38),
  STATUS_CD          VARCHAR2(50),
  MESSAGE            CLOB,
  ENTRY_DATE         DATE,
  CHANGE_DATE        DATE,
  CHANGEDBY_CHAR     CHAR(50),
  PRIMARY KEY
  (PROCESS_ID)
  ENABLE VALIDATE
);


--
-- SCHEMES  (Table) 
--
CREATE TABLE SCHEMES
(
  C_KEY          VARCHAR2(50)              NOT NULL,
  C_NAME         VARCHAR2(50)              NOT NULL,
  C_DESCRIPTION  VARCHAR2(100),
  CONSTRAINT SCHEMES_PK
  PRIMARY KEY
  (C_KEY)
  ENABLE VALIDATE
);


--
-- TABLE_ACCESS  (Table) 
--
CREATE TABLE TABLE_ACCESS
(
  C_TABLE_CD          VARCHAR2(50)         NOT NULL,
  C_TABLE_NAME        VARCHAR2(50)         NOT NULL,
  C_PROTECTED_ACCESS  CHAR(1),
  C_HLEVEL            NUMBER(22)                NOT NULL,
  C_FULLNAME          VARCHAR2(700)        NOT NULL,
  C_NAME              VARCHAR2(2000)       NOT NULL,
  C_SYNONYM_CD        CHAR(1)              NOT NULL,
  C_VISUALATTRIBUTES  CHAR(3)              NOT NULL,
  C_TOTALNUM          NUMBER(22),
  C_BASECODE          VARCHAR2(50),
  C_METADATAXML       CLOB,
  C_FACTTABLECOLUMN   VARCHAR2(50)         NOT NULL,
  C_DIMTABLENAME      VARCHAR2(50)         NOT NULL,
  C_COLUMNNAME        VARCHAR2(50)         NOT NULL,
  C_COLUMNDATATYPE    VARCHAR2(50)         NOT NULL,
  C_OPERATOR          VARCHAR2(10)         NOT NULL,
  C_DIMCODE           VARCHAR2(700)        NOT NULL,
  C_COMMENT           CLOB,
  C_TOOLTIP           VARCHAR2(900),
  C_ENTRY_DATE        DATE,
  C_CHANGE_DATE       DATE,
  C_STATUS_CD         CHAR(1),
  VALUETYPE_CD        VARCHAR2(50)
);


--
-- I2B2_ID_SEQ  (Sequence) 
--
CREATE SEQUENCE I2B2_ID_SEQ;


--
-- I2B2_TAG_ID_SEQ  (Sequence) 
--
CREATE SEQUENCE I2B2_TAG_ID_SEQ;


--
-- ONT_SQ_PS_PRID  (Sequence) 
--
CREATE SEQUENCE ONT_SQ_PS_PRID;


--
-- SEQ_CONCEPT_CODE  (Sequence) 
--
CREATE SEQUENCE SEQ_CONCEPT_CODE;


--
-- SEQ_I2B2_DATA_ID  (Sequence) 
--
CREATE SEQUENCE SEQ_I2B2_DATA_ID;


--
-- META_APPLIED_PATH_CUSTOM_IDX  (Index) 
--
CREATE INDEX META_APPLIED_PATH_CUSTOM_IDX ON CUSTOM_META
(M_APPLIED_PATH);


--
-- META_APPLIED_PATH_I2B2_IDX  (Index) 
--
CREATE INDEX META_APPLIED_PATH_I2B2_IDX ON I2B2
(M_APPLIED_PATH);


--
-- META_FULLNAME_CUSTOM_IDX  (Index) 
--
CREATE INDEX META_FULLNAME_CUSTOM_IDX ON CUSTOM_META
(C_FULLNAME);


--
-- META_FULLNAME_I2B2_IDX  (Index) 
--
CREATE INDEX META_FULLNAME_I2B2_IDX ON I2B2
(C_FULLNAME);


--
-- UTIL_GRANT_ALL  (Procedure) 
--
CREATE OR REPLACE PROCEDURE              "UTIL_GRANT_ALL"
(username	varchar2 := 'DATATRUST'
,V_WHATTYPE IN VARCHAR2 DEFAULT 'PROCEDURES,FUNCTIONS,TABLES,VIEWS,PACKAGES,SEQUENCES')
AUTHID CURRENT_USER
AS
/*************************************************************************
* Copyright 2008-2012 Janssen Research and Development, LLC.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************/

    v_user      varchar2(2000) := SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA');
	extTable	int;

  begin

	IF UPPER(V_WHATTYPE) LIKE '%TABLE%' THEN
    dbms_output.put_line('Owner ' || v_user  || '   Grantee ' || username);
    dbms_output.put_line('Tables');

     for L_TABLE in (select table_name from user_tables where table_name not like '%EXTRNL%') LOOP

		select count(*) into extTable
		from all_external_tables
		where owner = v_user
		  and table_name = L_TABLE.table_name;
		
       --if L_TABLE.table_name like '%EXTRNL%' then
	    if extTable > 0 then
          --grant select only to External tables
          execute immediate 'grant select on ' || L_TABLE.table_name || ' to ' || username;

       else
          --Grant full permissions on regular tables
          execute immediate 'grant select, insert, update, delete on ' || L_TABLE.table_name || ' to ' || username;
          --DBMS_OUTPUT.put_line('grant select, insert, update, delete on ' || L_TABLE.table_name || ' to ' || username);
       end if;

     END LOOP; --TABLE LOOP
     end if;

	IF UPPER(V_WHATTYPE) LIKE '%VIEW%' THEN
    dbms_output.put_line('Owner ' || v_user  || '   Grantee ' || username);
    dbms_output.put_line('Views');

     for L_VIEW in (select view_name from user_views ) LOOP
          execute immediate 'grant select on ' || L_VIEW.view_name || ' to ' || username;

     END LOOP; --TABLE LOOP
 end if;

 IF UPPER(V_WHATTYPE) LIKE '%PROCEDURE%' or UPPER(V_WHATTYPE) LIKE '%FUNCTION%' or UPPER(V_WHATTYPE) LIKE '%PACKAGE%'  THEN
    dbms_output.put_line(chr(10) || 'Procedures, functions and packages');

    for L_PROCEDURE in (select object_name from user_objects where object_type in ('PROCEDURE', 'FUNCTION', 'PACKAGE') )
     LOOP

       execute immediate 'grant execute on ' || L_PROCEDURE.object_name || ' to ' || username;
      -- DBMS_OUTPUT.put_line('grant execute on ' || L_PROCEDURE.object_name || ' to ' || username);

     END LOOP; --PROCEDURE LOOP
  end if;

 IF UPPER(V_WHATTYPE) LIKE '%SEQUENCE%'  THEN
    dbms_output.put_line(chr(10) || 'Sequence');

    for L_SEQUENCE in (select object_name from user_objects where object_type = 'SEQUENCE' )
     LOOP

       execute immediate 'grant select on ' || L_SEQUENCE.object_name || ' to ' || username;
      -- DBMS_OUTPUT.put_line('grant select on ' || L_SEQUENCE.object_name || ' to ' || username);

     END LOOP; --SEQUENCE LOOP
  end if;

END;
/

--
-- TRG_I2B2_TAG_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_I2B2_TAG_ID 
before insert on "I2B2_TAGS"    
for each row
begin     
  if inserting then       
    if :NEW."TAG_ID" is null then          
      select I2B2_TAG_ID_SEQ.nextval into :NEW."TAG_ID" from dual;       
    end if;    
  end if; 
end;
/


GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON I2B2_TAGS TO BIOMART_USER;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON I2B2_TRIAL_NODES TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON CUSTOM_META TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON I2B2 TO TM_CZ;

GRANT SELECT ON I2B2_ID_SEQ TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON I2B2_SECURE TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON I2B2_TAGS TO TM_CZ;

GRANT SELECT ON I2B2_TAG_ID_SEQ TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON ONT_PROCESS_STATUS TO TM_CZ;

GRANT SELECT ON ONT_SQ_PS_PRID TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SCHEMES TO TM_CZ;

GRANT SELECT ON SEQ_CONCEPT_CODE TO TM_CZ;

GRANT SELECT ON SEQ_I2B2_DATA_ID TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON TABLE_ACCESS TO TM_CZ;