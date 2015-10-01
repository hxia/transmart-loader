/*************************************************************************
 * tranSMART - translational medicine data mart
 *
 * Copyright 2015 ConvergeHEALTH by Deloitte, Deloitte Consulting LLP.
 *
 * This product includes software developed at ConvergeHEALTH by Deloitte, Deloitte Consulting LLP.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software  * Foundation, either version 3 of the License, or (at your option) any later version, along with the following terms:
 * 1.	You may convey a work based on this program in accordance with section 5, provided that you retain the above notices.
 * 2.	You may convey verbatim copies of this program code as you receive it, in any medium, provided that you retain the above notices.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 ******************************************************************/

package com.recomdata.pipeline.loader.entrezloader
import com.recomdata.pipeline.reader.EntrezReader
import com.recomdata.pipeline.transmart.biodatauid.BioDataUid
import com.recomdata.pipeline.transmart.biodatauid.BioDataUidFactory
import com.recomdata.pipeline.transmart.biomarker.BioMarker
import com.recomdata.pipeline.transmart.biomarker.BioMarkerFactory
import com.recomdata.pipeline.transmart.searchkeyword.SearchKeyword
import com.recomdata.pipeline.transmart.searchkeyword.SearchKeywordFactory
import com.recomdata.pipeline.transmart.searchkeywordterm.SearchKeywordTermFactory
import com.recomdata.pipeline.transmart.searchkeywordterm.SearchKeywordTerm
import com.recomdata.pipeline.util.PrinterUtil
import com.recomdata.pipeline.util.Util
import groovy.sql.Sql
import org.apache.log4j.Logger

abstract class Entrez {

    private static final Logger log = Logger.getLogger(Entrez)
    private Runtime runtime = Runtime.getRuntime();

    Sql biomart, searchapp
    String geneTable, synonymTable

    HashMap<String, ArrayList<String>> geneList, synonymList
    Map<String, String> organisms
    Properties props

//    static main(args) {
//
//        PropertyConfigurator.configure("conf/log4j.properties");
//
//        // load the default property file: conf/transmart.properties and conf/Entrez.properties
//        Properties props = Util.loadConfiguration("conf/Entrez.properties", "conf/transmart.properties")
//        String databaseType = props.get("common.databaseType")
//
//        Sql biomart = Util.createSqlFromPropertyFile(props, databaseType, "biomart")
//        Sql searchapp = Util.createSqlFromPropertyFile(props, databaseType, "searchapp")
//
//        if (props.get("entrez.skip").toString().toLowerCase().equals("yes")) {
//            log.info "Skip loading Entrez Gene Info ..."
//        } else {
//            File geneInfo = new File(props.get("entrez.gene_info_source"))
//
//            // store Human (9606), Mouse (10090), and Rat (10116) data
//            File entrez = new File(props.get("entrez.gene_info_source") + ".tsv")
//            if (entrez.size() > 0) {
//                entrez.delete()
//                entrez.createNewFile()
//            }
//
//            File synonym = new File(props.get("entrez.gene_info_source") + ".synonym")
//            if (synonym.size() > 0) {
//                synonym.delete()
//                synonym.createNewFile()
//            }
//
////            if (props.get("create_gene_info_table").toString().toLowerCase().equals("yes")) {
////                entrezLoader.createGeneInfoTable(databaseType)
////            } else {
////                log.info "Skip creating table ${props.get("gene_info_table")} ..."
////            }
//
////            if (props.get("create_gene_synonym_table").toString().toLowerCase().equals("yes")) {
////                entrezLoader.createGeneSynonymTable(databaseType)
////            } else {
////                log.info "Skip creating table ${props.get("create_gene_synonym_table")} ..."
////            }
//
//        }
//    }

    void loadEntrezGene(HashMap<String, ArrayList<String>> geneList) {
        geneList.each { organism, genes ->
            loadBioMarker(genes, organism)
            loadBioDataUid(organism)
            loadSearchKeyword(organism)
            loadSearchKeywordTerm(organism)
        }
    }

    void loadEntrezSynonym(HashMap<String, ArrayList<String>> synonymList) {
        synonymList.each { organism, synonyms ->
            log.info organism
//            HashMap<String, String> gene2BioMarkerMap = getGene2BioMarkerMap("GENE", organism, "Entrez")

        }
    }

