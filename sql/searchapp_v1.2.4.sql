--
-- Create Schema Script 
--   Database Version          : 11.2.0.1.0 
--   Database Compatible Level : 11.2.0.0.0 
--   Script Compatible Level   : 11.2.0.0.0 
--   Toad Version              : 12.1.0.22 
--   DB Connect String         : 10.118.255.5:1521/ORCL5 
--   Schema                    : SEARCHAPP 
--   Script Created by         : BIOMART_USER 
--   Script Created at         : 7/17/2015 3:26:49 PM 
--   Physical Location         :  
--   Notes                     :  
--

-- Object Counts: 
--   Functions: 2       Lines of Code: 46 
--   Indexes: 26        Columns: 28         
--   Materialized Views: 1 
--   Object Privileges: 74 
--   Procedures: 1      Lines of Code: 91 
--   Sequences: 6 
--   Tables: 31         Columns: 217        Constraints: 103    
--   Triggers: 12 
--   Views: 14          Columns: 47         


--
-- MLOG$_SEARCH_GENE_SIGNATUR  (Table) 
--
CREATE TABLE MLOG$_SEARCH_GENE_SIGNATUR
(
  DELETED_FLAG     NUMBER(1),
  PUBLIC_FLAG      NUMBER(1),
  M_ROW$$          VARCHAR2(255),
  SEQUENCE$$       NUMBER,
  SNAPTIME$$       DATE,
  DMLTYPE$$        VARCHAR2(1),
  OLD_NEW$$        VARCHAR2(1),
  CHANGE_VECTOR$$  RAW(255)
);

COMMENT ON TABLE MLOG$_SEARCH_GENE_SIGNATUR IS 'snapshot log for master table SEARCHAPP.SEARCH_GENE_SIGNATURE';


--
-- PLUGIN  (Table) 
--
CREATE TABLE PLUGIN
(
  PLUGIN_SEQ    NUMBER                          NOT NULL,
  NAME          VARCHAR2(200)              NOT NULL,
  PLUGIN_NAME   VARCHAR2(90)               NOT NULL,
  HAS_MODULES   CHAR(1)                    DEFAULT 'N'                   NOT NULL,
  HAS_FORM      CHAR(1)                    DEFAULT 'N'                   NOT NULL,
  DEFAULT_LINK  VARCHAR2(70)               NOT NULL,
  FORM_LINK     VARCHAR2(70),
  FORM_PAGE     VARCHAR2(100),
  ACTIVE        CHAR(1)
);


--
-- PLUGIN_MODULE  (Table) 
--
CREATE TABLE PLUGIN_MODULE
(
  MODULE_SEQ   NUMBER                           NOT NULL,
  PLUGIN_SEQ   NUMBER                           NOT NULL,
  NAME         VARCHAR2(70)                NOT NULL,
  PARAMS       CLOB                             NOT NULL,
  VERSION      VARCHAR2(10)                DEFAULT 0.1                   NOT NULL,
  ACTIVE       CHAR(1)                     DEFAULT 'Y'                   NOT NULL,
  HAS_FORM     CHAR(1)                     DEFAULT 'N'                   NOT NULL,
  FORM_LINK    VARCHAR2(90),
  FORM_PAGE    VARCHAR2(90),
  MODULE_NAME  VARCHAR2(50)                NOT NULL,
  CATEGORY     VARCHAR2(50)
);


--
-- REPORT  (Table) 
--
CREATE TABLE REPORT
(
  REPORT_ID     NUMBER                          NOT NULL,
  NAME          VARCHAR2(200),
  DESCRIPTION   VARCHAR2(1000),
  CREATINGUSER  VARCHAR2(200),
  PUBLIC_FLAG   CHAR(1),
  CREATE_DATE   TIMESTAMP(1),
  STUDY         VARCHAR2(200)
);


--
-- REPORT_ITEM  (Table) 
--
CREATE TABLE REPORT_ITEM
(
  REPORT_ITEM_ID  NUMBER                        NOT NULL,
  REPORT_ID       NUMBER                        NOT NULL,
  CODE            VARCHAR2(200)
);


--
-- SEARCH_APP_ACCESS_LOG  (Table) 
--
CREATE TABLE SEARCH_APP_ACCESS_LOG
(
  ID             NUMBER(19),
  ACCESS_TIME    TIMESTAMP(6),
  EVENT          VARCHAR2(255 CHAR),
  REQUEST_URL    VARCHAR2(255 CHAR),
  USER_NAME      VARCHAR2(255 CHAR),
  EVENT_MESSAGE  CLOB
);


--
-- SEARCH_AUTH_PRINCIPAL  (Table) 
--
CREATE TABLE SEARCH_AUTH_PRINCIPAL
(
  ID              NUMBER(19)                    NOT NULL,
  PRINCIPAL_TYPE  VARCHAR2(255),
  DATE_CREATED    DATE                          NOT NULL,
  DESCRIPTION     VARCHAR2(255),
  LAST_UPDATED    DATE                          NOT NULL,
  NAME            VARCHAR2(255),
  UNIQUE_ID       VARCHAR2(255),
  ENABLED         NUMBER(1),
  CONSTRAINT PK_SEARCH_PRINCIPAL
  PRIMARY KEY
  (ID)
  ENABLE VALIDATE
);


--
-- SEARCH_AUTH_USER  (Table) 
--
CREATE TABLE SEARCH_AUTH_USER
(
  ID              NUMBER(19),
  EMAIL           VARCHAR2(255 CHAR),
  EMAIL_SHOW      NUMBER(1),
  PASSWD          VARCHAR2(255 CHAR),
  USER_REAL_NAME  VARCHAR2(255 CHAR),
  USERNAME        VARCHAR2(255 CHAR),
  FEDERATED_ID    VARCHAR2(255),
  PRIMARY KEY
  (ID)
  ENABLE VALIDATE,
  CONSTRAINT SH_AUTH_USER_ID_FK 
  FOREIGN KEY (ID) 
  REFERENCES SEARCH_AUTH_PRINCIPAL (ID)
  ENABLE VALIDATE
);


--
-- SEARCH_CUSTOM_FILTER  (Table) 
--
CREATE TABLE SEARCH_CUSTOM_FILTER
(
  SEARCH_CUSTOM_FILTER_ID  NUMBER(18)           NOT NULL,
  SEARCH_USER_ID           NUMBER(18)           NOT NULL,
  NAME                     NVARCHAR2(200)       NOT NULL,
  DESCRIPTION              NVARCHAR2(2000),
  PRIVATE                  CHAR(1)         DEFAULT 'N'                   NOT NULL,
  CONSTRAINT SEARCH_CUSTOM_FILTER_PK
  PRIMARY KEY
  (SEARCH_CUSTOM_FILTER_ID)
  ENABLE VALIDATE
);


--
-- SEARCH_CUSTOM_FILTER_ITEM  (Table) 
--
CREATE TABLE SEARCH_CUSTOM_FILTER_ITEM
(
  SEARCH_CUSTOM_FILTER_ITEM_ID  NUMBER(18)      NOT NULL,
  SEARCH_CUSTOM_FILTER_ID       NUMBER(18)      NOT NULL,
  UNIQUE_ID                     VARCHAR2(200 CHAR) NOT NULL,
  BIO_DATA_TYPE                 VARCHAR2(100 CHAR) NOT NULL,
  CONSTRAINT SEARCH_CUST_FIL_ITEM_PK
  PRIMARY KEY
  (SEARCH_CUSTOM_FILTER_ITEM_ID)
  ENABLE VALIDATE
);


--
-- SEARCH_GENE_SIGNATURE_ITEM  (Table) 
--
CREATE TABLE SEARCH_GENE_SIGNATURE_ITEM
(
  SEARCH_GENE_SIGNATURE_ID    NUMBER            NOT NULL,
  BIO_MARKER_ID               NUMBER,
  FOLD_CHG_METRIC             NUMBER,
  BIO_DATA_UNIQUE_ID          NVARCHAR2(200),
  ID                          NUMBER            NOT NULL,
  BIO_ASSAY_FEATURE_GROUP_ID  NUMBER(18),
  PROBESET_ID                 NUMBER(22),
  CONSTRAINT SEARCH_GENE_SIGNATURE_ITE_PK
  PRIMARY KEY
  (ID)
  ENABLE VALIDATE
);

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE_ITEM.SEARCH_GENE_SIGNATURE_ID IS 'associated gene signature';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE_ITEM.BIO_MARKER_ID IS 'link to bio_marker table ';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE_ITEM.FOLD_CHG_METRIC IS 'the corresponding fold change value metric (actual number or -1,0,1 for composite gene signatures). If null, it''s assumed to be a gene list in which case all genes are assumed to be up regulated';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE_ITEM.BIO_DATA_UNIQUE_ID IS 'link to unique_id from bio_data_uid table (context sensitive)';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE_ITEM.ID IS 'hibernate primary key';


