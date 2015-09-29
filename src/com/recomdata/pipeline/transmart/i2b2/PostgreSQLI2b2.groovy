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
  

package com.recomdata.pipeline.transmart.i2b2
import org.apache.log4j.Logger

class PostgresqlI2b2 extends I2b2 {

	private static final Logger log = Logger.getLogger(PostgresqlI2b2)


    void insertI2b2(ArrayList<HashMap<String, String>> concepts) {
        concepts.each {
            insertI2b2(it)
        }
    }

    void insertI2b2(HashMap<String, String> concept) {
        String c_hlevel = concept["C_HLEVEL"].toString()
        String c_fullname = concept["C_FULLNAME"].toString().replace("/", "\\")
        String c_basecode = concept["C_BASECODE"]
        String c_name = concept["C_NAME"]
        String c_visualAttributes = concept["C_VISUALATTRIBUTES"]

        insertI2b2(c_hlevel, c_fullname, c_basecode, c_name, c_visualAttributes, "")
    }


    /**
     * add a concept into i2b2metadata.i2b2 table, and make sure the sequence i2b2metadata.seq_c_basecoe exists
     *  and has the following trigger in place:
     *

     DROP TRIGGER I2B2METADATA.TRG_I2B2_C_BASECODE;

     CREATE OR REPLACE TRIGGER I2B2METADATA.TRG_I2B2_C_BASECODE
     before insert ON I2B2METADATA.I2B2
     for each row
     begin
     if inserting then
     if :NEW."C_BASECODE" is null then
     select SEQ_C_BASECODE.nextval into :NEW."C_BASECODE" from dual;
     end if;
     end if;
     end;
     /

     * @param c_hlevel
     * @param c_fullname
     * @param c_name
     * @param c_visualattributes
     * @param c_comment
     */
    void insertI2b2(String c_hlevel, String c_fullname, String c_name, String c_visualattributes, String c_comment) {

        String qry = """ INSERT INTO I2B2 (c_hlevel, C_FULLNAME, C_NAME, C_VISUALATTRIBUTES, c_synonym_cd,
								C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_DIMCODE, C_TOOLTIP,
								SOURCESYSTEM_CD, C_OPERATOR, c_columndatatype, c_comment, i2b2_id)
						 VALUES(?, ?, ?, ?, 'N',
							   'CONCEPT_CD', 'CONCEPT_DIMENSION', 'CONCEPT_PATH', ?, ?,
								?, ?, 'LIKE', 'T',
								?, i2b2_id_seq.nextval)""";

        if (isI2b2Exist(c_fullname)) {
            log.info "$c_fullname already exists ..."
        } else {
            log.info "insert concept path: $c_fullname into I2B2 ..."
            i2b2metadata.execute(qry, [c_hlevel, c_fullname, c_name, c_visualattributes, c_fullname, c_fullname, studyName, c_comment])
        }
    }


    void insertI2b2(String c_hlevel, String c_fullname, String c_basecode, String c_name, String c_visualattributes, String c_comment) {

        String qry = """ INSERT INTO I2B2 (c_hlevel, C_FULLNAME, C_BASECODE, C_NAME, C_VISUALATTRIBUTES, c_synonym_cd,
								C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, C_DIMCODE, C_TOOLTIP,
								SOURCESYSTEM_CD, C_OPERATOR, c_columndatatype, c_comment, update_date, i2b2_id)
						 VALUES(?, ?, ?, ?, ?, 'N',
							   'CONCEPT_CD', 'CONCEPT_DIMENSION', 'CONCEPT_PATH', ?, ?,
								?, 'LIKE', 'T',	?, sysdate, i2b2_id_seq.nextval)""";

        if (isI2b2Exist(c_fullname)) {
            log.info "Concept \"$c_fullname\" already exists ..."
        } else {
            log.info "Loading concept \"$c_fullname\" into I2B2 ..."
            i2b2metadata.execute(qry, [c_hlevel, c_fullname, c_basecode, c_name, c_visualattributes, c_fullname, c_fullname, studyName, c_comment])
        }
    }


	boolean isI2b2Exist(String conceptPath){
		String qry = "select count(*) from i2b2 where c_fullname=?"
		def res = i2b2metadata.firstRow(qry, [conceptPath])
		if(res[0] > 0) return true
		else return false
	}
}

