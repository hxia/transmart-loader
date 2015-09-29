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
  

package com.recomdata.pipeline.transmart.conceptdimension
import org.apache.log4j.Logger

class OracleConceptDimension extends ConceptDimension {

	private static final Logger log = Logger.getLogger(OracleConceptDimension)


    void insertConceptDimension(ArrayList<HashMap<String, String>> concepts){
        concepts.each{
            insertConceptDimension(it)
        }
    }


	void insertConceptDimension(HashMap<String, String> concept){

//		if(tableName.equals(null)) tableName = "CONCEPT_DIMENSION"
//		String qry = "insert into concept_dimension(concept_path, concept_cd, name_char, sourcesystem_cd, table_name) values(?, ?, ?, ?, ?)"
		String qry = "insert into concept_dimension(concept_path, concept_cd, name_char, sourcesystem_cd) values(?, ?, ?, ?)"

        String conceptPath = concept["C_FULLNAME"]
		if(isConceptDimensionExist(conceptPath)){
			log.info "Concept \"$conceptPath\" already exists ..."
		}else{
			i2b2demodata.execute(qry, [
				conceptPath,
                concept["C_BASECODE"],
				concept["C_NAME"],
				studyName
			])

            log.info "insert concept: \"$conceptPath\""
		}
	}


	boolean isConceptDimensionExist(String conceptPath){
		String qry = "select count(*) from concept_dimension where concept_path=?"
		def res = i2b2demodata.firstRow(qry, [conceptPath])
		if(res[0] > 0) return true
		else return false
	}

}