--
-- SEARCH_GENE_SIG_FILE_SCHEMA  (Table) 
--
CREATE TABLE SEARCH_GENE_SIG_FILE_SCHEMA
(
  SEARCH_GENE_SIG_FILE_SCHEMA_ID  NUMBER        NOT NULL,
  NAME                            VARCHAR2(100) NOT NULL,
  DESCRIPTION                     VARCHAR2(255),
  NUMBER_COLUMNS                  NUMBER        DEFAULT 2                     NOT NULL,
  SUPPORTED                       NUMBER(1)     DEFAULT 0                     NOT NULL,
  CONSTRAINT SEARCH_GENE_SIG_FILE_SCHE_PK
  PRIMARY KEY
  (SEARCH_GENE_SIG_FILE_SCHEMA_ID)
  ENABLE VALIDATE
);

COMMENT ON TABLE SEARCH_GENE_SIG_FILE_SCHEMA IS 'Represents file schemas used to represent a gene signature upload. Normally this table would be populated only by seed data';

COMMENT ON COLUMN SEARCH_GENE_SIG_FILE_SCHEMA.SEARCH_GENE_SIG_FILE_SCHEMA_ID IS 'primary key';

COMMENT ON COLUMN SEARCH_GENE_SIG_FILE_SCHEMA.NAME IS 'name of the file schema';

COMMENT ON COLUMN SEARCH_GENE_SIG_FILE_SCHEMA.NUMBER_COLUMNS IS 'number of columns in tab delimited file';

COMMENT ON COLUMN SEARCH_GENE_SIG_FILE_SCHEMA.SUPPORTED IS 'a binary flag indicates if schema is supported by the application';


--
-- SEARCH_KEYWORD  (Table) 
--
CREATE TABLE SEARCH_KEYWORD
(
  KEYWORD                NVARCHAR2(200),
  BIO_DATA_ID            NUMBER(18),
  UNIQUE_ID              NVARCHAR2(500)         NOT NULL,
  SEARCH_KEYWORD_ID      NUMBER(18),
  DATA_CATEGORY          NVARCHAR2(200)         NOT NULL,
  SOURCE_CODE            NVARCHAR2(100),
  DISPLAY_DATA_CATEGORY  NVARCHAR2(200),
  OWNER_AUTH_USER_ID     NUMBER,
  CONSTRAINT SEARCH_KW_PK
  PRIMARY KEY
  (SEARCH_KEYWORD_ID)
  ENABLE VALIDATE,
  CONSTRAINT SEARCH_KEYWORD_UK
  UNIQUE (UNIQUE_ID, DATA_CATEGORY)
  ENABLE VALIDATE
);

COMMENT ON COLUMN SEARCH_KEYWORD.OWNER_AUTH_USER_ID IS 'the owner of the object, this can be used to control access permissions in search';


--
-- SEARCH_KEYWORD_TERM  (Table) 
--
CREATE TABLE SEARCH_KEYWORD_TERM
(
  KEYWORD_TERM            VARCHAR2(200),
  SEARCH_KEYWORD_ID       NUMBER(18),
  RANK                    NUMBER(18),
  SEARCH_KEYWORD_TERM_ID  NUMBER(18),
  TERM_LENGTH             NUMBER(18),
  OWNER_AUTH_USER_ID      NUMBER,
  DATA_CATEGORY           NVARCHAR2(200),
  CONSTRAINT SEARCH_KW_TERM_PK
  PRIMARY KEY
  (SEARCH_KEYWORD_TERM_ID)
  ENABLE VALIDATE,
  CONSTRAINT SEARCH_KW_FK 
  FOREIGN KEY (SEARCH_KEYWORD_ID) 
  REFERENCES SEARCH_KEYWORD (SEARCH_KEYWORD_ID)
  ENABLE VALIDATE
);

COMMENT ON COLUMN SEARCH_KEYWORD_TERM.OWNER_AUTH_USER_ID IS 'owner of the object, this can be used to control access in search';


--
-- SEARCH_REQUEST_MAP  (Table) 
--
CREATE TABLE SEARCH_REQUEST_MAP
(
  ID                NUMBER(19),
  VERSION           NUMBER(19),
  CONFIG_ATTRIBUTE  VARCHAR2(255 CHAR),
  URL               VARCHAR2(255 CHAR)
);


--
-- SEARCH_ROLE  (Table) 
--
CREATE TABLE SEARCH_ROLE
(
  ID           NUMBER(19),
  VERSION      NUMBER(19),
  AUTHORITY    VARCHAR2(255 CHAR),
  DESCRIPTION  VARCHAR2(255 CHAR),
  PRIMARY KEY
  (ID)
  ENABLE VALIDATE
);


--
-- SEARCH_ROLE_AUTH_USER  (Table) 
--
CREATE TABLE SEARCH_ROLE_AUTH_USER
(
  PEOPLE_ID       NUMBER(19),
  AUTHORITIES_ID  NUMBER(19),
  CONSTRAINT FKFB14EF79287E0CAC 
  FOREIGN KEY (AUTHORITIES_ID) 
  REFERENCES SEARCH_AUTH_USER (ID)
  ENABLE VALIDATE,
  CONSTRAINT FKFB14EF798F01F561 
  FOREIGN KEY (PEOPLE_ID) 
  REFERENCES SEARCH_ROLE (ID)
  ENABLE VALIDATE
);


--
-- SEARCH_SECURE_OBJECT  (Table) 
--
CREATE TABLE SEARCH_SECURE_OBJECT
(
  SEARCH_SECURE_OBJECT_ID  NUMBER(18),
  BIO_DATA_ID              NUMBER(18),
  DISPLAY_NAME             NVARCHAR2(100),
  DATA_TYPE                NVARCHAR2(200),
  BIO_DATA_UNIQUE_ID       NVARCHAR2(200),
  CONSTRAINT SEARCH_SEC_OBJ_PK
  PRIMARY KEY
  (SEARCH_SECURE_OBJECT_ID)
  ENABLE VALIDATE
);


--
-- SEARCH_SECURE_OBJECT_PATH  (Table) 
--
CREATE TABLE SEARCH_SECURE_OBJECT_PATH
(
  SEARCH_SECURE_OBJECT_ID    NUMBER(18),
  I2B2_CONCEPT_PATH          NVARCHAR2(2000),
  SEARCH_SECURE_OBJ_PATH_ID  NUMBER(18)         NOT NULL,
  CONSTRAINT SEARCH_SEC_OBJ__PATH_PK
  PRIMARY KEY
  (SEARCH_SECURE_OBJ_PATH_ID)
  ENABLE VALIDATE
);


--
-- SEARCH_SEC_ACCESS_LEVEL  (Table) 
--
CREATE TABLE SEARCH_SEC_ACCESS_LEVEL
(
  SEARCH_SEC_ACCESS_LEVEL_ID  NUMBER(18),
  ACCESS_LEVEL_NAME           NVARCHAR2(200),
  ACCESS_LEVEL_VALUE          NUMBER(18),
  CONSTRAINT SEARCH_SEC_AC_LEVEL_PK
  PRIMARY KEY
  (SEARCH_SEC_ACCESS_LEVEL_ID)
  ENABLE VALIDATE
);


--
-- SEARCH_TAXONOMY  (Table) 
--
CREATE TABLE SEARCH_TAXONOMY
(
  TERM_ID            NUMBER(22)                 NOT NULL,
  TERM_NAME          VARCHAR2(900)         NOT NULL,
  SOURCE_CD          VARCHAR2(900),
  IMPORT_DATE        TIMESTAMP(1)               DEFAULT Sysdate,
  SEARCH_KEYWORD_ID  NUMBER(38),
  CONSTRAINT SEARCH_TAXONOMY_PK
  PRIMARY KEY
  (TERM_ID)
  ENABLE VALIDATE,
  CONSTRAINT FK_SEARCH_TAX_SEARCH_KEYWORD 
  FOREIGN KEY (SEARCH_KEYWORD_ID) 
  REFERENCES SEARCH_KEYWORD (SEARCH_KEYWORD_ID)
  ENABLE VALIDATE
);


