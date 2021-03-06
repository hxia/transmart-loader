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


package com.recomdata.pipeline.transmart.biodataextcode

import org.apache.log4j.Logger

class OracleBioDataExtCode extends BioDataExtCode {

    private static final Logger log = Logger.getLogger(OracleBioDataExtCode)

    /**
     * load Entrez synonyms from temp table into bio_data_ext_code
     */
    void insertEntrezBioDataExtCode(String synonymTable) {

        log.info "Start loading Entrez's synonyms into BIO_DATA_EXT_CODE using temp table ..."

        String qry = """ insert into bio_data_ext_code(bio_data_id, code, code_source, code_type, bio_data_type)
								 select t2.bio_marker_id, t1.gene_synonym, to_nchar('Alias'), to_nchar('SYNONYM'), 'BIO_MARKER.GENE'
								 from ${synonymTable} t1, bio_marker t2
								 where t1.gene_id = t2.primary_external_id
								 minus
								 select bio_data_id, to_nchar(code), code_source, code_type, bio_data_type
								 from bio_data_ext_code """

        biomart.execute(qry)

        log.info "End loading Entrez's synonyms into BIO_DATA_EXT_CODE using temp table ..."
    }

    /**
     *
     * @param extCodeMap code -> bio_data_type	bio_data_ext_code_id
     */
    void loadBioDataExtCode(Map extCodeMap) {

        String code, dataType, qry
        long bioDataId

        extCodeMap.each { k, v ->
            code = k
            dataType = v.split("\t")[0]
            bioDataId = Integer.parseInt(v.split("\t")[1])

            if (isBioDataExtCodeExist(dataType, bioDataId)) {
                log.info "(\"$code\", $dataType) already exists in BIO_DATA_EXT_CODE ..."
            } else {
                log.info "Load (\"$code\", $dataType) into BIO_DATA_EXT_CODE ..."

                qry = """ insert into bio_data_ext_code(code, code_source, code_type, bio_data_type, bio_data_id) values(?, ?, ?, ?, ?) """
                biomart.execute(qry, [
                        code,
                        'Alias',
                        'SYNONYM',
                        dataType,
                        bioDataId
                ])
            }
        }
    }


    boolean isBioDataExtCodeExist(long bioDataId) {
        String qry = "select count(1) from bio_data_ext_code where bio_data_id=?"
        if (biomart.firstRow(qry, [bioDataId])[0] > 0) {
            return true
        } else {
            return false
        }
    }


    boolean isBioDataExtCodeExist(String dataType, long bioDataId) {
        String qry = "select count(1) from bio_data_ext_code where bio_data_type=? and bio_data_id=?"
        if (biomart.firstRow(qry, [
                dataType,
                bioDataId
        ])[0] > 0) {
            return true
        } else {
            return false
        }
    }

}
