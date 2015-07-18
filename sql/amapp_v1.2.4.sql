--
-- Create Schema Script 
--   Database Version          : 11.2.0.1.0 
--   Database Compatible Level : 11.2.0.0.0 
--   Script Compatible Level   : 11.2.0.0.0 
--   Toad Version              : 12.1.0.22 
--   DB Connect String         : 10.118.255.5:1521/ORCL5 
--   Schema                    : AMAPP 
--   Script Created by         : BIOMART_USER 
--   Script Created at         : 7/17/2015 3:49:45 PM 
--   Physical Location         :  
--   Notes                     :  
--

-- Object Counts: 
--   Functions: 1       Lines of Code: 8 
--   Indexes: 7         Columns: 10         
--   Object Privileges: 12 
--   Sequences: 1 
--   Tables: 6          Columns: 33         Constraints: 25     
--   Triggers: 5 
--   Views: 1           Columns: 6          


--
-- AM_DATA_UID  (Table) 
--
CREATE TABLE AM_DATA_UID
(
  AM_DATA_ID    NUMBER(18)                      NOT NULL,
  UNIQUE_ID     NVARCHAR2(300)                  NOT NULL,
  AM_DATA_TYPE  NVARCHAR2(100)                  NOT NULL,
  CONSTRAINT AM_DATA_UID_PK
  PRIMARY KEY
  (AM_DATA_ID)
  ENABLE VALIDATE,
  CONSTRAINT AM_DATA_UID_UK
  UNIQUE (UNIQUE_ID)
  ENABLE VALIDATE
);


--
-- AM_TAG_ASSOCIATION  (Table) 
--
CREATE TABLE AM_TAG_ASSOCIATION
(
  SUBJECT_UID  NVARCHAR2(300)                   NOT NULL,
  OBJECT_UID   NVARCHAR2(300)                   NOT NULL,
  OBJECT_TYPE  NVARCHAR2(50),
  TAG_ITEM_ID  NUMBER(18),
  PRIMARY KEY
  (SUBJECT_UID, OBJECT_UID)
  ENABLE VALIDATE
);


--
-- AM_TAG_ITEM  (Table) 
--
CREATE TABLE AM_TAG_ITEM
(
  TAG_TEMPLATE_ID     NUMBER                    NOT NULL,
  TAG_ITEM_ID         NUMBER                    NOT NULL,
  REQUIRED            NVARCHAR2(1),
  DISPLAY_ORDER       NUMBER,
  DISPLAY_NAME        NVARCHAR2(200)            NOT NULL,
  GUI_HANDLER         NVARCHAR2(200)            NOT NULL,
  MAX_VALUES          NUMBER,
  CODE_TYPE_NAME      NVARCHAR2(200),
  EDITABLE            NVARCHAR2(1),
  ACTIVE_IND          CHAR(1 BYTE)              NOT NULL,
  TAG_ITEM_UID        NVARCHAR2(300)            NOT NULL,
  TAG_ITEM_ATTR       NVARCHAR2(300),
  TAG_ITEM_TYPE       NVARCHAR2(200),
  VIEW_IN_GRID        NUMBER(1),
  TAG_ITEM_SUBTYPE    NVARCHAR2(200),
  VIEW_IN_CHILD_GRID  NUMBER(1),
  PRIMARY KEY
  (TAG_TEMPLATE_ID, TAG_ITEM_ID)
  ENABLE VALIDATE
);


--
-- AM_TAG_TEMPLATE  (Table) 
--
CREATE TABLE AM_TAG_TEMPLATE
(
  TAG_TEMPLATE_ID       NUMBER                  NOT NULL,
  TAG_TEMPLATE_NAME     NVARCHAR2(200)          NOT NULL,
  TAG_TEMPLATE_TYPE     NVARCHAR2(50)           NOT NULL,
  TAG_TEMPLATE_SUBTYPE  NVARCHAR2(50),
  ACTIVE_IND            CHAR(1 BYTE)            NOT NULL,
  PRIMARY KEY
  (TAG_TEMPLATE_ID)
  ENABLE VALIDATE
);


--
-- AM_TAG_TEMPLATE_ASSOCIATION  (Table) 
--
CREATE TABLE AM_TAG_TEMPLATE_ASSOCIATION
(
  TAG_TEMPLATE_ID  NUMBER                       NOT NULL,
  OBJECT_UID       NVARCHAR2(300)               NOT NULL,
  ID               INTEGER,
  PRIMARY KEY
  (TAG_TEMPLATE_ID, OBJECT_UID)
  ENABLE VALIDATE
);