--
-- SEARCH_TAXONOMY_RELS  (Table) 
--
CREATE TABLE SEARCH_TAXONOMY_RELS
(
  SEARCH_TAXONOMY_RELS_ID  NUMBER(22),
  CHILD_ID                 NUMBER(22)           NOT NULL,
  PARENT_ID                NUMBER(22),
  PRIMARY KEY
  (SEARCH_TAXONOMY_RELS_ID)
  ENABLE VALIDATE,
  CONSTRAINT U_CHILD_ID_PARENT_ID
  UNIQUE (CHILD_ID, PARENT_ID)
  ENABLE VALIDATE,
  CONSTRAINT FK_SEARCH_TAX_RELS_CHILD 
  FOREIGN KEY (CHILD_ID) 
  REFERENCES SEARCH_TAXONOMY (TERM_ID)
  ENABLE VALIDATE,
  CONSTRAINT FK_SEARCH_TAX_RELS_PARENT 
  FOREIGN KEY (PARENT_ID) 
  REFERENCES SEARCH_TAXONOMY (TERM_ID)
  ENABLE VALIDATE
);


--
-- SEARCH_USER_FEEDBACK  (Table) 
--
CREATE TABLE SEARCH_USER_FEEDBACK
(
  SEARCH_USER_FEEDBACK_ID  NUMBER(18),
  SEARCH_USER_ID           NUMBER(18),
  CREATE_DATE              DATE,
  FEEDBACK_TEXT            NVARCHAR2(2000),
  APP_VERSION              NVARCHAR2(100),
  CONSTRAINT RDT_SEARCH_USER_FDBK_PK
  PRIMARY KEY
  (SEARCH_USER_FEEDBACK_ID)
  ENABLE VALIDATE
);


--
-- SEARCH_USER_SETTINGS  (Table) 
--
CREATE TABLE SEARCH_USER_SETTINGS
(
  ID             NUMBER,
  USER_ID        NUMBER,
  SETTING_NAME   VARCHAR2(255),
  SETTING_VALUE  VARCHAR2(1024)
);


--
-- SUBSET  (Table) 
--
CREATE TABLE SUBSET
(
  SUBSET_ID          NUMBER                     NOT NULL,
  DESCRIPTION        VARCHAR2(1000)        NOT NULL,
  CREATE_DATE        TIMESTAMP(6)               NOT NULL,
  CREATING_USER      VARCHAR2(200)         NOT NULL,
  PUBLIC_FLAG        NUMBER(1)                  DEFAULT 0                     NOT NULL,
  DELETED_FLAG       NUMBER(1)                  DEFAULT 0                     NOT NULL,
  QUERY_MASTER_ID_1  NUMBER                     NOT NULL,
  QUERY_MASTER_ID_2  NUMBER,
  STUDY              VARCHAR2(200),
  CONSTRAINT SUBSET_PK
  PRIMARY KEY
  (SUBSET_ID)
  ENABLE VALIDATE
);


--
-- HIBERNATE_SEQUENCE  (Sequence) 
--
CREATE SEQUENCE HIBERNATE_SEQUENCE;


--
-- PLUGIN_MODULE_SEQ  (Sequence) 
--
CREATE SEQUENCE PLUGIN_MODULE_SEQ;


--
-- PLUGIN_SEQ  (Sequence) 
--
CREATE SEQUENCE PLUGIN_SEQ;


--
-- SEQ_SEARCH_DATA_ID  (Sequence) 
--
CREATE SEQUENCE SEQ_SEARCH_DATA_ID;


--
-- SEQ_SEARCH_TAXONOMY_RELS_ID  (Sequence) 
--
CREATE SEQUENCE SEQ_SEARCH_TAXONOMY_RELS_ID;


--
-- SEQ_SEARCH_TAXONOMY_TERM_ID  (Sequence) 
--
CREATE SEQUENCE SEQ_SEARCH_TAXONOMY_TERM_ID;


--
-- SEARCH_KEYWORD_INDEX1  (Index) 
--
CREATE INDEX SEARCH_KEYWORD_INDEX1 ON SEARCH_KEYWORD(KEYWORD);


--
-- SEARCH_KEYWORD_INDEX2  (Index) 
--
CREATE INDEX SEARCH_KEYWORD_INDEX2 ON SEARCH_KEYWORD(BIO_DATA_ID);


--
-- SEARCH_KEYWORD_INDEX3  (Index) 
--
CREATE INDEX SEARCH_KEYWORD_INDEX3 ON SEARCH_KEYWORD(OWNER_AUTH_USER_ID);

--
-- SEARCH_KW_TERM_SKID_IDX  (Index) 
--
CREATE INDEX SEARCH_KW_TERM_SKID_IDX ON SEARCH_KEYWORD_TERM(SEARCH_KEYWORD_ID);


--
-- UTIL_GRANT_ALL  (Procedure) 
--
CREATE OR REPLACE PROCEDURE           "UTIL_GRANT_ALL"
(username	varchar2 := 'DATATRUST'
,V_WHATTYPE IN VARCHAR2 DEFAULT 'PROCEDURES,FUNCTIONS,TABLES,VIEWS,PACKAGES,SEQUENCE')
AUTHID CURRENT_USER
AS
-------------------------------------------------------------------------------------
-- NAME: UTIL_GRANT_ALL
--
-- Copyright c 2011 Recombinant Data Corp.
--

--------------------------------------------------------------------------------------

    --GRANTS DATATRUST POSSIBLE PERMISSIONS
    --ON OBJECTS OWNED BY THE CURRENT USER
	
	--	JEA@20110901	Added parameter to allow username other than DATATRUST, look for EXTRNL as external table names
	--	JEA@20120223	Added grant drop any table, grant analyze any to TABLES routine
	--	JEA@20120226	Added additional grants to TABLES routine

    v_user      varchar2(2000) := SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA');

  begin

	IF UPPER(V_WHATTYPE) LIKE '%TABLE%' THEN
    dbms_output.put_line('Owner ' || v_user  || '   Grantee ' || username);
    dbms_output.put_line('Tables');

     for L_TABLE in (select table_name from user_tables where table_name not like '%EXTRNL%') LOOP

       if L_TABLE.table_name like '%EXTRNL%' then
          --grant select only to External tables
          execute immediate 'grant select on ' || L_TABLE.table_name || ' to ' || username;

       else
          --Grant full permissions on regular tables
          execute immediate 'grant select, insert, update, delete on ' || L_TABLE.table_name || ' to ' || username;
          --DBMS_OUTPUT.put_line('grant select, insert, update, delete on ' || L_TABLE.table_name || ' to ' || username);
       end if;

     END LOOP; --TABLE LOOP
	
	 execute immediate 'grant create any table to ' || username;
	 execute immediate 'grant drop any table to ' || username;
	 execute immediate 'grant alter any table to ' || username;
	 execute immediate 'grant create any index to ' || username;
	 execute immediate 'grant drop any index to ' || username;
	 execute immediate 'grant analyze any to ' || username;
	
     end if;

	IF UPPER(V_WHATTYPE) LIKE '%VIEW%' THEN
    dbms_output.put_line('Owner ' || v_user  || '   Grantee ' || username);
    dbms_output.put_line('Views');

     for L_VIEW in (select view_name from user_views ) LOOP
          execute immediate 'grant select on ' || L_VIEW.view_name || ' to ' || username;

     END LOOP; --TABLE LOOP
 end if;

 IF UPPER(V_WHATTYPE) LIKE '%PROCEDURE%' or UPPER(V_WHATTYPE) LIKE '%FUNCTION%' or UPPER(V_WHATTYPE) LIKE '%PACKAGE%' THEN
    dbms_output.put_line(chr(10) || 'Procedures, functions and packages');

    for L_PROCEDURE in (select object_name from user_objects where object_type in ('PROCEDURE', 'FUNCTION', 'PACKAGE') )
     LOOP

       execute immediate 'grant execute on ' || L_PROCEDURE.object_name || ' to ' || username;
      -- DBMS_OUTPUT.put_line('grant execute on ' || L_PROCEDURE.object_name || ' to ' || username);

     END LOOP; --PROCEDURE LOOP
  end if;

 IF UPPER(V_WHATTYPE) LIKE '%SEQUENCE%'  THEN
    dbms_output.put_line(chr(10) || 'Sequence');

    for L_PROCEDURE in (select object_name from user_objects where object_type = 'SEQUENCE' )
     LOOP

       execute immediate 'grant select on ' || L_PROCEDURE.object_name || ' to ' || username;
      -- DBMS_OUTPUT.put_line('grant execute on ' || L_PROCEDURE.object_name || ' to ' || username);

     END LOOP; --PROCEDURE LOOP
  end if;

