/*************************************************************************
 * tranSMART - translational medicine data mart
 *
 * Copyright 2008-2012 Janssen Research & Development, LLC.
 *
 * This product includes software developed at Janssen Research & Development, LLC.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software  * Foundation, either version 3 of the License, or (at your option) any later version, along with the following terms:
 * 1.	You may convey a work based on this program in accordance with section 5, provided that you retain the above notices.
 * 2.	You may convey verbatim copies of this program code as you receive it, in any medium, provided that you retain the above notices.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS    * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 ******************************************************************/


package com.recomdata.pipeline.annotation

import com.recomdata.pipeline.util.Util
import groovy.sql.Sql
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator

class GeneInfo {

    private static final Logger log = Logger.getLogger(GeneInfo)

    Sql biomart, searchapp
    String geneInfoTable, geneSynonymTable

    static main(args) {

        PropertyConfigurator.configure("conf/log4j.properties");

        // load the default property file: conf/transmart.properties
        Properties props = Util.loadConfiguration()
        String databaseType = props.get("common.databaseType")

        Sql biomart = Util.createSqlFromPropertyFile(props, databaseType, "biomart")
        Sql searchapp = Util.createSqlFromPropertyFile(props, databaseType, "searchapp")

        if (props.get("skip_load_gene_info").toString().toLowerCase().equals("yes")) {
            log.info "Skip loading Gene Info ..."
        } else {
            File geneInfo = new File(props.get("gene_info_source"))

            // store Human (9606), Mouse (10090), and Rat (10116) data
            File entrez = new File(props.get("gene_info_source") + ".tsv")
            if (entrez.size() > 0) {
                entrez.delete()
                entrez.createNewFile()
            }

            File synonym = new File(props.get("gene_info_source") + ".synonym")
            if (synonym.size() > 0) {
                synonym.delete()
                synonym.createNewFile()
            }

            GeneInfo gi = new GeneInfo()
            gi.setBiomart(biomart)
            gi.setSearchapp(searchapp)
            gi.setGeneInfoTable(props.get("gene_info_table").toString().toLowerCase())
            gi.setGeneSynonymTable(props.get("gene_synonym_table").toString().toLowerCase())

            if (props.get("create_gene_info_table").toString().toLowerCase().equals("yes")) {
                gi.createGeneInfoTable(databaseType)
            } else {
                log.info "Skip creating table ${props.get("gene_info_table")} ..."
            }

            if (props.get("create_gene_synonym_table").toString().toLowerCase().equals("yes")) {
                gi.createGeneSynonymTable(databaseType)
            } else {
                log.info "Skip creating table ${props.get("create_gene_synonym_table")} ..."
            }

            Map selectedOrganism = gi.getSelectedOrganism(props.get("selected_organism"))
            gi.extractSelectedGeneInfo(geneInfo, entrez, synonym, selectedOrganism)
            //gi.readGeneInfo(geneInfo, entrez, synonym)
            gi.loadGeneInfo(databaseType, entrez, props)
            gi.updateBioMarker(databaseType, selectedOrganism)
            gi.loadGeneSynonym(databaseType, synonym, props)
            gi.updateBioDataUid(databaseType, selectedOrganism)
            gi.updateBioDataExtCode(databaseType, selectedOrganism)

            gi.loadSearchKeyword(databaseType, props)
            gi.loadSearchKeywordTerm(databaseType, props)
        }
    }

    /**
     *  load Entrez genes and their synonyms into search_keyword table
     *
     * @param databaseType
     * @param sql
     * @param props
     */
    void loadSearchKeyword(String databaseType, Properties props) {
        if (props.get("skip_search_keyword").toString().toLowerCase().equals("yes")) {
            log.info("Skip loading Entrez genes into SEARCH_KEYWORD ...")
        } else {
            if (databaseType.equals("oracle")) {
                loadOracleSearchKeyword(props)
            } else if (databaseType.equals("netezza")) {
                loadNetezzaSearchKeyword(props)
            } else if (databaseType.equals("postgresql")) {
                loadPostgreSQLSearchKeyword(props)
            } else if (databaseType.equals("db2")) {
                loadDB2SearchKeyword(props)
            } else {
                log.info "Database support for $databaseType will be added soon ... "
            }
        }
    }

    void loadDB2SearchKeyword(Properties props) {

    }

    /**
     * load Entrez genes and their synonyms into search_keyword table @ Netezza
     *
     * @param props
     */
    void loadNetezzaSearchKeyword(Properties props) {

        log.info("Start loading Entrez genes into Netezza SEARCH_KEYWORD ...")

        String qry = """ insert into searchapp.SEARCH_KEYWORD (SEARCH_KEYWORD_ID, KEYWORD, BIO_DATA_ID, UNIQUE_ID, DATA_CATEGORY, DISPLAY_DATA_CATEGORY)
                             select next value for SEQ_SEARCH_DATA_ID, t.bio_marker_name, t.bio_marker_id, 'GENE:'||t.primary_external_id, 'GENE', 'Gene'
                             from (
                                 select distinct bio_marker_name, bio_marker_id, primary_external_id
                                 from biomart.bio_marker
                                 where bio_marker_id not in
                                         (select bio_data_id from searchapp.search_keyword
                                          where data_category='GENE' and bio_data_id is not null)
                             ) t
				         """
        searchapp.execute(qry)

        log.info("End loading Entrez genes into Netezza SEARCH_KEYWORD ...")
    }

