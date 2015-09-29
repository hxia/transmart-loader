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


package com.recomdata.pipeline.transmart.observationfact

import org.apache.log4j.Logger

class OracleObservationFact extends ObservationFact {

    private static final Logger log = Logger.getLogger(OracleObservationFact)

    /**
     *
     * @param observationFact
     */
    void insertObservationFact(String observationFact) {
        // valtype_cd ('T' -- Text data type; 'N' -- numeric data type)
        // tval_char ('E' for numeric, other for text value)

//        CREATE UNIQUE INDEX TRANSMART.OBSERVATION_FACT_PKEY ON TRANSMART.OBSERVATION_FACT
//        (PATIENT_NUM, CONCEPT_CD, PROVIDER_ID, MODIFIER_CD)

        String qry = """ insert into observation_fact (patient_num, concept_cd, modifier_cd, valtype_cd, tval_char,
							    nval_num, sourcesystem_cd, import_date, valueflag_cd, provider_id,
							    encounter_num, instance_num, location_cd )
						 values(?, ?, '@', ?, ?,
								?, ?, sysdate, '@', '@',
								SEQ_ENCOUNTER_NUM.nextval, 1, '@')
					"""
        String[] rec = observationFact.split("\t")
        if(rec[2].equals("T"))  {
            i2b2demodata.execute(qry, [rec[0], rec[1], rec[2], rec[3], null, studyName])
        } else if(rec[2].equals("N"))  {
            i2b2demodata.execute(qry, [rec[0], rec[1], rec[2], 'E', rec[3], studyName])
        } else {
            log.warn("Unsuuported data type \"${rec[2]}\"!")
        }
    }


    void loadObservationFact(Map subjects) {

        log.info "Start loading OBSERVATION_FACT ..."

        String conceptPath
        subjects.each { key, val ->

            long patientNum = subjectToPatient[key]
            if (val.equals(null) || val.size() == 0) conceptPath = basePath
            else conceptPath = basePath + val + "/"
            String conceptCode = conceptPathToCode[conceptPath]

            insertObservationFact(patientNum, conceptCode)
        }
    }


    void insertObservationFact(long patientNum, String conceptCode) {

        String qry = """ insert into observation_fact (patient_num, concept_cd, modifier_cd
							,valtype_cd
							,tval_char
							,nval_num
							,sourcesystem_cd
							,import_date
							,valueflag_cd
							,provider_id
							,location_cd
							)
						 values(?, ?, ?
								  ,'T' -- Text data type
								  ,'E'  --Stands for Equals for Text Types
								  ,null	--	not numeric for Proteomics
								  ,?
								  ,sysdate
								  ,'@'
								  ,'@'
								  ,'@')
							""";

        if (isObservationFactExist(patientNum, conceptCode)) {
            log.info "($patientNum, $conceptCode) already exists in OBSERVATION_FACT ..."
        } else {
            i2b2demodata.execute(qry, [
                    patientNum,
                    conceptCode,
                    studyName,
                    studyName
            ])
        }
    }

    boolean isObservationFactExist(long patientNum, String conceptCode) {
        String qry = "select count(*) from observation_fact where patient_num=? and concept_cd=?"
        def res = i2b2demodata.firstRow(qry, [patientNum, conceptCode])
        if (res[0] > 0) return true
        else return false
    }


    boolean isObservationFactExist() {
        String qry = "select count(1) from observation_fact where sourcesystem_cd=?"
        def res = i2b2demodata.firstRow(qry, [studyName])
        if (res[0] > 0) return true
        else return false
    }


    void setBasePath(String basePath) {
        this.basePath = basePath
    }


    void setSubjectToPatient(Map subjectToPatient) {
        this.subjectToPatient = subjectToPatient
    }

    void setConceptPathToCode(Map conceptPathToCode) {
        this.conceptPathToCode = conceptPathToCode
    }


}