END;
/


--
-- BIO_CLINICAL_TRIAL_UID  (Function) 
--
CREATE OR REPLACE FUNCTION             "BIO_CLINICAL_TRIAL_UID" (
  TRIAL_NUMBER VARCHAR2,
  TITLE VARCHAR2,
  CONDITION VARCHAR2
) RETURN VARCHAR2 AS
BEGIN
  RETURN nvl(TRIAL_NUMBER || '|', '') || nvl(TITLE || '|', '') || nvl(CONDITION, '');
END BIO_CLINICAL_TRIAL_UID; 
/


--
-- BIO_COMPOUND_UID  (Function) 
--
CREATE OR REPLACE FUNCTION             "BIO_COMPOUND_UID" 
( CAS_REGISTRY IN VARCHAR2,
  JNJ_NUMBER IN VARCHAR2,
  CNTO_NUMBER IN VARCHAR2
) RETURN VARCHAR2 AS
BEGIN
  RETURN nvl(CAS_REGISTRY || '|', '') || nvl(JNJ_NUMBER || '|', '') || nvl(CNTO_NUMBER, '');
END BIO_COMPOUND_UID;
/


--
-- PATHWAY_GENES  (View) 
--
CREATE OR REPLACE FORCE VIEW PATHWAY_GENES
(GENE_KEYWORD_ID, PATHWAY_KEYWORD_ID, GENE_BIOMARKER_ID)
AS 
SELECT k_gene.search_keyword_id AS gene_keyword_id,
    k_pathway.search_keyword_id   AS pathway_keyword_id,
    b.asso_bio_marker_id          AS gene_biomarker_id
  FROM searchapp.search_keyword k_pathway,
    biomart.bio_marker_correl_mv b,
    searchapp.search_keyword k_gene
  WHERE ((((((b.correl_type)     = 'PATHWAY_GENE')
  AND (b.bio_marker_id           = k_pathway.bio_data_id))
  AND ((k_pathway.data_category) = 'PATHWAY'))
  AND (b.asso_bio_marker_id      = k_gene.bio_data_id))
  AND ((k_gene.data_category)    = 'GENE'));


--
-- SEARCH_CATEGORIES  (View) 
--
CREATE OR REPLACE FORCE VIEW SEARCH_CATEGORIES
(CATEGORY_ID, CATEGORY_NAME)
AS 
SELECT str.child_id AS category_id,
  st.term_name AS category_name
 FROM searchapp.search_taxonomy_rels str,
  searchapp.search_taxonomy st
WHERE ((str.parent_id = ( SELECT search_taxonomy_rels.child_id
         FROM search_taxonomy_rels
WHERE (search_taxonomy_rels.parent_id IS NULL))) AND (str.child_id = st.term_id));


--
-- SEARCH_TAXONOMY_LEVEL1  (View) 
--
CREATE OR REPLACE FORCE VIEW SEARCH_TAXONOMY_LEVEL1
(TERM_ID, TERM_NAME, CATEGORY_NAME)
AS 
SELECT st.term_id,
    st.term_name,
    sc.category_name
  FROM searchapp.search_taxonomy_rels str,
    search_taxonomy st,
    search_categories sc
  WHERE ((str.parent_id = sc.category_id)
  AND (str.child_id     = st.term_id));


--
-- SEARCH_TAXONOMY_LEVEL2  (View) 
--
CREATE OR REPLACE FORCE VIEW SEARCH_TAXONOMY_LEVEL2
(TERM_ID, TERM_NAME, CATEGORY_NAME)
AS 
SELECT st.term_id,
    st.term_name,
    stl1.category_name
  FROM searchapp.search_taxonomy_rels str,
    search_taxonomy st,
    search_taxonomy_level1 stl1
  WHERE ((str.parent_id = stl1.term_id)
  AND (str.child_id     = st.term_id));


--
-- SEARCH_TAXONOMY_LEVEL3  (View) 
--
CREATE OR REPLACE FORCE VIEW SEARCH_TAXONOMY_LEVEL3
(TERM_ID, TERM_NAME, CATEGORY_NAME)
AS 
SELECT st.term_id,
    st.term_name,
    stl2.category_name
  FROM searchapp.search_taxonomy_rels str,
    search_taxonomy st,
    search_taxonomy_level2 stl2
  WHERE ((str.parent_id = stl2.term_id)
  AND (str.child_id     = st.term_id));


--
-- SEARCH_TAXONOMY_LEVEL4  (View) 
--
CREATE OR REPLACE FORCE VIEW SEARCH_TAXONOMY_LEVEL4
(TERM_ID, TERM_NAME, CATEGORY_NAME)
AS 
SELECT st.term_id,
    st.term_name,
    stl3.category_name
  FROM searchapp.search_taxonomy_rels str,
    search_taxonomy st,
    search_taxonomy_level3 stl3
  WHERE ((str.parent_id = stl3.term_id)
  AND (str.child_id     = st.term_id));


--
-- SEARCH_TAXONOMY_LEVEL5  (View) 
--
CREATE OR REPLACE FORCE VIEW SEARCH_TAXONOMY_LEVEL5
(TERM_ID, TERM_NAME, CATEGORY_NAME)
AS 
SELECT st.term_id,
    st.term_name,
    stl4.category_name
  FROM searchapp.search_taxonomy_rels str,
    search_taxonomy st,
    search_taxonomy_level4 stl4
  WHERE ((str.parent_id = stl4.term_id)
  AND (str.child_id     = st.term_id));


--
-- SEARCH_TAXONOMY_LINEAGE  (View) 
--
CREATE OR REPLACE FORCE VIEW SEARCH_TAXONOMY_LINEAGE
(CHILD_ID, PARENT1, PARENT2, PARENT3, PARENT4)
AS 
SELECT s1.child_id,
    s2.child_id AS parent1,
    s3.child_id AS parent2,
    s4.child_id AS parent3,
    s5.child_id AS parent4
  FROM searchapp.search_taxonomy_rels s1,
    searchapp.search_taxonomy_rels s2,
    searchapp.search_taxonomy_rels s3,
    searchapp.search_taxonomy_rels s4,
    searchapp.search_taxonomy_rels s5
  WHERE ((((s1.parent_id = s2.child_id)
  AND (s2.parent_id      = s3.child_id))
  AND (s3.parent_id      = s4.child_id))
  AND (s4.parent_id      = s5.child_id));


--
-- SEARCH_TAXONOMY_TERMS_CATS  (View) 
--
CREATE OR REPLACE FORCE VIEW SEARCH_TAXONOMY_TERMS_CATS
(TERM_ID, TERM_NAME, CATEGORY_NAME)
AS 
SELECT DISTINCT unoin_results.term_id,
    unoin_results.term_name,
    unoin_results.category_name
  FROM ( ( (
    (SELECT search_taxonomy_level1.term_id,
      search_taxonomy_level1.term_name,
      search_taxonomy_level1.category_name
    FROM searchapp.search_taxonomy_level1
    UNION
    SELECT search_taxonomy_level2.term_id,
      search_taxonomy_level2.term_name,
      search_taxonomy_level2.category_name
    FROM searchapp.search_taxonomy_level2
    )
  UNION
  SELECT search_taxonomy_level3.term_id,
    search_taxonomy_level3.term_name,
    search_taxonomy_level3.category_name
  FROM searchapp.search_taxonomy_level3)
  UNION
  SELECT search_taxonomy_level4.term_id,
    search_taxonomy_level4.term_name,
    search_taxonomy_level4.category_name
  FROM searchapp.search_taxonomy_level4)
  UNION
  SELECT search_taxonomy_level5.term_id,
    search_taxonomy_level5.term_name,
    search_taxonomy_level5.category_name
  FROM searchapp.search_taxonomy_level5) unoin_results;