    /**
     * load Entrez genes and their synonyms into search_keyword table @ PostgreSQL
     *
     * @param props
     */
    void loadPostgreSQLSearchKeyword(Properties props) {

        log.info("Start loading Entrez genes into PostgreSQL SEARCH_KEYWORD ...")

        String qry = """ insert into searchapp.SEARCH_KEYWORD (SEARCH_KEYWORD_ID, KEYWORD, BIO_DATA_ID, UNIQUE_ID, DATA_CATEGORY, DISPLAY_DATA_CATEGORY)
                             select nextval('SEQ_SEARCH_DATA_ID'), t.mh, t.bio_disease_id, 'DIS:'||t.ui, 'DISEASE', 'Disease'
                             from (
                                 select distinct bio_marker_name, bio_marker_id, primary_external_id
                                 from biomart.bio_marker
                                 where bio_marker_id not in
                                         (select bio_data_id from searchapp.search_keyword
                                          where data_category='GENE' and bio_data_id is not null)
                             ) t
				         """
        searchapp.execute(qry)

        log.info("End loading Entrez genes into PostgreSQL SEARCH_KEYWORD ...")
    }

    /**
     * load Entrez genes and their synonyms into search_keyword table @ Oracle
     *
     * @param props
     */
    void loadOracleSearchKeyword(Properties props) {

        log.info("Start loading Entrez genes into Oracle SEARCH_KEYWORD ...")

        String qry = """ insert into searchapp.SEARCH_KEYWORD (KEYWORD, BIO_DATA_ID, UNIQUE_ID, DATA_CATEGORY, DISPLAY_DATA_CATEGORY)
                             select distinct bio_marker_name, bio_marker_id, primary_external_id
                             from biomart.bio_marker
                             where bio_marker_id not in
                                         (select bio_data_id from searchapp.search_keyword
                                          where data_category='GENE' and bio_data_id is not null)
				         """
        searchapp.execute(qry)

        log.info("End loading Entrez genes into Oracle SEARCH_KEYWORD ...")
    }

    /**
     * load Entrez genes and their synonyms into search_keyword_term table
     *
     * @param databaseType
     * @param sql
     * @param props
     */
    void loadSearchKeywordTerm(String databaseType, Properties props) {
        if (props.get("skip_search_keyword_term").toString().toLowerCase().equals("yes")) {
            log.info("Skip loading Entrez genes and synonyms to SEARCH_KEYWORD_TERM ...")
        } else {
            if (databaseType.equals("oracle")) {
                loadOracleSearchKeywordTerm(props)
            } else if (databaseType.equals("netezza")) {
                loadNetezzaSearchKeywordTerm(props)
            } else if (databaseType.equals("postgresql")) {
                loadPostgreSQLSearchKeywordTerm(props)
            } else if (databaseType.equals("db2")) {
                loadDB2SearchKeywordTerm(props)
            } else {
                log.info "Database support for $databaseType will be added soon ... "
            }
        }
    }

    void loadDB2SearchKeywordTerm(Properties props) {

    }

    /**
     *  load Entrez terms and their synonyms into search_keyword_term table @ PostgreSQL
     *
     * @param props
     */
    void loadPostgreSQLSearchKeywordTerm(Properties props) {

        log.info("Start loading Entrez genes and synonyms to PostgreSQL SEARCH_KEYWORD_TERM ...")

        // Entrez genes
        String qry = """ insert into search_keyword_term (SEARCH_KEYWORD_TERM_ID, KEYWORD_TERM, SEARCH_KEYWORD_ID, RANK,TERM_LENGTH)
							 select nextval('SEQ_SEARCH_DATA_ID'), upper(keyword), search_keyword_id, 1, length(keyword)
							 from search_keyword
							 where search_keyword_id not in
								  (select search_keyword_id from searchapp.search_keyword_term where rank=1)
						 """
        searchapp.execute(qry)

        // Entrez synonym
        String qrys = """ insert into search_keyword_term (SEARCH_KEYWORD_TERM_ID, KEYWORD_TERM, SEARCH_KEYWORD_ID, RANK,TERM_LENGTH)
                              select nextval('SEQ_SEARCH_DATA_ID'), t.keyword_term, t.search_keyword_id, t.rank, t.term_length
                              from (
                                  select upper(e.code) as keyword_term, s.search_keyword_id, 2 as rank, length(s.keyword) as term_length
                                  from search_keyword s, biomart.bio_data_ext_code e, biomart.bio_disease d
                                  where s.bio_data_id=e.bio_data_id and e.bio_data_id=d.bio_disease_id
                                  minus
                                  select keyword_term, search_keyword_id, rank, term_length
                                  from searchapp.search_keyword_term
                                  where rank=2
                              )  t
						 """
        searchapp.execute(qrys)

        log.info "End loading Entrez genes and synonyms to PostgreSQL SEARCH_KEYWORD_TERM ... "
    }

