--
-- Create Schema Script 
--   Database Version          : 11.2.0.1.0 
--   Database Compatible Level : 11.2.0.0.0 
--   Script Compatible Level   : 11.2.0.0.0 
--   Toad Version              : 12.1.0.22 
--   DB Connect String         : 10.118.255.5:1521/ORCL5 
--   Schema                    : FMAPP 
--   Script Created by         : BIOMART_USER 
--   Script Created at         : 7/17/2015 3:50:21 PM 
--   Physical Location         :  
--   Notes                     :  
--

-- Object Counts: 
--   Functions: 3       Lines of Code: 50 
--   Indexes: 5         Columns: 6          
--   Object Privileges: 10 
--   Procedures: 1      Lines of Code: 18 
--   Sequences: 1 
--   Tables: 5          Columns: 29         Constraints: 29     
--   Triggers: 4 


--
-- FM_DATA_UID  (Table) 
--
CREATE TABLE FM_DATA_UID
(
  FM_DATA_ID    NUMBER(18)                      NOT NULL,
  UNIQUE_ID     NVARCHAR2(300)                  NOT NULL,
  FM_DATA_TYPE  NVARCHAR2(100)                  NOT NULL,
  CONSTRAINT FM_DATA_UID_PK_1
  PRIMARY KEY
  (FM_DATA_ID)
  ENABLE VALIDATE,
  CONSTRAINT FM_DATA_UID_UK_1
  UNIQUE (UNIQUE_ID)
  ENABLE VALIDATE
);


--
-- FM_FILE  (Table) 
--
CREATE TABLE FM_FILE
(
  FILE_ID             NUMBER(18)                NOT NULL,
  DISPLAY_NAME        NVARCHAR2(1000)           NOT NULL,
  ORIGINAL_NAME       NVARCHAR2(1000)           NOT NULL,
  FILE_VERSION        NUMBER(18),
  FILE_TYPE           NVARCHAR2(100),
  FILE_SIZE           NUMBER(18),
  FILESTORE_LOCATION  NVARCHAR2(1000),
  FILESTORE_NAME      NVARCHAR2(1000),
  LINK_URL            NVARCHAR2(1000),
  ACTIVE_IND          CHAR(1 BYTE)              NOT NULL,
  CREATE_DATE         DATE                      NOT NULL,
  UPDATE_DATE         DATE                      NOT NULL,
  PRIMARY KEY
  (FILE_ID)
  ENABLE VALIDATE
);


--
-- FM_FOLDER  (Table) 
--
CREATE TABLE FM_FOLDER
(
  FOLDER_ID         NUMBER(18)                  NOT NULL,
  FOLDER_NAME       NVARCHAR2(1000)             NOT NULL,
  FOLDER_FULL_NAME  NVARCHAR2(1000)             NOT NULL,
  FOLDER_LEVEL      NUMBER(18)                  NOT NULL,
  FOLDER_TYPE       NVARCHAR2(100)              NOT NULL,
  FOLDER_TAG        NVARCHAR2(50),
  ACTIVE_IND        CHAR(1 BYTE)                NOT NULL,
  PARENT_ID         NUMBER(18),
  DESCRIPTION       NVARCHAR2(2000),
  PRIMARY KEY
  (FOLDER_ID)
  ENABLE VALIDATE
);


--
-- FM_FOLDER_ASSOCIATION  (Table) 
--
CREATE TABLE FM_FOLDER_ASSOCIATION
(
  FOLDER_ID    NUMBER(18)                       NOT NULL,
  OBJECT_UID   NVARCHAR2(300)                   NOT NULL,
  OBJECT_TYPE  NVARCHAR2(100)                   NOT NULL,
  CONSTRAINT PK_FOLDER_ASSOC
  PRIMARY KEY
  (FOLDER_ID, OBJECT_UID)
  DISABLE NOVALIDATE,
  CONSTRAINT FK_FM_FOLDER_ASSOC_FM_FOLDER 
  FOREIGN KEY (FOLDER_ID) 
  REFERENCES FM_FOLDER (FOLDER_ID)
  ENABLE VALIDATE
);


--
-- FM_FOLDER_FILE_ASSOCIATION  (Table) 
--
CREATE TABLE FM_FOLDER_FILE_ASSOCIATION
(
  FOLDER_ID  NUMBER(18)                         NOT NULL,
  FILE_ID    NUMBER(18)                         NOT NULL,
  CONSTRAINT PK_FOLDER_FILE_ASSOC
  PRIMARY KEY
  (FOLDER_ID, FILE_ID)
  ENABLE VALIDATE,
  CONSTRAINT FK_FM_FOLDER_FM_FOLDER 
  FOREIGN KEY (FOLDER_ID) 
  REFERENCES FM_FOLDER (FOLDER_ID)
  ENABLE VALIDATE,
  CONSTRAINT FK_FOLDER_FILE_ASSOC_FILE 
  FOREIGN KEY (FILE_ID) 
  REFERENCES FM_FILE (FILE_ID)
  ENABLE VALIDATE
);


--
-- SEQ_FM_ID  (Sequence) 
--
CREATE SEQUENCE SEQ_FM_ID
  START WITH 1992727
  MAXVALUE 9999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER;