--
-- SOLR_KEYWORDS_LINEAGE  (View) 
--
CREATE OR REPLACE FORCE VIEW SOLR_KEYWORDS_LINEAGE
(TERM_ID, ANCESTOR_ID, SEARCH_KEYWORD_ID)
AS 
SELECT DISTINCT union_results.term_id,
  union_results.ancestor_id,
  union_results.search_keyword_id
 FROM (        (        (        (         SELECT DISTINCT l.child_id AS term_id,
                                          l.child_id AS ancestor_id,
                                          st.search_keyword_id
                                         FROM searchapp.search_taxonomy_lineage l,
                                          searchapp.search_taxonomy st
                                        WHERE ((l.child_id = st.term_id) AND (l.child_id IS NOT NULL))
                              UNION
                                       SELECT DISTINCT l.child_id AS term_id,
                                          l.parent1 AS ancestor_id,
                                          st.search_keyword_id
                                         FROM searchapp.search_taxonomy_lineage l,
                                          searchapp.search_taxonomy st
                                        WHERE ((l.parent1 = st.term_id) AND (l.parent1 IS NOT NULL)))
                      UNION
                               SELECT DISTINCT l.child_id AS term_id,
                                  l.parent2 AS ancestor_id,
                                  st.search_keyword_id
                                 FROM searchapp.search_taxonomy_lineage l,
                                  searchapp.search_taxonomy st
                                WHERE ((l.parent2 = st.term_id) AND (l.parent2 IS NOT NULL)))
              UNION
                       SELECT DISTINCT l.child_id AS term_id,
                          l.parent3 AS ancestor_id,
                          st.search_keyword_id
                         FROM searchapp.search_taxonomy_lineage l,
                          searchapp.search_taxonomy st
                        WHERE ((l.parent3 = st.term_id) AND (l.parent3 IS NOT NULL)))
      UNION
               SELECT DISTINCT l.child_id AS term_id,
                  l.parent4 AS ancestor_id,
                  st.search_keyword_id
                 FROM searchapp.search_taxonomy_lineage l,
                  searchapp.search_taxonomy st
                WHERE ((l.parent4 = st.term_id) AND (l.parent4 IS NOT NULL))) union_results
WHERE (union_results.search_keyword_id IS NOT NULL);


--
-- TGR_SEARCH_TAXONOMY_RELS_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TGR_SEARCH_TAXONOMY_RELS_ID 
  before insert ON SEARCH_TAXONOMY_RELS for each row
begin 
    If Inserting 
      Then If :New.search_taxonomy_rels_Id Is Null 
        Then Select Seq_Search_Taxonomy_rels_Id.Nextval Into :New.search_taxonomy_rels_Id From Dual; 
      End If; 
    end if;
end;
/


--
-- TGR_SEARCH_TAXONOMY_TERM_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TGR_SEARCH_TAXONOMY_TERM_ID 
  before insert ON SEARCH_TAXONOMY for each row
begin 
    If Inserting 
      Then If :New.Term_Id Is Null 
        Then Select Seq_Search_Taxonomy_Term_Id.Nextval Into :New.Term_Id From Dual; 
      End If; 
    end if;
end;
/


--
-- TRG_SEARCH_AU_PRCPL_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_SEARCH_AU_PRCPL_ID 
 before insert on SEARCH_AUTH_PRINCIPAL   
 for each row
begin     
 if inserting then      
 if(:NEW.ID is null or :NEW.ID = -2000) then       
 select SEQ_SEARCH_DATA_ID.nextval into :NEW.ID from dual;      
 end if;    end if; end;
/


--
-- TRG_SEARCH_CUSTOM_FILTER_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_SEARCH_CUSTOM_FILTER_ID 
  before insert on "SEARCH_CUSTOM_FILTER" for each row
begin 
    if inserting then if :NEW."SEARCH_CUSTOM_FILTER_ID" is null then select SEQ_SEARCH_DATA_ID.nextval into :NEW."SEARCH_CUSTOM_FILTER_ID" from dual; end if; end if;
end;
/


--
-- TRG_SEARCH_CUST_FIL_ITEM_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_SEARCH_CUST_FIL_ITEM_ID 
  before insert on "SEARCH_CUSTOM_FILTER_ITEM" for each row
begin 
    if inserting then if :NEW."SEARCH_CUSTOM_FILTER_ITEM_ID" is null then select SEQ_SEARCH_DATA_ID.nextval into :NEW."SEARCH_CUSTOM_FILTER_ITEM_ID" from dual; end if; end if;
end;
/


--
-- TRG_SEARCH_KEYWORD_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_SEARCH_KEYWORD_ID before insert on "SEARCH_KEYWORD"    for each row
begin     if inserting then       if :NEW."SEARCH_KEYWORD_ID" is null then          select SEQ_SEARCH_DATA_ID.nextval into :NEW."SEARCH_KEYWORD_ID" from dual;       end if;    end if; end;
/


--
-- TRG_SEARCH_KEYWORD_TERM_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_SEARCH_KEYWORD_TERM_ID before insert on "SEARCH_KEYWORD_TERM"    for each row
begin     if inserting then       if :NEW."SEARCH_KEYWORD_TERM_ID" is null then          select SEQ_SEARCH_DATA_ID.nextval into :NEW."SEARCH_KEYWORD_TERM_ID" from dual;       end if;    end if; end;
/


--
-- TRG_SEARCH_SEC_ACC_LEVEL_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_SEARCH_SEC_ACC_LEVEL_ID before insert on "SEARCH_SEC_ACCESS_LEVEL"    for each row
begin     if inserting then       if :NEW."SEARCH_SEC_ACCESS_LEVEL_ID" is null then          select SEQ_SEARCH_DATA_ID.nextval into :NEW."SEARCH_SEC_ACCESS_LEVEL_ID" from dual;       end if;    end if; end;
/


--
-- TRG_SEARCH_SEC_OBJ_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_SEARCH_SEC_OBJ_ID before insert on "SEARCH_SECURE_OBJECT"    for each row
begin     if inserting then       if :NEW."SEARCH_SECURE_OBJECT_ID" is null then          select SEQ_SEARCH_DATA_ID.nextval into :NEW."SEARCH_SECURE_OBJECT_ID" from dual;       end if;    end if; end;
/


--
-- TRG_SEARCH_SEC_OBJ_PATH_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_SEARCH_SEC_OBJ_PATH_ID before insert on "SEARCH_SECURE_OBJECT_PATH"    for each row
begin     if inserting then       if :NEW."SEARCH_SECURE_OBJ_PATH_ID" is null then          select SEQ_SEARCH_DATA_ID.nextval into :NEW."SEARCH_SECURE_OBJ_PATH_ID" from dual;       end if;    end if; end;
/


--
-- SEARCH_AUTH_GROUP  (Table) 
--
CREATE TABLE SEARCH_AUTH_GROUP
(
  ID              NUMBER(19)                    NOT NULL,
  GROUP_CATEGORY  VARCHAR2(255),
  CONSTRAINT PK_AUTH_USR_GROUP
  PRIMARY KEY
  (ID)
  ENABLE VALIDATE,
  CONSTRAINT SH_AUTH_GP_ID_FK 
  FOREIGN KEY (ID) 
  REFERENCES SEARCH_AUTH_PRINCIPAL (ID)
  ENABLE VALIDATE
);


--
-- SEARCH_AUTH_GROUP_MEMBER  (Table) 
--
CREATE TABLE SEARCH_AUTH_GROUP_MEMBER
(
  AUTH_USER_ID   NUMBER(19),
  AUTH_GROUP_ID  NUMBER(19),
  CONSTRAINT SCH_USER_GP_M_GRP_FK 
  FOREIGN KEY (AUTH_GROUP_ID) 
  REFERENCES SEARCH_AUTH_GROUP (ID)
  ENABLE VALIDATE,
  CONSTRAINT SCH_USER_GP_M_USR_FK 
  FOREIGN KEY (AUTH_USER_ID) 
  REFERENCES SEARCH_AUTH_PRINCIPAL (ID)
  ENABLE VALIDATE
);