    /**
     *  load Entrez terms and their synonyms into search_keyword_term table @ Netezza
     *
     * @param props
     */
    void loadNetezzaSearchKeywordTerm(Properties props) {

        log.info("Start loading Entrez genes and synonyms to Netezza SEARCH_KEYWORD_TERM ...")

        // Entrez genes
        String qry = """ insert into search_keyword_term (SEARCH_KEYWORD_TERM_ID, KEYWORD_TERM, SEARCH_KEYWORD_ID, RANK,TERM_LENGTH)
							 select next value for SEQ_SEARCH_DATA_ID, upper(keyword), search_keyword_id, 1, length(keyword)
							 from search_keyword
							 where search_keyword_id not in
								  (select search_keyword_id from searchapp.search_keyword_term where rank=1)
						 """
        searchapp.execute(qry)

        // Entrez synonym
        String qrys = """ insert into search_keyword_term (SEARCH_KEYWORD_TERM_ID, KEYWORD_TERM, SEARCH_KEYWORD_ID, RANK,TERM_LENGTH)
                              select next value for SEQ_SEARCH_DATA_ID, t.keyword_term, t.search_keyword_id, t.rank, t.term_length
                              from (
                                  select upper(e.code) as keyword_term, s.search_keyword_id, 2 as rank, length(s.keyword) as term_length
                                  from search_keyword s, biomart.bio_data_ext_code e, biomart.bio_disease d
                                  where s.bio_data_id=e.bio_data_id and e.bio_data_id=d.bio_disease_id
                                  minus
                                  select keyword_term, search_keyword_id, rank, term_length
                                  from searchapp.search_keyword_term
                                  where rank=2
                              )  t
						 """
        searchapp.execute(qrys)

        log.info "End loading Entrez genes and synonyms to Netezza SEARCH_KEYWORD_TERM ... "
    }

    /**
     *  load Entrez terms and their synonyms into search_keyword_term table @ Oracle
     *
     * @param props
     */
    void loadOracleSearchKeywordTerm(Properties props) {

        log.info("Start loading Entrez genes and synonyms to Oracle SEARCH_KEYWORD_TERM ...")

        // Entrez genes
        String qry = """ insert into search_keyword_term (KEYWORD_TERM, SEARCH_KEYWORD_ID, RANK,TERM_LENGTH)
							 select upper(keyword), search_keyword_id, 1, length(keyword)
							 from search_keyword
							 where search_keyword_id not in
								  (select search_keyword_id from searchapp.search_keyword_term where rank=1)
						 """
        searchapp.execute(qry)

        // Entrez synonym
        String qrys = """ insert into search_keyword_term (KEYWORD_TERM, SEARCH_KEYWORD_ID, RANK,TERM_LENGTH)
							  select upper(e.code), s.search_keyword_id, 2, length(s.keyword)
							  from search_keyword s, biomart.bio_data_ext_code e, biomart.bio_disease d
							  where s.bio_data_id=e.bio_data_id and e.bio_data_id=d.bio_disease_id
                              minus
							  select keyword_term, search_keyword_id, rank, term_length
                              from searchapp.search_keyword_term
                              where rank=2
						 """
        searchapp.execute(qrys)

        log.info "End loading Entrez genes and synonyms to Oracle SEARCH_KEYWORD_TERM ... "
    }


    void updateBioMarker(String databaseType, Map selectedOrganism) {
        selectedOrganism.each { taxonomyId, organism ->
            updateBioMarker(databaseType, taxonomyId, organism)
        }
    }


    void updateBioMarker(String databaseType, String taxonomyId, String organism) {
        if (databaseType.equals("oracle")) {
            updateOracleBioMarker(taxonomyId, organism)
        } else if (databaseType.equals("netezza")) {
            updateNetezzaBioMarker(taxonomyId, organism)
        } else if (databaseType.equals("postgresql")) {
            updatePostgreSQLBioMarker(taxonomyId, organism)
        } else if (databaseType.equals("db2")) {
            updateDB2BioMarker(taxonomyId, organism)
        } else {
            log.info("The database $databaseType is not supported yet ...")
        }
    }

    void updateDB2BioMarker(String taxonomyId, String organism) {

    }


    void updatePostgreSQLBioMarker(String taxonomyId, String organism) {
        updateNetezzaBioMarker(taxonomyId, organism)
    }

    void updateNetezzaBioMarker(String taxonomyId, String organism) {

        log.info "Start updating BIO_MARKER for $taxonomyId:$organism using Entrez data ..."

        String qry = """ insert into bio_marker(BIO_MARKER_ID, bio_marker_name, bio_marker_description, organism, primary_source_code,
								primary_external_id, bio_marker_type)
						 select next value for SEQ_BIO_DATA_ID, gene_symbol, gene_descr, ?, 'Entrez', gene_id, 'GENE'
						 from ${geneInfoTable}
						 where tax_id=? and gene_id not in
							 (select primary_external_id from bio_marker where upper(organism)=?) """

        biomart.execute(qry, [organism, taxonomyId, organism])

        log.info "End updating BIO_MARKER for $taxonomyId:$organism using Entrez data ..."
    }


    void updateOracleBioMarker(String taxonomyId, String organism) {

        log.info "Start updating BIO_MARKER for $taxonomyId:$organism using Entrez data ..."

        String qry = """ insert into bio_marker(bio_marker_name, bio_marker_description, organism, primary_source_code,
								primary_external_id, bio_marker_type)
						 select gene_symbol, gene_descr, ?, 'Entrez', to_char(gene_id), 'GENE'
						 from ${geneInfoTable}
						 where tax_id=? and to_char(gene_id) not in
							 (select primary_external_id from bio_marker where upper(organism)=?) """

        biomart.execute(qry, [organism, taxonomyId, organism])

        log.info "End updating BIO_MARKER for $taxonomyId:$organism using Entrez data ..."
    }