    HashMap<String, String> getGene2BioMarkerMap(String markerType, String organism, String source) {

        HashMap<String, String> gene2BioMarker = new HashMap<String, String> ()

        log.info "Start retrieving gene_id to bio_marker_id mapping from bio_marker table ... "
        log.info("${new Date()}:  " + Util.getMemoryUsage(runtime))

        BioMarker bioMarker = new BioMarkerFactory().createBioMarker(props)
        bioMarker.setMarkerType(markerType)
        bioMarker.setPrimarySource(source)
        bioMarker.setOrganism(organism)

        gene2BioMarker = bioMarker.getGene2BioMarkerMap(markerType, organism, source)

        log.info("${new Date()}:  " + Util.getMemoryUsage(runtime))
        log.info "End retrieving gene_id to bio_marker_id mapping from bio_marker table ... "

        return  gene2BioMarker
    }

    void loadSearchKeywordTerm(String organism) {

        if (props.get("entrez.skip_load_search_keyword_term").toString().toLowerCase().equals("yes")) {
            log.info "Skip loading Entrez into search_keyword_term table ..."
        } else {
            log.info "Start loading Entrez genes into search_keyword_term table ..."
            SearchKeywordTerm searchKeywordTerm = new SearchKeywordTermFactory().createSearchKeywordTerm(props)
//            searchKeyword.insertEntrezBioDataUid(organism)
            log.info "End loading Entrez genes into search_keyword_term table ..."
        }
    }

    void loadSearchKeyword(String organism) {

        if (props.get("entrez.skip_load_search_keyword").toString().toLowerCase().equals("yes")) {
            log.info "Skip loading Entrez into search_keyword table ..."
        } else {
            log.info "Start loading Entrez genes into search_keyword table ..."
            SearchKeyword searchKeyword = new SearchKeywordFactory().createSearchKeyword(props)
//            searchKeyword.insertEntrezBioDataUid(organism)
            log.info "End loading Entrez genes into search_keyword table ..."
        }
    }

    void loadBioDataUid(String organism) {

        if (props.get("entrez.skip_load_bio_data_uid").toString().toLowerCase().equals("yes")) {
            log.info "Skip loading Entrez into bio_data_uid table ..."
        } else {
            log.info "Start loading Entrez genes into bio_data_uid table ..."
            BioDataUid bioDataUid = new BioDataUidFactory().createBioDataUid(props)
            bioDataUid.insertEntrezBioDataUid(organism)
            log.info "End loading Entrez genes into bio_data_uid table ..."
        }
    }

    void loadBioMarker(ArrayList<String> genes, String organism) {

        if (props.get("entrez.skip_load_bio_marker").toString().toLowerCase().equals("yes")) {
            log.info "Skip loading Entrez into bio_marker table ..."
        } else {
            PrinterUtil.printArrayList("Loading Entrez genes into bio_marker table", genes, "trace")

            log.info "Start loading Entrez genes into bio_marker table ..."
            BioMarker bioMarker = new BioMarkerFactory().createBioMarker(props)
            bioMarker.setMarkerType("GENE")
            bioMarker.setPrimarySource("Entrez")
            bioMarker.setOrganism(organism)

            // HOMO SAPIENS \t 105379704 \t LOC105379704 \t CASP-like protein 4A1
            bioMarker.insertBioMarkers(genes)
            log.info "End loading Entrez genes into bio_marker table ..."
        }
    }

    /**
     * extract genes and synonyms from Entrez's gene_info file
     */
    void getEntrez() {

        String geneInfo = props.get("entrez.gene_info_source")

        EntrezReader entrezReader = new EntrezReader()
        Map selectedOrganism = entrezReader.getSelectedOrganism(props.get("entrez.selected_organism"))
        entrezReader.extractEntrez(geneInfo, selectedOrganism)

        setOrganisms(selectedOrganism)
        setGeneList(entrezReader.getGeneArray())
        setSynonymList(entrezReader.getSynonymArray())
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

    /**
     *
     * @param selectedOrganism
     */
    void updateBioDataUid(String databaseType, Map selectedOrganism) {
        selectedOrganism.each { taxonomyId, organism ->
            updateBioDataUid(databaseType, taxonomyId, organism)
        }
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

    void loadGeneInfo(HashMap<String, ArrayList<String>> genes){
         genes.each { organism, geneList ->
             loadGeneInfo(geneList)
         }
    }

    abstract void loadGeneInfo(ArrayList<String> genes)

    void loadGeneSynonym(HashMap<String, ArrayList<String>> synonyms){
        synonyms.each { organism, synonymList ->
            loadGeneSynonym(synonymList)
        }
    }

    abstract void loadGeneSynonym(ArrayList<String> synonyms)

    abstract void createGeneTable()

    abstract void createSynonymTable()
}