--
-- SEARCH_AUTH_SEC_OBJECT_ACCESS  (Table) 
--
CREATE TABLE SEARCH_AUTH_SEC_OBJECT_ACCESS
(
  AUTH_SEC_OBJ_ACCESS_ID  NUMBER(18),
  AUTH_PRINCIPAL_ID       NUMBER(18),
  SECURE_OBJECT_ID        NUMBER(18),
  SECURE_ACCESS_LEVEL_ID  NUMBER(18),
  CONSTRAINT SCH_SEC_A_A_S_A_PK
  PRIMARY KEY
  (AUTH_SEC_OBJ_ACCESS_ID)
  ENABLE VALIDATE,
  CONSTRAINT SCH_SEC_A_U_FK 
  FOREIGN KEY (AUTH_PRINCIPAL_ID) 
  REFERENCES SEARCH_AUTH_PRINCIPAL (ID)
  ENABLE VALIDATE,
  CONSTRAINT SCH_SEC_S_A_L_FK 
  FOREIGN KEY (SECURE_ACCESS_LEVEL_ID) 
  REFERENCES SEARCH_SEC_ACCESS_LEVEL (SEARCH_SEC_ACCESS_LEVEL_ID)
  ENABLE VALIDATE,
  CONSTRAINT SCH_SEC_S_O_FK 
  FOREIGN KEY (SECURE_OBJECT_ID) 
  REFERENCES SEARCH_SECURE_OBJECT (SEARCH_SECURE_OBJECT_ID)
  ENABLE VALIDATE
);


--
-- SEARCH_AUTH_USER_SEC_ACCESS  (Table) 
--
CREATE TABLE SEARCH_AUTH_USER_SEC_ACCESS
(
  SEARCH_AUTH_USER_SEC_ACCESS_ID  NUMBER(18),
  SEARCH_AUTH_USER_ID             NUMBER(18),
  SEARCH_SECURE_OBJECT_ID         NUMBER(18),
  SEARCH_SEC_ACCESS_LEVEL_ID      NUMBER(18),
  CONSTRAINT SEARCH_SEC_A_U_S_A_PK
  PRIMARY KEY
  (SEARCH_AUTH_USER_SEC_ACCESS_ID)
  ENABLE VALIDATE,
  CONSTRAINT SEARCH_SEC_A_U_FK 
  FOREIGN KEY (SEARCH_AUTH_USER_ID) 
  REFERENCES SEARCH_AUTH_USER (ID)
  ENABLE VALIDATE,
  CONSTRAINT SEARCH_SEC_S_A_L_FK 
  FOREIGN KEY (SEARCH_SEC_ACCESS_LEVEL_ID) 
  REFERENCES SEARCH_SEC_ACCESS_LEVEL (SEARCH_SEC_ACCESS_LEVEL_ID)
  ENABLE VALIDATE,
  CONSTRAINT SEARCH_SEC_S_O_FK 
  FOREIGN KEY (SEARCH_SECURE_OBJECT_ID) 
  REFERENCES SEARCH_SECURE_OBJECT (SEARCH_SECURE_OBJECT_ID)
  ENABLE VALIDATE
);


--
-- SEARCH_GENE_SIGNATURE  (Table) 
--
CREATE TABLE SEARCH_GENE_SIGNATURE
(
  SEARCH_GENE_SIGNATURE_ID        NUMBER        NOT NULL,
  NAME                            VARCHAR2(100) NOT NULL,
  DESCRIPTION                     VARCHAR2(1000),
  UNIQUE_ID                       VARCHAR2(50),
  CREATE_DATE                     TIMESTAMP(6)  NOT NULL,
  CREATED_BY_AUTH_USER_ID         NUMBER        NOT NULL,
  LAST_MODIFIED_DATE              TIMESTAMP(6),
  MODIFIED_BY_AUTH_USER_ID        NUMBER,
  VERSION_NUMBER                  VARCHAR2(50),
  PUBLIC_FLAG                     NUMBER(1)     DEFAULT 0                     NOT NULL,
  DELETED_FLAG                    NUMBER(1)     DEFAULT 0                     NOT NULL,
  PARENT_GENE_SIGNATURE_ID        NUMBER,
  SOURCE_CONCEPT_ID               NUMBER,
  SOURCE_OTHER                    VARCHAR2(255),
  OWNER_CONCEPT_ID                NUMBER,
  STIMULUS_DESCRIPTION            VARCHAR2(1000),
  STIMULUS_DOSING                 VARCHAR2(255),
  TREATMENT_DESCRIPTION           VARCHAR2(1000),
  TREATMENT_DOSING                VARCHAR2(255),
  TREATMENT_BIO_COMPOUND_ID       NUMBER,
  TREATMENT_PROTOCOL_NUMBER       VARCHAR2(50),
  PMID_LIST                       VARCHAR2(255),
  SPECIES_CONCEPT_ID              NUMBER        NOT NULL,
  SPECIES_MOUSE_SRC_CONCEPT_ID    NUMBER,
  SPECIES_MOUSE_DETAIL            VARCHAR2(255),
  TISSUE_TYPE_CONCEPT_ID          NUMBER,
  EXPERIMENT_TYPE_CONCEPT_ID      NUMBER,
  EXPERIMENT_TYPE_IN_VIVO_DESCR   VARCHAR2(255),
  EXPERIMENT_TYPE_ATCC_REF        VARCHAR2(255),
  ANALYTIC_CAT_CONCEPT_ID         NUMBER,
  ANALYTIC_CAT_OTHER              VARCHAR2(255),
  BIO_ASSAY_PLATFORM_ID           NUMBER        NOT NULL,
  ANALYST_NAME                    VARCHAR2(100),
  NORM_METHOD_CONCEPT_ID          NUMBER,
  NORM_METHOD_OTHER               VARCHAR2(255),
  ANALYSIS_METHOD_CONCEPT_ID      NUMBER,
  ANALYSIS_METHOD_OTHER           VARCHAR2(255),
  MULTIPLE_TESTING_CORRECTION     NUMBER(1),
  P_VALUE_CUTOFF_CONCEPT_ID       NUMBER        NOT NULL,
  UPLOAD_FILE                     VARCHAR2(255) NOT NULL,
  SEARCH_GENE_SIG_FILE_SCHEMA_ID  NUMBER        DEFAULT 1                     NOT NULL,
  FOLD_CHG_METRIC_CONCEPT_ID      NUMBER        DEFAULT NULL                  NOT NULL,
  EXPERIMENT_TYPE_CELL_LINE_ID    NUMBER,
  QC_PERFORMED                    NUMBER(1),
  QC_DATE                         TIMESTAMP(6),
  QC_INFO                         VARCHAR2(255),
  DATA_SOURCE                     VARCHAR2(255),
  CUSTOM_VALUE1                   VARCHAR2(255),
  CUSTOM_NAME1                    VARCHAR2(255),
  CUSTOM_VALUE2                   VARCHAR2(255),
  CUSTOM_NAME2                    VARCHAR2(255),
  CUSTOM_VALUE3                   VARCHAR2(255),
  CUSTOM_NAME3                    VARCHAR2(255),
  CUSTOM_VALUE4                   VARCHAR2(255),
  CUSTOM_NAME4                    VARCHAR2(255),
  CUSTOM_VALUE5                   VARCHAR2(255),
  CUSTOM_NAME5                    VARCHAR2(255),
  VERSION                         VARCHAR2(255),
  CONSTRAINT SEARCH_GENE_SIG_DESCR_PK
  PRIMARY KEY
  (SEARCH_GENE_SIGNATURE_ID)
  ENABLE VALIDATE,
  CONSTRAINT GENE_SIG_CREATE_AUTH_USER_FK1 
  FOREIGN KEY (CREATED_BY_AUTH_USER_ID) 
  REFERENCES SEARCH_AUTH_USER (ID)
  ENABLE VALIDATE,
  CONSTRAINT GENE_SIG_FILE_SCHEMA_FK1 
  FOREIGN KEY (SEARCH_GENE_SIG_FILE_SCHEMA_ID) 
  REFERENCES SEARCH_GENE_SIG_FILE_SCHEMA (SEARCH_GENE_SIG_FILE_SCHEMA_ID)
  ENABLE VALIDATE,
  CONSTRAINT GENE_SIG_MOD_AUTH_USER_FK1 
  FOREIGN KEY (MODIFIED_BY_AUTH_USER_ID) 
  REFERENCES SEARCH_AUTH_USER (ID)
  ENABLE VALIDATE,
  CONSTRAINT GENE_SIG_PARENT_FK1 
  FOREIGN KEY (PARENT_GENE_SIGNATURE_ID) 
  REFERENCES SEARCH_GENE_SIGNATURE (SEARCH_GENE_SIGNATURE_ID)
  ENABLE VALIDATE
);

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.NAME IS 'name of the gene signature for identification purposes';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.DESCRIPTION IS 'expanded description ';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.UNIQUE_ID IS 'a unique code assigned to the object by a naming convention';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.CREATE_DATE IS 'date object was created';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.CREATED_BY_AUTH_USER_ID IS 'auth user that created the object';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.LAST_MODIFIED_DATE IS 'date of the last modification';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.MODIFIED_BY_AUTH_USER_ID IS 'auth user that last modified the object';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.VERSION_NUMBER IS 'for version tracking for modifications';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.PUBLIC_FLAG IS 'binary flag indicates if object is accessible to other users besides the creator';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.DELETED_FLAG IS 'binary flag indicates if object is deleted so that it will not appear on the UI';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.PARENT_GENE_SIGNATURE_ID IS 'tracks the parent gene signature this object was derived from';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.SOURCE_CONCEPT_ID IS 'source meta data defined in bio_concept_code';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.SOURCE_OTHER IS 'source of the object when selection is not defined in bio_concept_code (other selection)';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.OWNER_CONCEPT_ID IS 'owner of the data defined in bio_concept_code';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.STIMULUS_DESCRIPTION IS 'a description for the stimulus ';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.STIMULUS_DOSING IS 'the dosing used for the stimulus';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.TREATMENT_DESCRIPTION IS 'description of the treamtent involved';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.TREATMENT_DOSING IS 'descipriont of any treatment dosing used';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.TREATMENT_BIO_COMPOUND_ID IS 'reference to the bio_compound_id if relevant';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.TREATMENT_PROTOCOL_NUMBER IS 'the protocol number associated with the treatment';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.PMID_LIST IS 'list of associated pmids (comma separated)';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.SPECIES_CONCEPT_ID IS 'species meta data defined in bio_concept_code';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.SPECIES_MOUSE_SRC_CONCEPT_ID IS 'for species of mouse type, specifies the source of the mouse in bio_concept_code';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.SPECIES_MOUSE_DETAIL IS 'extra detail for knockout/transgenic, or other mouse strain ';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.TISSUE_TYPE_CONCEPT_ID IS 'tissue type meta data defined in bio_concept_code';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.EXPERIMENT_TYPE_CONCEPT_ID IS 'experiment type meta data defined in bio_concept_code';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.EXPERIMENT_TYPE_IN_VIVO_DESCR IS 'describes the model for in vivo experiment types';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.EXPERIMENT_TYPE_ATCC_REF IS 'experiment type atcc designation';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.ANALYTIC_CAT_CONCEPT_ID IS 'analytic category meta deta from bio_concept_code';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.ANALYTIC_CAT_OTHER IS 'analytic category atcc designation';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.BIO_ASSAY_PLATFORM_ID IS 'technology platform meta deta from bio_concept_code';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.ANALYST_NAME IS 'name of the analyst performing analysis (analysis meta data)';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.NORM_METHOD_CONCEPT_ID IS 'normalization method from bio_concept_code (analysis meta data)';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.NORM_METHOD_OTHER IS 'normalization method for other selection (analysis meta data)';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.ANALYSIS_METHOD_CONCEPT_ID IS 'analysis method from bio_concept_code (analysis meta data)';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.ANALYSIS_METHOD_OTHER IS 'analysis method for other selection (analysis meta data)';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.MULTIPLE_TESTING_CORRECTION IS 'binary flag indicates if multiple testing correction was employed (analysis meta data) ';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.P_VALUE_CUTOFF_CONCEPT_ID IS 'p-value cutoff from bio_concept_code (analysis meta data)';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.UPLOAD_FILE IS 'upload file name from user containing gene items';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.SEARCH_GENE_SIG_FILE_SCHEMA_ID IS 'file schema for the upload gene signature file';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.FOLD_CHG_METRIC_CONCEPT_ID IS 'fold change metric type in bio_concept_code';