    // can be retired
    void updateBioMarker() {

        log.info "Start updating BIO_MARKER using Entrez data ..."

        String qry = """ insert into bio_marker(bio_marker_name, bio_marker_description, organism, primary_source_code,
		                        primary_external_id, bio_marker_type)
					     select gene_symbol, gene_descr, ?, 'Entrez', to_char(gene_id), 'GENE'
						 from ${geneInfoTable}
						 where tax_id=? and to_char(gene_id) not in
						 	(select primary_external_id from bio_marker where upper(organism)=?) """

        log.info "Start updating Home sapiens gene info  ..."
        biomart.execute(qry, [
                "Homo sapiens",
                "9606",
                "HOMO SAPIENS"
        ])
        log.info "End updating Home sapiens gene info  ..."

        log.info "Start updating Mus musculus gene info  ..."
        biomart.execute(qry, [
                "Mus musculus",
                "10090",
                "MUS MUSCULUS"
        ])
        log.info "End updating Mus musculus gene info  ..."

        log.info "Start updating Rattus norvegicus gene info  ..."
        biomart.execute(qry, [
                "Rattus norvegicus",
                "10116",
                "RATTUS NORVEGICUS"
        ])
        log.info "End updating Rattus norvegicus gene info  ..."

        log.info "End updating BIO_MARKER using Entrez data ..."
    }

    /**
     *
     * @param selectedOrganism
     */
    void updateBioDataUid(String databaseType, Map selectedOrganism) {
        selectedOrganism.each { taxonomyId, organism ->
            updateBioDataUid(databaseType, taxonomyId, organism)
        }
    }

    void updateBioDataUid(String databaseType, String taxonomyId, String organism) {
        if (databaseType.equals("oracle")) {
            updateOracleBioDataUid(taxonomyId, organism)
        } else if (databaseType.equals("netezza")) {
            updateNetezzaBioDataUid(taxonomyId, organism)
        } else if (databaseType.equals("postgresql")) {
            updatePostgreSQLBioDataUid(taxonomyId, organism)
        } else if (databaseType.equals("db2")) {
            updateDB2BioDataUid(taxonomyId, organism)
        } else {
            log.info("The database $databaseType is not supported yet ...")
        }
    }

    void updateDB2BioDataUid(String taxonomyId, String organism) {

    }

    void updatePostgreSQLBioDataUid(String taxonomyId, String organism) {

    }


    void updateNetezzaBioDataUid(String taxonomyId, String organism) {

        log.info "Start loading BIO_DATA_UID using Entrez data ..."

        String qry = """ insert into bio_data_uid(bio_data_id, unique_id, bio_data_type)
								select bio_marker_id, 'GENE:'||primary_external_id, 'BIO_MARKER.GENE'
								from biomart.bio_marker where upper(organism)=?
								minus
								select bio_data_id, unique_id, bio_data_type
								from bio_data_uid """

        log.info "Start loading genes from $taxonomyId:$organism  ..."
        biomart.execute(qry, [organism])
        log.info "End loading genes from $taxonomyId:$organism  ..."

        log.info "End loading BIO_DATA_UID using Entrez data ..."
    }


    void updateOracleBioDataUid(String taxonomyId, String organism) {

        log.info "Start loading BIO_DATA_UID using Entrez data ..."

        String qry = """ insert into bio_data_uid(bio_data_id, unique_id, bio_data_type)
								select bio_marker_id, 'GENE:'||primary_external_id, to_nchar('BIO_MARKER.GENE')
								from biomart.bio_marker where upper(organism)=?
								minus
								select bio_data_id, unique_id, bio_data_type
								from bio_data_uid """

        log.info "Start loading genes from $taxonomyId:$organism  ..."
        biomart.execute(qry, [organism])
        log.info "End loading genes from $taxonomyId:$organism  ..."

        log.info "End loading BIO_DATA_UID using Entrez data ..."
    }

    /**
     *
     * @param selectedOrganism
     */
    void updateBioDataExtCode(String databaseType, Map selectedOrganism) {
        selectedOrganism.each { taxonomyId, organism ->
            updateBioDataExtCode(databaseType, taxonomyId, organism)
        }
    }

    void updateBioDataExtCode(String databaseType, String taxonomyId, String organism) {
        if (databaseType.equals("oracle")) {
            updateOracleBioDataExtCode(taxonomyId, organism)
        } else if (databaseType.equals("netezza")) {
            updateNetezzaBioDataExtCode(taxonomyId, organism)
        } else if (databaseType.equals("postgresql")) {
            updatePostgreSQLBioDataExtCode(taxonomyId, organism)
        } else if (databaseType.equals("db2")) {
            updateDB2BioDataExtCode(taxonomyId, organism)
        } else {
            log.info("The database $databaseType is not supported yet ...")
        }
    }

    void updateDB2BioDataExtCode(String taxonomyId, String organism) {

    }

    void updatePostgreSQLBioDataExtCode(String taxonomyId, String organism) {

    }

