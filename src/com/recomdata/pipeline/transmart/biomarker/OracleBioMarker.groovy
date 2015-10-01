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


package com.recomdata.pipeline.transmart.biomarker

import org.apache.log4j.Logger

class OracleBioMarker extends BioMarker {

    private static final Logger log = Logger.getLogger(OracleBioMarker)

    /**
     * load Entrez genes using a temp table
     */
    void insertBioMarkers() {

        log.info "Start loading Entrez genes into BIO_MARKER ..."

        String qry = """ insert into bio_marker(bio_marker_name, bio_marker_description, organism, primary_source_code,
								primary_external_id, bio_marker_type)
						 select gene_symbol, gene_descr, organism, to_nchar('Entrez'), gene_id, to_nchar('GENE')
						 from ${geneTable}
                         minus
						 select bio_marker_name, bio_marker_description, organism, primary_source_code,
                                primary_external_id, bio_marker_type
                         from bio_marker """

        biomart.execute(qry)

        log.info "End loading Entrez genes into BIO_MARKER ..."
    }

    /**
     * load genes(StringBuffer) (GENE_ID:GENE_NAME:GENE_DESCRIPTION) into bio_marker
     *
     * @param sb
     */
    void insertBioMarker(ArrayList<String> genes) {

        String qry = """insert into bio_marker(bio_marker_id, bio_marker_name, bio_marker_description, organism, primary_source_code, primary_external_id, bio_marker_type)
                        values(seq_bio_data_id.nextval, ?, ?, ?, '$primarySource', ?, '$markerType') """

        if (genes.size() > 0) {
            biomart.withTransaction {
                biomart.withBatch(100, qry, { stmt ->
                    genes.each {
                        String[] str = it.split(/\t/)
                        stmt.addBatch([
                                str[2].trim(),
                                str[3].trim(),
                                str[0].trim(),
                                str[1].trim()
                        ])
                    }
                })
            }
        } else {
            log.error("No genes found.")
        }
    }


    void insertBioMarker(String organism, String geneId, String geneSymbol, String description) {

        String qry = """ insert into bio_marker(bio_marker_id, bio_marker_name, bio_marker_description, organism, primary_source_code,
		                        primary_external_id, bio_marker_type) values(seq_bio_data_id.nextval, ?, ?, ?, ?, ?, ?) """

        if (isBioMarkerExist(geneId, markerType, organism)) {
            log.info "\"$organism:$geneSymbol:$geneId:$markerType\" already exists in BIO_MARKER ..."
        } else {
            biomart.execute(qry, [geneSymbol, description, organism, primarySource, geneId, markerType])
        }
    }


    HashMap<String, String> getGene2BioMarkerMap(String markerType, String organism, String primarySource) {

        HashMap<String, String> gene2MarkerMap = new HashMap<String, String>()

        String qry = """select bio_marker_id, primary_external_id from bio_marker
                        where bio_marker_type=? and organism=? and primary_source_code=? and rownum < 100 """

        try {
            biomart.eachRow(qry, [markerType, organism, primarySource]) {
                gene2MarkerMap[it.primary_external_id] = it.bio_marker_id
            }
//              def rows = biomart.rows(qry, [markerType, organism, primarySource])

            return gene2MarkerMap
        } catch (e) {
            return null
        }
    }

    /**
     * check if Entrez genes for a species are loaded already
     *
     * @param markerType
     * @param organism
     * @return
     */
    boolean isBioMarkerExist(String markerType, String organism) {

        String qry = "select count(1) from bio_marker where bio_marker_type=? and organism=?"

        try {
            def res = biomart.firstRow(qry, [markerType, organism])
            if (res[0] >= 1) return true else return false
        } catch (e) {
            return false
        }
    }

    /**
     * check if an Entrez gene is already loaded
     *
     * @param geneId
     * @param markerType
     * @param organism
     * @return
     */
    boolean isBioMarkerExist(String geneId, String markerType, String organism) {

        String qry = "select count(1) from bio_marker where primary_external_id=? and organism=? and bio_marker_type=?"

        try {
            def res = biomart.firstRow(qry, [geneId, organism, markerType])
            if (res[0] >= 1) return true else return false
        } catch (e) {
            return false
        }
    }
}