--
-- AM_TAG_VALUE  (Table) 
--
CREATE TABLE AM_TAG_VALUE
(
  TAG_VALUE_ID  NUMBER                          NOT NULL,
  VALUE         NVARCHAR2(2000),
  CONSTRAINT AM_TAG_VALUE_PK
  PRIMARY KEY
  (TAG_VALUE_ID)
  ENABLE VALIDATE
);


--
-- SEQ_AMAPP_DATA_ID  (Sequence) 
--
CREATE SEQUENCE SEQ_AMAPP_DATA_ID
  START WITH 1996295
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER;








--
-- AM_TAG_VALUE_UID  (Function) 
--
CREATE OR REPLACE FUNCTION         "AM_TAG_VALUE_UID" (
  tag_value_id number
) return varchar2
as
begin
  return 'TAG:' || to_char(tag_value_id);
end;
 
/

--
-- AM_TAG_DISPLAY_VW  (View) 
--
CREATE OR REPLACE FORCE VIEW AM_TAG_DISPLAY_VW
(SUBJECT_UID, TAG_ITEM_ID, DISPLAY_VALUE, OBJECT_TYPE, OBJECT_UID, 
 OBJECT_ID)
AS 
SELECT DISTINCT tass.subject_uid,
    tass.tag_item_id,
    TO_CHAR(tval.value) AS display_value,
    tass.object_type,
    tass.object_uid    AS object_uid,
    obj_uid.am_data_id AS object_id
  FROM AMAPP.am_tag_association tass
  JOIN AMAPP.am_data_uid obj_uid
  ON tass.object_uid = obj_uid.unique_id
  JOIN AMAPP.am_tag_value tval
  ON obj_uid.am_data_id = tval.tag_value_id
  UNION
  -- BIO_CONCEPT_CODE
  SELECT DISTINCT tass.subject_uid,
    tass.tag_item_id,
    TO_CHAR(bio_val.code_name) AS display_value,
    tass.object_type,
    tass.object_uid     AS object_uid,
    obj_uid.bio_data_id AS object_id
  FROM AMAPP.am_tag_association tass
  JOIN biomart.bio_data_uid obj_uid
  ON tass.object_uid = obj_uid.unique_id
  JOIN BIOMART.bio_concept_code bio_val
  ON obj_uid.bio_data_id = bio_val.bio_concept_code_id
  UNION
  -- BIO_DISEASE
  SELECT DISTINCT tass.subject_uid,
    tass.tag_item_id,
    TO_CHAR(bio_val.disease) AS display_value,
    tass.object_type,
    tass.object_uid     AS object_uid,
    obj_uid.bio_data_id AS object_id
  FROM AMAPP.am_tag_association tass
  JOIN biomart.bio_data_uid obj_uid
  ON tass.object_uid = obj_uid.unique_id
  JOIN BIOMART.bio_disease bio_val
  ON obj_uid.bio_data_id = bio_val.bio_disease_id
  UNION
  -- BIO_ASSAY_PLATFORM
SELECT DISTINCT tass.subject_uid,
    tass.tag_item_id,
    case when ati.code_type_name = 'PLATFORM_NAME'
    then TO_CHAR(bio_val.platform_type)||'/'||TO_CHAR(bio_val.platform_technology)|| '/' ||TO_CHAR(bio_val.platform_vendor)|| '/' ||TO_CHAR(bio_val.platform_name) 
    when ati.code_type_name = 'VENDOR' then TO_CHAR(bio_val.platform_vendor) 
    when ati.code_type_name = 'MEASUREMENT_TYPE' then TO_CHAR(bio_val.platform_type) 
    when ati.code_type_name = 'TECHNOLOGY' then TO_CHAR(bio_val.platform_technology) end AS display_value,
    tass.object_type,
    tass.object_uid     AS object_uid,
    obj_uid.bio_data_id AS object_id
  FROM AMAPP.am_tag_association tass
  JOIN biomart.bio_data_uid obj_uid
  ON tass.object_uid = obj_uid.unique_id
  JOIN BIOMART.bio_assay_platform bio_val
  ON obj_uid.bio_data_id = bio_val.bio_assay_platform_id
  JOIN amapp.am_tag_item ati 
  ON ati.tag_item_id = tass.tag_item_id