    void updateNetezzaBioDataExtCode(String taxonomyId, String organism) {

        log.info "Start loading Netezza/PostgreSQL BIO_DATA_EXT_CODE using Entrez's synonyms data ..."
        log.info "Taxonomy Id: $taxonomyId       Organism: $organism"

        String qry = """ insert into bio_data_ext_code(BIO_DATA_EXT_CODE_ID, bio_data_id, code, code_source, code_type, bio_data_type)
                         select next value for SEQ_BIO_DATA_ID, t.bio_data_id, t.code, t.code_source, t.code_type, t.bio_data_type
                         from (
								 select t2.bio_marker_id as bio_data_id, t1.gene_synonym as code, 'Alias' as code_source,
								        'SYNONYM' as code_type, 'BIO_MARKER.GENE' as bio_data_type
								 from ${geneSynonymTable} t1, bio_marker t2
								 where tax_id=? and t1.gene_id = t2.primary_external_id
									  and upper(t2.organism)=?
								 minus
								 select bio_data_id, code, code_source, code_type, bio_data_type
								 from bio_data_ext_code ) t"""

        log.info "Start loading synonyms for genes from $taxonomyId:$organism  ..."
        biomart.execute(qry, [taxonomyId, organism])
        log.info "End loading synonyms for genes from $taxonomyId:$organism  ..."

        log.info "End loading Netezza/PostgreSQL BIO_DATA_EXT_CODE using Entrez's synonyms data ..."
    }

    void updateOracleBioDataExtCode(String taxonomyId, String organism) {

        log.info "Start loading BIO_DATA_EXT_CODE using Entrez's synonyms data ..."

        String qry = """ insert into bio_data_ext_code(bio_data_id, code, code_source, code_type, bio_data_type)
								 select t2.bio_marker_id, t1.gene_synonym, 'Alias', 'SYNONYM', 'BIO_MARKER.GENE'
								 from ${geneSynonymTable} t1, bio_marker t2
								 where tax_id=? and to_char(t1.gene_id) = t2.primary_external_id
									  and upper(t2.organism)=?
								 minus
								 select bio_data_id, code, to_char(code_source), to_char(code_type), bio_data_type
								 from bio_data_ext_code """

        log.info "Start loading synonyms for genes from $taxonomyId:$organism  ..."
        biomart.execute(qry, [taxonomyId, organism])
        log.info "End loading synonyms for genes from $taxonomyId:$organism  ..."

        log.info "End loading BIO_DATA_EXT_CODE using Entrez's synonyms data ..."
    }

    // can be retired
    void updateBioDataExtCode() {

        log.info "Start loading BIO_DATA_EXT_CODE using Entrez'S Synonyms data ..."

        String qry = """ insert into bio_data_ext_code(bio_data_id, code, code_source, code_type, bio_data_type)
						 select t2.bio_marker_id, t1.gene_synonym, 'Alias', 'SYNONYM', 'BIO_MARKER.GENE'
						 from ${geneSynonymTable} t1, bio_marker t2
						 where tax_id=? and to_char(t1.gene_id) = t2.primary_external_id 
							  and upper(t2.organism)=? 
						 minus
						 select bio_data_id, code, to_char(code_source), to_char(code_type), bio_data_type 
						 from bio_data_ext_code """

        log.info "Start loading synonyms for Home sapiens genes  ..."
        biomart.execute(qry, [
                "9606",
                "HOMO SAPIENS"
        ])
        log.info "End loading synonyms for Home sapiens genes  ..."

        log.info "Start loading synonyms for Mus musculus genes  ..."
        biomart.execute(qry, [
                "10090",
                "MUS MUSCULUS"
        ])
        log.info "End loading synonyms for Mus musculus genes  ..."

        log.info "Start loading synonyms for Rattus norvegicus genes  ..."
        biomart.execute(qry, [
                "10116",
                "RATTUS NORVEGICUS"
        ])
        log.info "End loading synonyms for Rattus norvegicus genes  ..."

        log.info "End loading BIO_DATA_EXT_CODE using Entrez'S Synonyms data ..."
    }

    /**
     *
     * 0	tax_id		the unique identifier provided by NCBI Taxonomy for the species or strain/isolate
     * 1	GeneID		the unique identifier for a gene ASN1:  geneid
     * 2	Symbol		the default symbol for the gene ASN1:  gene->locus
     * 3	LocusTag
     * 4	Synonyms
     * 5	dbXrefs
     * 6	chromosome
     * 7	map_location
     * 8	description
     * 9	type_of_gene
     * 10	Symbol_from_nomenclature_authority
     * 11	Full_name_from_nomenclature_authority
     * 12	Nomenclature_status
     * 13	Other_designations
     * 14	Modification_date
     *
     * @param geneInfo
     */