COMMENT ON COLUMN SEARCH_GENE_SIGNATURE.EXPERIMENT_TYPE_CELL_LINE_ID IS 'for established cell line experiment, specifies the specific cell line from bio_cell_line';


--
-- LISTSIG_GENES  (View) 
--
CREATE OR REPLACE FORCE VIEW LISTSIG_GENES
(GENE_KEYWORD_ID, LIST_KEYWORD_ID)
AS 
SELECT k_gsi.search_keyword_id AS gene_keyword_id,
    k_gs.search_keyword_id       AS list_keyword_id
  FROM searchapp.search_keyword k_gs,
    searchapp.search_gene_signature gs,
    searchapp.search_gene_signature_item gsi,
    searchapp.search_keyword k_gsi
  WHERE (((k_gs.bio_data_id        = gs.search_gene_signature_id)
  AND (gs.search_gene_signature_id = gsi.search_gene_signature_id))
  AND (gsi.bio_marker_id           = k_gsi.bio_data_id));


--
-- SEARCH_AUTH_USER_SEC_ACCESS_V  (View) 
--
CREATE OR REPLACE FORCE VIEW SEARCH_AUTH_USER_SEC_ACCESS_V
(SEARCH_AUTH_USER_SEC_ACCESS_ID, SEARCH_AUTH_USER_ID, SEARCH_SECURE_OBJECT_ID, SEARCH_SEC_ACCESS_LEVEL_ID)
AS 
SELECT 
 sasoa.AUTH_SEC_OBJ_ACCESS_ID AS SEARCH_AUTH_USER_SEC_ACCESS_ID,
 sasoa.AUTH_PRINCIPAL_ID AS SEARCH_AUTH_USER_ID,
 sasoa.SECURE_OBJECT_ID AS SEARCH_SECURE_OBJECT_ID,
 sasoa.SECURE_ACCESS_LEVEL_ID AS SEARCH_SEC_ACCESS_LEVEL_ID
FROM SEARCH_AUTH_USER sau, 
SEARCH_AUTH_SEC_OBJECT_ACCESS sasoa
WHERE 
sau.ID = sasoa.AUTH_PRINCIPAL_ID
UNION
 SELECT 
 sasoa.AUTH_SEC_OBJ_ACCESS_ID AS SEARCH_AUTH_USER_SEC_ACCESS_ID,
 sagm.AUTH_USER_ID AS SEARCH_AUTH_USER_ID,
 sasoa.SECURE_OBJECT_ID AS SEARCH_SECURE_OBJECT_ID,
 sasoa.SECURE_ACCESS_LEVEL_ID AS SEARCH_SEC_ACCESS_LEVEL_ID
FROM SEARCH_AUTH_GROUP sag, 
SEARCH_AUTH_GROUP_MEMBER sagm,
SEARCH_AUTH_SEC_OBJECT_ACCESS sasoa
WHERE 
sag.ID = sagm.AUTH_GROUP_ID
AND
sag.ID = sasoa.AUTH_PRINCIPAL_ID
UNION
SELECT 
 sasoa.AUTH_SEC_OBJ_ACCESS_ID AS SEARCH_AUTH_USER_SEC_ACCESS_ID,
 NULL AS SEARCH_AUTH_USER_ID,
 sasoa.SECURE_OBJECT_ID AS SEARCH_SECURE_OBJECT_ID,
 sasoa.SECURE_ACCESS_LEVEL_ID AS SEARCH_SEC_ACCESS_LEVEL_ID
FROM SEARCH_AUTH_GROUP sag, 
SEARCH_AUTH_SEC_OBJECT_ACCESS sasoa
WHERE 
sag.group_category = 'EVERYONE_GROUP'
AND
sag.ID = sasoa.AUTH_PRINCIPAL_ID;


--
-- SEARCH_BIO_MKR_CORREL_FST_VIEW  (View) 
--
CREATE OR REPLACE FORCE VIEW SEARCH_BIO_MKR_CORREL_FST_VIEW
(DOMAIN_OBJECT_ID, ASSO_BIO_MARKER_ID, CORREL_TYPE, VALUE_METRIC, MV_ID)
AS 
SELECT i.search_gene_signature_id AS domain_object_id,
    i.bio_marker_id                 AS asso_bio_marker_id,
    'GENE_SIGNATURE_ITEM'           AS correl_type,
    CASE
      WHEN (i.fold_chg_metric IS NULL)
      THEN 1
      ELSE i.fold_chg_metric
    END AS value_metric,
    3   AS mv_id
  FROM searchapp.search_gene_signature_item i,
    searchapp.search_gene_signature gs
  WHERE ((i.search_gene_signature_id = gs.search_gene_signature_id)
  AND (gs.deleted_flag               = 0));