--
-- FM_FILE_UID  (Function) 
--
CREATE OR REPLACE FUNCTION         "FM_FILE_UID" (
  FILE_ID NUMBER
) RETURN VARCHAR2 AS
BEGIN
  -- $Id$
  -- Creates uid for bio_concept_code.

  RETURN 'FIL:' || FILE_ID;
END FM_FILE_UID;
 
/

--
-- FM_FOLDER_UID  (Function) 
--
CREATE OR REPLACE FUNCTION         "FM_FOLDER_UID" (
  FOLDER_NAME VARCHAR2
) RETURN VARCHAR2 AS
BEGIN
  -- $Id$
  -- Creates uid for bio_concept_code.

  RETURN 'FOL:' || FOLDER_NAME;
END FM_FOLDER_UID;

 
/

--
-- FM_GET_FOLDER_FULL_NAME  (Function) 
--
CREATE OR REPLACE FUNCTION         "FM_GET_FOLDER_FULL_NAME" (
  p_folder_id number
)
return nvarchar2
as
  v_parent_id number;
  v_folder_full_name nvarchar2(1000);
begin

  select parent_id into v_parent_id
  from fm_folder
  where folder_id = p_folder_id;
  
  v_folder_full_name := fm_folder_uid(p_folder_id) || '\';
  
  while v_parent_id is not null
  loop
    v_folder_full_name := fm_folder_uid(v_parent_id) || '\' || v_folder_full_name;

    select parent_id into v_parent_id
    from fm_folder
    where folder_id = v_parent_id;
  end loop;

  v_folder_full_name := '\' || v_folder_full_name;
  
  return v_folder_full_name;  
end;
 
/

--
-- TRG_FM_FILE_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_FM_FILE_ID before insert ON FM_FILE    
for each row
begin    
if inserting then      
  if :NEW."FILE_ID" is null then          
    select SEQ_FM_ID.nextval into :NEW."FILE_ID" from dual;       
  end if;    
end if; 
end;
/


--
-- TRG_FM_FILE_UID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_FM_FILE_UID after insert ON FM_FILE    
for each row
DECLARE
  rec_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO rec_count 
  FROM fm_data_uid 
  WHERE fm_data_id = :new.FILE_ID;
  
  if rec_count = 0 then
    insert into fmapp.fm_data_uid (fm_data_id, unique_id, fm_data_type)
    values (:NEW."FILE_ID", FM_FILE_UID(:NEW."FILE_ID"), 'FM_FILE');
  end if;
end;
/


--
-- TRG_FM_FOLDER_ID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_FM_FOLDER_ID before insert on FM_FOLDER    
for each row
begin    
if inserting then      
  if :NEW.FOLDER_ID is null then          
    select SEQ_FM_ID.nextval into :NEW.FOLDER_ID from dual;       
  end if;
  if :new.FOLDER_FULL_NAME is null then
    if :new.PARENT_ID is null then
      select '\' || fm_folder_uid(:new.folder_id) || '\' into :new.folder_full_name 
      from dual;
    else
      select folder_full_name || fm_folder_uid(:new.folder_id) || '\' into :new.folder_full_name 
      from fm_folder
      where folder_id = :new.parent_id;
    end if;
  end if;
end if; 
end;
/


--
-- TRG_FM_FOLDER_UID  (Trigger) 
--
CREATE OR REPLACE TRIGGER TRG_FM_FOLDER_UID after insert on "FM_FOLDER"    
for each row
DECLARE
  rec_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO rec_count 
  FROM fm_data_uid 
  WHERE fm_data_id = :new.FOLDER_ID;
  
  if rec_count = 0 then
    insert into fmapp.fm_data_uid (fm_data_id, unique_id, fm_data_type)
    values (:NEW."FOLDER_ID", FM_FOLDER_UID(:NEW."FOLDER_ID"), 'FM_FOLDER');
  end if;
end;
/


--
-- FM_UPDATE_FOLDER_FULL_NAME  (Procedure) 
--
CREATE OR REPLACE PROCEDURE         "FM_UPDATE_FOLDER_FULL_NAME" 
as
  v_folder_full_name nvarchar2(1000);
  cursor folder_ids is
    select folder_id
    from fm_folder;
    
begin
  for folder_rec in folder_ids
  loop
    select fm_get_folder_full_name(folder_rec.folder_id) into v_folder_full_name
    from dual;
    
    update fm_folder set folder_full_name = v_folder_full_name
    where folder_id = folder_rec.folder_id;
  end loop;
end;
 
/

GRANT SELECT ON FM_DATA_UID TO BIOMART_USER;

GRANT SELECT ON FM_FILE TO BIOMART_USER;

GRANT SELECT ON FM_FOLDER TO BIOMART_USER;

GRANT SELECT ON FM_FOLDER_ASSOCIATION TO BIOMART_USER;

GRANT SELECT ON FM_FOLDER_FILE_ASSOCIATION TO BIOMART_USER;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON FM_DATA_UID TO TM_CZ;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON FM_FILE TO TM_CZ;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON FM_FOLDER TO TM_CZ;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON FM_FOLDER_ASSOCIATION TO TM_CZ;

GRANT ALTER, DELETE, INDEX, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON FM_FOLDER_FILE_ASSOCIATION TO TM_CZ;