    void readGeneInfo(File geneInfo, File entrez, File synonym) {

        StringBuffer sb = new StringBuffer()
        StringBuffer sbSynonym = new StringBuffer()

        if (geneInfo.size() > 0) {
            log.info "Reading Gene Info file: " + geneInfo.toString()
            geneInfo.eachLine {
                String[] str = it.split(/\t/)
                if (it.indexOf("#Format") != -1) {
                    String[] s = it.replace("#Format: ", "").split(" ")
                    //for(int i in 0 .. s.size()-1) println i + "\t" + s[i]
                } else {
                    if ((str[0].indexOf("9606") == 0) || (str[0].indexOf("10090") == 0) || (str[0].indexOf("10116") == 0)) {
                        sb.append(str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + str[8] + "\n")

                        if (!str[4].equals("-")) {
                            println str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + str[4]
                            if (str[4].indexOf("|") != -1) {
                                String[] tmp = str[4].split(/\|/)
                                tmp.each {
                                    if (!it.equals(null) && (it.trim().size() > 0))
                                        sbSynonym.append(str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + it + "\n")
                                    println str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + it
                                }
                            } else {
                                sbSynonym.append(str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + str[4] + "\n")
                            }
                        }
                    }
                }
            }
        } else {
            log.error(geneInfo.toString() + " is empty.")
            return
        }

        if (entrez.size() > 0) {
            entrez.delete()
            entrez.createNewFile()
        }
        if (sb.size() > 0) entrez.append(sb.toString())

        if (synonym.size() > 0) {
            synonym.delete()
            synonym.createNewFile()
        }
        if (sbSynonym.size() > 0) synonym.append(sbSynonym.toString())
    }



    Map getSelectedOrganism(String selectedOrganism) {

        Map selectedOrganismMap = [:]

        if (selectedOrganism.indexOf(";")) {
            String[] oragnisms = selectedOrganism.split(";")
            for (int n in 0..oragnisms.size() - 1) {
                String[] temp = oragnisms[n].split(":")
                selectedOrganismMap[temp[0]] = temp[1]
            }
        } else {
            selectedOrganismMap[selectedOrganism.split(":")[0]] = selectedOrganism.split(":")[1]
        }

        return selectedOrganismMap
    }



    void extractSelectedGeneInfo(File geneInfo, File entrez, File synonym, Map selectedOrganism) {
        selectedOrganism.each { k, v ->
            extractSelectedGeneInfo(geneInfo, entrez, synonym, k, v)
        }
    }



    void extractSelectedGeneInfo(File geneInfo, File entrez, File synonym, String taxnomyId, String organism) {

        StringBuffer sb = new StringBuffer()
        StringBuffer sbSynonym = new StringBuffer()

        if (geneInfo.size() > 0) {
            log.info "Extracting data for $taxnomyId:$organism from Gene Info file: " + geneInfo.toString()
            geneInfo.eachLine {
                String[] str = it.split(/\t/)
                if (it.indexOf("#Format") != -1) {
                    String[] s = it.replace("#Format: ", "").split(" ")
                    //for(int i in 0 .. s.size()-1) println i + "\t" + s[i]
                } else {
                    if (str[0] == taxnomyId) {
                        sb.append(str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + str[8] + "\n")

                        if (!str[4].equals("-")) {
                            // println str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + str[4]
                            if (str[4].indexOf("|") != -1) {
                                String[] tmp = str[4].split(/\|/)
                                tmp.each {
                                    if (!it.equals(null) && (it.trim().size() > 0))
                                        sbSynonym.append(str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + it + "\n")
                                    //println str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + it
                                }
                            } else {
                                sbSynonym.append(str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + str[4] + "\n")
                            }
                        }
                    }
                }
            }
        } else {
            log.error(geneInfo.toString() + " is empty.")
            return
        }

        if (sb.size() > 0) entrez.append(sb.toString())
        if (sbSynonym.size() > 0) synonym.append(sbSynonym.toString())
    }


    void loadGeneInfo(String databaseType, File geneInfo, Properties props) {
        if (databaseType.equals("oracle")) {
            loadOracleGeneInfo(geneInfo)
        } else if (databaseType.equals("netezza")) {
            loadNetezzaGeneInfo(geneInfo, props)
        } else if (databaseType.equals("postgresql")) {
            loadPostgreSQLGeneInfo(geneInfo)
        } else if (databaseType.equals("db2")) {
            loadDB2GeneInfo(geneInfo)
        } else {
            log.info("The database $databaseType is not supported yet ...")
        }
    }

    void loadNetezzaGeneInfo(File geneInfo, Properties props) {
        String nzload = props.get("nzload")
        String user = props.get("biomart_username")
        String password = props.get("biomart_password")
        String host = props.get("url").split(":")[2].toString().replaceAll("/") { "" }

        def command = "$nzload -u $user -pw $password -host \"$host\" -db transmart -t $geneInfoTable -delim \"\\t\" -outputDir \"c:/temp\" -df \"$geneInfo\""
        log.info "nzload command: " + command
        def proc = command.execute()
        proc.waitFor()
    }

    void loadDB2GeneInfo(File geneInfo) {
        // to be implemented
    }

    void loadPostgreSQLGeneInfo(File geneInfo) {
        // to be implemented
    }

    void loadOracleGeneInfo(File geneInfo) {

        String qry = "insert into $geneInfoTable (tax_id, gene_id, gene_symbol, gene_descr) values (?, ?, ?, ?)"

        if (geneInfo.size() > 0) {
            log.info "Start loading file: " + geneInfo.toString()
            biomart.withTransaction {
                biomart.withBatch(100, qry, { stmt ->
                    geneInfo.eachLine {

                        String[] str = it.split(/\t/)
                        //println str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + str[3]

                        stmt.addBatch([
                                str[0].trim(),
                                str[1].trim(),
                                str[2].trim(),
                                str[3].trim()
                        ])
                    }
                })
            }
        } else {
            log.error("Taxonomy file is empty.")
            return
        }

        log.info "End loading Gene Info file: " + geneInfo.toString()
    }