--
-- SEARCH_BIO_MKR_CORREL_VIEW  (View) 
--
CREATE OR REPLACE FORCE VIEW SEARCH_BIO_MKR_CORREL_VIEW
(DOMAIN_OBJECT_ID, ASSO_BIO_MARKER_ID, CORREL_TYPE, VALUE_METRIC, MV_ID)
AS 
SELECT domain_object_id,
    asso_bio_marker_id,
    correl_type,
    value_metric,
    mv_id
  FROM
    (SELECT i.SEARCH_GENE_SIGNATURE_ID AS domain_object_id,
      i.BIO_MARKER_ID                  AS asso_bio_marker_id,
      'GENE_SIGNATURE_ITEM'            AS correl_type,
      CASE
        WHEN i.FOLD_CHG_METRIC IS NULL
        THEN 1
        ELSE i.FOLD_CHG_METRIC
      END AS value_metric,
      1   AS mv_id
    FROM SEARCH_GENE_SIGNATURE_ITEM i,
      SEARCH_GENE_SIGNATURE gs
    WHERE i.SEARCH_GENE_SIGNATURE_ID = gs.SEARCH_GENE_SIGNATURE_ID
    AND gs.DELETED_FLAG              = 0
    AND i.bio_marker_id             IS NOT NULL
    UNION ALL
    SELECT i.SEARCH_GENE_SIGNATURE_ID AS domain_object_id,
      bada.BIO_MARKER_ID              AS asso_bio_marker_id,
      'GENE_SIGNATURE_ITEM'           AS correl_type,
      CASE
        WHEN i.FOLD_CHG_METRIC IS NULL
        THEN 1
        ELSE i.FOLD_CHG_METRIC
      END AS value_metric,
      2   AS mv_id
    FROM SEARCH_GENE_SIGNATURE_ITEM i,
      SEARCH_GENE_SIGNATURE gs,
      biomart.bio_assay_data_annotation bada
    WHERE i.SEARCH_GENE_SIGNATURE_ID    = gs.SEARCH_GENE_SIGNATURE_ID
    AND gs.DELETED_FLAG                 = 0
    AND bada.bio_assay_feature_group_id = i.bio_assay_feature_group_id
    And I.Bio_Assay_Feature_Group_Id   Is Not Null
    );


--
-- TRG_SEARCH_AU_OBJ_ACCESS_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_SEARCH_AU_OBJ_ACCESS_ID before insert on SEARCH_AUTH_SEC_OBJECT_ACCESS    for each row
begin     if inserting then       if :NEW.AUTH_SEC_OBJ_ACCESS_ID is null then          select SEQ_SEARCH_DATA_ID.nextval into :NEW.AUTH_SEC_OBJ_ACCESS_ID from dual;       end if;    end if; end;
/


--
-- TRG_SEARCH_A_U_SEC_ACCESS_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_SEARCH_A_U_SEC_ACCESS_ID before insert on "SEARCH_AUTH_USER_SEC_ACCESS"    for each row
begin     if inserting then       if :NEW."SEARCH_AUTH_USER_SEC_ACCESS_ID" is null then          select SEQ_SEARCH_DATA_ID.nextval into :NEW."SEARCH_AUTH_USER_SEC_ACCESS_ID" from dual;       end if;    end if; end;
/


--
-- SEARCH_BIO_MKR_CORREL_FAST_MV  (Materialized View) 
--
CREATE MATERIALIZED VIEW SEARCH_BIO_MKR_CORREL_FAST_MV
BUILD IMMEDIATE
REFRESH COMPLETE ON COMMIT
WITH PRIMARY KEY
AS 
SELECT   i.SEARCH_GENE_SIGNATURE_ID AS domain_object_id,
         i.BIO_MARKER_ID AS asso_bio_marker_id,
         'GENE_SIGNATURE_ITEM' AS correl_type,
         CASE
            WHEN i.FOLD_CHG_METRIC IS NULL THEN 1
            ELSE i.FOLD_CHG_METRIC
         END
            AS value_metric,
         3 AS mv_id
  FROM   SEARCH_GENE_SIGNATURE_ITEM i, SEARCH_GENE_SIGNATURE gs
 WHERE   i.SEARCH_GENE_SIGNATURE_ID = gs.SEARCH_GENE_SIGNATURE_ID
         AND gs.DELETED_FLAG = 0;


COMMENT ON MATERIALIZED VIEW SEARCH_BIO_MKR_CORREL_FAST_MV IS 'snapshot table for snapshot SEARCHAPP.SEARCH_BIO_MKR_CORREL_FAST_MV';

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON SEARCH_KEYWORD TO BIOMART;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON SEARCH_KEYWORD_TERM TO BIOMART;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_USER_SETTINGS TO BIOMART;

GRANT DELETE, INSERT, SELECT, UPDATE ON MLOG$_SEARCH_GENE_SIGNATUR TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON PLUGIN TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON PLUGIN_MODULE TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON REPORT TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_APP_ACCESS_LOG TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_AUTH_PRINCIPAL TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_AUTH_USER TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_BIO_MKR_CORREL_FAST_MV TO BIOMART_USER;

GRANT SELECT ON SEARCH_CATEGORIES TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_CUSTOM_FILTER TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_CUSTOM_FILTER_ITEM TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_GENE_SIGNATURE_ITEM TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_GENE_SIG_FILE_SCHEMA TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_KEYWORD TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_KEYWORD_TERM TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_REQUEST_MAP TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_ROLE TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_ROLE_AUTH_USER TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_SECURE_OBJECT TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_SECURE_OBJECT_PATH TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_SEC_ACCESS_LEVEL TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_TAXONOMY TO BIOMART_USER;

GRANT SELECT ON SEARCH_TAXONOMY_LEVEL1 TO BIOMART_USER;

GRANT SELECT ON SEARCH_TAXONOMY_LEVEL2 TO BIOMART_USER;

GRANT SELECT ON SEARCH_TAXONOMY_LEVEL3 TO BIOMART_USER;

GRANT SELECT ON SEARCH_TAXONOMY_LEVEL4 TO BIOMART_USER;

GRANT SELECT ON SEARCH_TAXONOMY_LEVEL5 TO BIOMART_USER;

GRANT SELECT ON SEARCH_TAXONOMY_LINEAGE TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_TAXONOMY_RELS TO BIOMART_USER;

GRANT SELECT ON SEARCH_TAXONOMY_TERMS_CATS TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_USER_FEEDBACK TO BIOMART_USER;

GRANT SELECT ON SOLR_KEYWORDS_LINEAGE TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON MLOG$_SEARCH_GENE_SIGNATUR TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON PLUGIN TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON PLUGIN_MODULE TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON REPORT TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON REPORT_ITEM TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_APP_ACCESS_LOG TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_AUTH_PRINCIPAL TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_AUTH_USER TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_BIO_MKR_CORREL_FAST_MV TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_CUSTOM_FILTER TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_CUSTOM_FILTER_ITEM TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_GENE_SIGNATURE_ITEM TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_GENE_SIG_FILE_SCHEMA TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_KEYWORD TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_KEYWORD_TERM TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_REQUEST_MAP TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_ROLE TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_ROLE_AUTH_USER TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_SECURE_OBJECT TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_SECURE_OBJECT_PATH TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_SEC_ACCESS_LEVEL TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_TAXONOMY TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_TAXONOMY_RELS TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_USER_FEEDBACK TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_USER_SETTINGS TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SUBSET TO TM_CZ;

GRANT SELECT ON LISTSIG_GENES TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_AUTH_GROUP TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_AUTH_GROUP_MEMBER TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_AUTH_SEC_OBJECT_ACCESS TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_AUTH_USER_SEC_ACCESS TO BIOMART_USER;

GRANT SELECT ON SEARCH_AUTH_USER_SEC_ACCESS_V TO BIOMART_USER;

GRANT SELECT ON SEARCH_BIO_MKR_CORREL_FST_VIEW TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_GENE_SIGNATURE TO BIOMART_USER;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_AUTH_GROUP TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_AUTH_GROUP_MEMBER TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_AUTH_SEC_OBJECT_ACCESS TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_AUTH_USER_SEC_ACCESS TO TM_CZ;

GRANT DELETE, INSERT, SELECT, UPDATE ON SEARCH_GENE_SIGNATURE TO TM_CZ;