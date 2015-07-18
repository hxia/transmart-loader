
-- ****************************************************
--  populate searchapp.search_gene_sig_file_schema
-- ****************************************************

truncate table searchapp.search_gene_sig_file_schema;

insert into searchapp.search_gene_sig_file_schema 
(SEARCH_GENE_SIG_FILE_SCHEMA_ID, NAME, DESCRIPTION, NUMBER_COLUMNS, SUPPORTED)
values (1, 'Gene Symbol <tab> Metric Indicator', 'two column upload file schema containing: gene id <tab> value type indicator', 2, true);

insert into searchapp.search_gene_sig_file_schema 
(SEARCH_GENE_SIG_FILE_SCHEMA_ID, NAME, DESCRIPTION, NUMBER_COLUMNS, SUPPORTED)
values (2, 'Gene Symbol <tab> Probeset Symbol <tab> Metric Indicator', 'three column upload file schema containing: gene id <tab> probeset <tab> value type indicator', 3, false);

insert into searchapp.search_gene_sig_file_schema 
(SEARCH_GENE_SIG_FILE_SCHEMA_ID, NAME, DESCRIPTION, NUMBER_COLUMNS, SUPPORTED)
values (3, 'ProbeSet Symbol <tab> Metric Indicator', 'two column upload file schema containing: probeset id <tab> value type indicator', 2, true);


-- ****************************************************
--  populate SEARCHAPP.SEARCH_TAXONOMY
-- ****************************************************

truncate table SEARCHAPP.SEARCH_TAXONOMY;

Insert into SEARCHAPP.SEARCH_TAXONOMY (TERM_ID,TERM_NAME,SOURCE_CD,IMPORT_DATE,SEARCH_KEYWORD_ID) 
values (3,'Study',null,now(),null);

Insert into SEARCHAPP.SEARCH_TAXONOMY (TERM_ID,TERM_NAME,SOURCE_CD,IMPORT_DATE,SEARCH_KEYWORD_ID) 
values (4,'Region of Interest',null,now(),null);

Insert into SEARCHAPP.SEARCH_TAXONOMY (TERM_ID,TERM_NAME,SOURCE_CD,IMPORT_DATE,SEARCH_KEYWORD_ID) 
values (5,'Data Type',null,now(),null);

Insert into SEARCHAPP.SEARCH_TAXONOMY (TERM_ID,TERM_NAME,SOURCE_CD,IMPORT_DATE,SEARCH_KEYWORD_ID) 
values (6,'GWAS',null,now(),null);

Insert into SEARCHAPP.SEARCH_TAXONOMY (TERM_ID,TERM_NAME,SOURCE_CD,IMPORT_DATE,SEARCH_KEYWORD_ID) 
values (7,'EQTL',null,now(),null);

Insert into SEARCHAPP.SEARCH_TAXONOMY (TERM_ID,TERM_NAME,SOURCE_CD,IMPORT_DATE,SEARCH_KEYWORD_ID) 
values (1,'Categories',null,now(),null);

Insert into SEARCHAPP.SEARCH_TAXONOMY (TERM_ID,TERM_NAME,SOURCE_CD,IMPORT_DATE,SEARCH_KEYWORD_ID) 
values (2,'Analyses',null,now(),null);


-- ****************************************************
--  populate SEARCHAPP.SEARCH_TAXONOMY_RELS
-- ****************************************************

truncate table SEARCHAPP.SEARCH_TAXONOMY_RELS;

Insert into SEARCHAPP.SEARCH_TAXONOMY_RELS (SEARCH_TAXONOMY_RELS_ID,CHILD_ID,PARENT_ID) values (1,1,null);
Insert into SEARCHAPP.SEARCH_TAXONOMY_RELS (SEARCH_TAXONOMY_RELS_ID,CHILD_ID,PARENT_ID) values (2,2,1);
Insert into SEARCHAPP.SEARCH_TAXONOMY_RELS (SEARCH_TAXONOMY_RELS_ID,CHILD_ID,PARENT_ID) values (3,3,1);
Insert into SEARCHAPP.SEARCH_TAXONOMY_RELS (SEARCH_TAXONOMY_RELS_ID,CHILD_ID,PARENT_ID) values (4,4,1);
Insert into SEARCHAPP.SEARCH_TAXONOMY_RELS (SEARCH_TAXONOMY_RELS_ID,CHILD_ID,PARENT_ID) values (5,5,1);