    void loadGeneSynonym(String databaseType, File geneSynonym, Properties props) {
        if (databaseType.equals("oracle")) {
            loadOracleGeneSynonym(geneSynonym)
        } else if (databaseType.equals("netezza")) {
            loadNetezzaGeneSynonym(geneSynonym, props)
        } else if (databaseType.equals("postgresql")) {
            loadPostgreSQLGeneSynonym(geneSynonym)
        } else if (databaseType.equals("db2")) {
            loadDB2GeneSynonym(geneSynonym)
        } else {
            log.info("The database $databaseType is not supported yet ...")
        }
    }

    void loadDB2GeneSynonym(File geneSynonym) {
        // to be implemented
    }

    void loadPostgreSQLGeneSynonym(File geneSynonym) {
        // to be implemented
    }

    void loadNetezzaGeneSynonym(File geneSynonym, Properties props) {
        String nzload = props.get("nzload")
        String user = props.get("biomart_username")
        String password = props.get("biomart_password")
        String host = props.get("url").split(":")[2].toString().replaceAll("/") { "" }

        def command = "$nzload -u $user -pw $password -host \"$host\" -db transmart -t $geneSynonymTable -delim \"\\t\" -outputDir \"c:/temp\" -df \"$geneSynonym\""
        log.info "nzload command: " + command
        def proc = command.execute()
        proc.waitFor()
    }

    void loadOracleGeneSynonym(File geneSynonym) {

        String qry = "insert into $geneSynonymTable (tax_id, gene_id, gene_symbol, gene_synonym) values (?, ?, ?, ?)"

        if (geneSynonym.size() > 0) {
            log.info "Start loading file: " + geneSynonym.toString()
            biomart.withTransaction {
                biomart.withBatch(100, qry, { stmt ->
                    geneSynonym.eachLine {

                        String[] str = it.split(/\t/)
                        //println str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + str[3]

                        stmt.addBatch([
                                str[0].trim(),
                                str[1].trim(),
                                str[2].trim(),
                                str[3].trim()
                        ])
                    }
                })
            }
        } else {
            log.error("Gene's synonym file is empty.")
            return
        }

        log.info "End loading gene synonym file: " + geneSynonym.toString()
    }


    void createGeneInfoTable(String databaseType) {
        if (databaseType.equals("oracle")) {
            createOracleGeneInfoTable()
        } else if (databaseType.equals("netezza")) {
            createNetezzaGeneInfoTable()
        } else if (databaseType.equals("postgresql")) {
            createPostgreSQLGeneInfoTable()
        } else if (databaseType.equals("db2")) {
            createDb2GeneInfoTable()
        } else {
            log.info "The database $databaseType is not supported yet ..."
        }
    }

    void createDb2GeneInfoTable() {
        // to be implemented
    }

    void createPostgreSQLGeneInfoTable() {
        createNetezzaGeneInfoTable()
    }

    void createNetezzaGeneInfoTable() {

        String currentSchema = Util.getNetezzaCurrentSchema(biomart)

        String qry = "select count(*) from information_schema.tables where lower(table_name)=? and TABLE_SCHEMA='$currentSchema'"
//        log.info qry

        if (biomart.firstRow(qry, [geneInfoTable])[0] > 0) {
            log.info "Drop Netezza table $geneInfoTable ..."
            qry = "drop table $geneInfoTable"
            biomart.execute(qry)
        }

        log.info "Start creating Netezza/PostgreSQL table $geneInfoTable ..."

        qry = """ create table $geneInfoTable (
						tax_id   int,
						gene_id   varchar(10),
						gene_symbol   varchar(200),
						gene_descr    varchar(4000)
				 ) """
        biomart.execute(qry)

        log.info "End creating Netezza/PostgreSQL table $geneInfoTable ..."
    }

    void createOracleGeneInfoTable() {

        String qry = "select count(1) from user_tables where table_name=upper(?)"
        if (biomart.firstRow(qry, [geneInfoTable])[0] > 0) {
            log.info "Drop table $geneInfoTable ..."
            qry = "drop table $geneInfoTable purge"
            biomart.execute(qry)
        }

        log.info "Start creating table $geneInfoTable ..."

        qry = """ create table $geneInfoTable (
						tax_id   number(10,0),
						gene_id   number(20,0),
						gene_symbol   varchar2(200),
						gene_descr    varchar2(4000)
				 ) """
        biomart.execute(qry)

        log.info "End creating table $geneInfoTable ..."
    }


    void createGeneSynonymTable(String databaseType) {
        if (databaseType.equals("oracle")) {
            createOracleGeneSynonymTable()
        } else if (databaseType.equals("netezza")) {
            createNetezzaGeneSynonymTable()
        } else if (databaseType.equals("postgresql")) {
            createPostgreSQLGeneSynonymTable()
        } else if (databaseType.equals("db2")) {
            createDb2GeneSynonymTable()
        } else {
            log.info "The database $databaseType is not supported yet ..."
        }
    }

    void createDb2GeneSynonymTable() {
        // to be implemented
    }

    void createPostgreSQLGeneSynonymTable() {
        createNetezzaGeneSynonymTable()
    }