UNION
  -- BIO_COMPOUND
  SELECT DISTINCT tass.subject_uid,
    tass.tag_item_id,
    TO_CHAR(bio_val.code_name) AS display_value,
    tass.object_type,
    tass.object_uid     AS object_uid,
    obj_uid.bio_data_id AS object_id
  FROM AMAPP.am_tag_association tass
  JOIN biomart.bio_data_uid obj_uid
  ON tass.object_uid = obj_uid.unique_id
  JOIN BIOMART.bio_compound bio_val
  ON obj_uid.bio_data_id = bio_val.bio_compound_id

    UNION
  -- BIO_MARKER
  SELECT DISTINCT tass.subject_uid,
    tass.tag_item_id,
    TO_CHAR(bio_val.bio_marker_name) AS display_value,
    tass.object_type,
    tass.object_uid     AS object_uid,
    obj_uid.bio_data_id AS object_id
  FROM AMAPP.am_tag_association tass
  JOIN biomart.bio_data_uid obj_uid
  ON tass.object_uid = obj_uid.unique_id
  JOIN BIOMART.bio_marker bio_val
  ON obj_uid.bio_data_id = bio_val.bio_marker_id
  
     UNION
  -- BIO_OBSERVATION
  SELECT DISTINCT tass.subject_uid,
    tass.tag_item_id,
    TO_CHAR(bio_val.obs_name) AS display_value,
    tass.object_type,
    tass.object_uid     AS object_uid,
    obj_uid.bio_data_id AS object_id
  FROM AMAPP.am_tag_association tass
  JOIN biomart.bio_data_uid obj_uid
  ON tass.object_uid = obj_uid.unique_id
  JOIN BIOMART.bio_observation bio_val
ON obj_uid.bio_data_id = bio_val.bio_observation_id;


--
-- TRG_AM_TAG_ITEM_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_AM_TAG_ITEM_ID before insert ON AM_TAG_ITEM    
for each row
begin    
if inserting then      
  if :NEW."TAG_ITEM_ID" is null then          
    select SEQ_AMAPP_DATA_ID.nextval into :NEW."TAG_ITEM_ID" from dual;       
  end if;    
end if; 
end;
/


--
-- TRG_AM_TAG_TEMPLATE_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_AM_TAG_TEMPLATE_ID before insert ON AM_TAG_TEMPLATE    
for each row
begin    
if inserting then      
  if :NEW."TAG_TEMPLATE_ID" is null then          
    select SEQ_AMAPP_DATA_ID.nextval into :NEW."TAG_TEMPLATE_ID" from dual;       
  end if;    
end if; 
end;
/


--
-- TRG_AM_TAG_TEMP_ASSOC_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_AM_TAG_TEMP_ASSOC_ID before insert ON AM_TAG_TEMPLATE_ASSOCIATION    
for each row
begin    
if inserting then      
  if :NEW."ID" is null then          
    select SEQ_AMAPP_DATA_ID.nextval into :NEW."ID" from dual;       
  end if;    
end if; 
end;
/


--
-- TRG_AM_TAG_VALUE_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_AM_TAG_VALUE_ID before insert ON AM_TAG_VALUE    
for each row
begin    
if inserting then      
  if :NEW."TAG_VALUE_ID" is null then          
    select SEQ_AMAPP_DATA_ID.nextval into :NEW."TAG_VALUE_ID" from dual;       
  end if;    
end if; 
end;
/


--
-- TRG_AM_TAG_VALUE_UID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_AM_TAG_VALUE_UID after insert on "AM_TAG_VALUE"    
for each row
DECLARE
  rec_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO rec_count 
  FROM am_data_uid 
  WHERE am_data_id = :new.TAG_VALUE_ID;
  
  if rec_count = 0 then
    insert into amapp.am_data_uid (am_data_id, unique_id, am_data_type)
    values (:NEW."TAG_VALUE_ID", AM_TAG_VALUE_UID(:NEW."TAG_VALUE_ID"), 'AM_TAG_VALUE');
  end if;
end;
/


GRANT SELECT ON AM_DATA_UID TO BIOMART_USER;

GRANT SELECT ON AM_TAG_ASSOCIATION TO BIOMART_USER;

GRANT SELECT ON AM_TAG_ITEM TO BIOMART_USER;

GRANT SELECT ON AM_TAG_TEMPLATE TO BIOMART_USER;

GRANT SELECT ON AM_TAG_TEMPLATE_ASSOCIATION TO BIOMART_USER;

GRANT SELECT ON AM_TAG_VALUE TO BIOMART_USER;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON AM_DATA_UID TO TM_CZ;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON AM_TAG_ASSOCIATION TO TM_CZ;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON AM_TAG_ITEM TO TM_CZ;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON AM_TAG_TEMPLATE TO TM_CZ;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON AM_TAG_TEMPLATE_ASSOCIATION TO TM_CZ;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON AM_TAG_VALUE TO TM_CZ;