    void createNetezzaGeneSynonymTable() {
        String currentSchema = Util.getNetezzaCurrentSchema(biomart)
        String qry = "select count(*) from information_schema.tables where lower(table_name)=? and TABLE_SCHEMA='$currentSchema'"
        log.info qry

        if (biomart.firstRow(qry, [geneSynonymTable])[0] > 0) {
            log.info "Drop Netezza/PostgreSQL  table $geneSynonymTable ..."
            qry = "drop table $geneSynonymTable"
            biomart.execute(qry)
        }

        log.info "Start creating Netezza/PostgreSQL table $geneSynonymTable ..."

        qry = """ create table $geneSynonymTable (
								tax_id        int,
								gene_id       varchar(10),
								gene_symbol   varchar(200),
								gene_synonym       varchar(200)
						 ) """
        biomart.execute(qry)

        log.info "End creating Netezza/PostgreSQL table $geneSynonymTable ..."
    }

    void createOracleGeneSynonymTable() {

        String qry = "select count(1) from user_tables where table_name=upper(?)"
        if (biomart.firstRow(qry, [geneSynonymTable])[0] > 0) {
            log.info "Drop table $geneSynonymTable ..."
            qry = "drop table $geneSynonymTable purge"
            biomart.execute(qry)
        }

        log.info "Start creating table $geneSynonymTable ..."

        qry = """ create table $geneSynonymTable (
								tax_id        number(10,0),
								gene_id       number(20,0),
								gene_symbol   varchar2(200),
								gene_synonym       varchar2(200)
						 ) """
        biomart.execute(qry)

        log.info "End creating table $geneSynonymTable ..."
    }


    void setGeneInfoTable(String geneInfoTable) {
        this.geneInfoTable = geneInfoTable
    }


    void setGeneSynonymTable(String geneSynonymTable) {
        this.geneSynonymTable = geneSynonymTable
    }


    void setSearchapp(Sql searchapp) {
        this.searchapp = searchapp
    }

    void setBiomart(Sql biomart) {
        this.biomart = biomart
    }
}

/*
 * 
 insert into bio_marker nologging 
 (BIO_MARKER_NAME, BIO_MARKER_DESCRIPTION, ORGANISM, PRIMARY_EXTERNAL_ID, BIO_MARKER_TYPE)
 select GENE_SYMBOL, GENE_DESCR, 'Homo sapiens', GENE_ID, 'GENE' 
 from gene_info
 where tax_id=9606 and to_char(gene_id) not in 
 (select PRIMARY_EXTERNAL_ID from bio_marker where upper(ORGANISM) = 'HOMO SAPIENS')
 ;
 commit;
 insert into bio_marker nologging 
 (BIO_MARKER_NAME, BIO_MARKER_DESCRIPTION, ORGANISM, PRIMARY_EXTERNAL_ID, BIO_MARKER_TYPE)
 select GENE_SYMBOL, GENE_DESCR, 'Rattus norvegicus', GENE_ID, 'GENE' 
 from gene_info
 where tax_id=10116 and to_char(gene_id) not in 
 (select PRIMARY_EXTERNAL_ID from bio_marker where upper(ORGANISM) = 'RATTUS NORVEGICUS')
 ;
 commit;
 insert into bio_marker nologging 
 (BIO_MARKER_NAME, BIO_MARKER_DESCRIPTION, ORGANISM, PRIMARY_EXTERNAL_ID, BIO_MARKER_TYPE)
 select GENE_SYMBOL, GENE_DESCR, 'Mus musculus', GENE_ID, 'GENE' 
 from gene_info
 where tax_id=10090 and to_char(gene_id) not in 
 (select PRIMARY_EXTERNAL_ID from bio_marker where upper(ORGANISM) = 'MUS MUSCULUS')
 ;
 commit;
 ===========================================================================
 gene_info                                       recalculated daily
 ---------------------------------------------------------------------------
 tab-delimited
 one line per GeneID
 Column header line is the first line in the file.
 Note: subsets of gene_info are available in the DATA/GENE_INFO
 directory (described later)
 ---------------------------------------------------------------------------
 tax_id:
 the unique identifier provided by NCBI Taxonomy
 for the species or strain/isolate
 GeneID:
 the unique identifier for a gene
 ASN1:  geneid
 Symbol:
 the default symbol for the gene
 ASN1:  gene->locus
 LocusTag:
 the LocusTag value
 ASN1:  gene->locus-tag
 Synonyms:
 bar-delimited set of unofficial symbols for the gene
 dbXrefs:
 bar-delimited set of identifiers in other databases
 for this gene.  The unit of the set is database:value.
 chromosome:
 the chromosome on which this gene is placed.
 for mitochondrial genomes, the value 'MT' is used.
 map location:
 the map location for this gene
 description:
 a descriptive name for this gene
 type of gene:
 the type assigned to the gene according to the list of options
 provided in http://www.ncbi.nlm.nih.gov/IEB/ToolBox/CPP_DOC/lxr/source/src/objects/entrezgene/entrezgene.asn
 Symbol from nomenclature authority:
 when not '-', indicates that this symbol is from a
 a nomenclature authority
 Full name from nomenclature authority:
 when not '-', indicates that this full name is from a
 a nomenclature authority
 Nomenclature status:
 when not '-', indicates the status of the name from the
 nomenclature authority (O for official, I for interim)
 Other designations:
 pipe-delimited set of some alternate descriptions that
 have been assigned to a GeneID
 '-' indicates none is being reported.
 Modification date:
 the last date a gene record was updated, in YYYYMMDD format
 */			