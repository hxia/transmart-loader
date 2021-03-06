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
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS    * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 ******************************************************************/


package com.recomdata.pipeline.transmart.patientdimension

import com.recomdata.pipeline.util.Util
import org.apache.log4j.Logger

class NetezzaPatientDimension extends PatientDimension {

    private static final Logger log = Logger.getLogger(NetezzaPatientDimension)


    void insertPatientDimension(String subjectId, int age, String gender, String vitalStatus) {

    }

    boolean isPatientDimensionExist(String subjectId) {

    }

    /**
     *  extract patient info from PLINK's FAM file and load them into
     *  i2b2demodata's patient_dimension table, one patient per record
     *
     * @param sql db handler
     * @param fam FAM from binary PLINK files
     * @return
     */

    def loadPatientDimensionFromFam(File fam) {

        if (!fam.exists()) {
            log.error("Cannot fine the file: " + fam.toString())
            throw new RuntimeException("Cannot fine the file: " + fam.toString())
        }

        log.info "Start loading data into PATIENT_DIMENSION from: " + fam.toString()

        String qry = "insert into patient_dimension(sex_cd, sourcesystem_cd) values (?, ?)"

        fam.eachLine {
            String[] str = (it.indexOf("\t") != -1) ? it.split("\t") : it.split(" +")

            // used for cleaning up U Mich's UMCCC subject id
            def indId = Util.cleanupId(str[1].trim())
            //str[1].replace("G", "").replace("T", "").replace("_2", "").replace("NA", "")

            // translate gender from "1" to "M" (Male),
            //        "2" to "F" (Female), and other to "U" (Unknown)
            String sex = ""
            if (str[4].equals("1")) sex = 'Male'
            else if (str[4].equals("2")) sex = 'Female'
            else sex = 'Unknown'

            if (isPatientNumber(indId)) {
                log.info "Patient number exists for $indId ($studyName) "
            } else {
                String sourcesystem_cd = studyName + ":" + indId
                def patient_num = getPatientNumberByIndividualId(sourcesystem_cd)
                if (patient_num > 0) {
                    log.info "Subject id exists for : " + patient_num + "(" + sourcesystem_cd + ")"
                } else {
                    log.info "Create new patient record for : " + sourcesystem_cd
                    i2b2demodata.execute(qry, [sex, sourcesystem_cd])
                }
            }
        }
        log.info "End loading data into PATIENT_DIMENSION ..."
    }


    def loadNetezzaPatientDimensionFromSamples(Map samples) {

        log.info "Start loading data into PATIENT_DIMENSION from Samples ... "

        String qry = """ insert into patient_dimension(patient_num, sex_cd, sourcesystem_cd, import_date)
                         values (next value for i2b2demodata.SQ_PATIENT_NUM, ?, ?, now()) """
        String update = "update patient_dimension set sex_cd=? where sourcesystem_cd=?"

        samples.each { key, val ->
            String sourcesystem_cd = studyName + ":" + key
            String gender = val.split(":")[1].toString().trim()
            def patient_num = getPatientNumberByIndividualId(sourcesystem_cd)
            if (patient_num > 0) {
                log.info "Patient exists for : " + sourcesystem_cd + "(" + patient_num + "), so update its gender"
                if (!gender.equals(null) && gender.size() > 0) i2b2demodata.execute(qry, [gender, sourcesystem_cd])
            } else {
                log.info "New patient for : " + sourcesystem_cd
                if (!gender.equals(null) && gender.size() > 0) i2b2demodata.execute(qry, [gender, sourcesystem_cd])
                else i2b2demodata.execute(qry, [null, sourcesystem_cd])
            }
        }

        log.info "End loading data into PATIENT_DIMENSION from Samples ..."
    }


    def loadPatientDimensionFromSamples(Map samples) {

        log.info "Start loading data into PATIENT_DIMENSION from Samples ... "

        String qry = "insert into patient_dimension(sourcesystem_cd, import_date) values (?, sysdate)"

        samples.each { key, val ->
            String sourcesystem_cd = studyName + ":" + key
            def patient_num = getPatientNumberByIndividualId(sourcesystem_cd)
            if (patient_num > 0) {
                log.info "Patient exists for : " + sourcesystem_cd + "(" + patient_num + ")"
            } else {
                log.info "New patient for : " + sourcesystem_cd
                i2b2demodata.execute(qry, [sourcesystem_cd])
            }
        }

        log.info "End loading data into PATIENT_DIMENSION from Samples ..."
    }


    boolean isPatientNumber(String patientNum) {
        try {
            int pid = Integer.parseInt(patientNum)
            String qry = "select count(1) from patient_dimension where patient_num=" + patientNum
            def r = i2b2demodata.firstRow(qry)
            if (r[0] > 0) return true
            else return false
        } catch (Exception e) {
            return false
        }
    }


    void addPatient(String subjectId) {
        String qry = "insert into patient_dimension(sourcesystem_cd) values(?)"

        if (getPatientNumberBySubjectId(subjectId) == 0) {
            log.info "Add $subjectId to PATIENT_DIMENSION ... "
            i2b2demodata.execute(qry, [
                    studyName + ":" + subjectId
            ])
        } else {
            log.info "$subjectId already exists in PATIENT_DIMENSION ... "
        }
    }


    Map getSubjectPatientMap() {
        Map subjectPatientMap = [:]
        String qry = "select patient_num, sourcesystem_cd from patient_dimension where sourcesystem_cd like '" + studyName + ":%'"
        i2b2demodata.eachRow(qry) {
            String id = it.sourcesystem_cd
            subjectPatientMap[id.replace(studyName + ":", "")] = it.patient_num
        }
        return subjectPatientMap
    }


    Map getPatientSubjectMap() {
        Map patientSubjectMap = [:]
        String qry = "select patient_num, sourcesystem_cd from patient_dimension where sourcesystem_cd like '" + studyName + ":%'"
        i2b2demodata.eachRow(qry) {
            String id = it.sourcesystem_cd
            patientSubjectMap[it.patient_num] = id.replace(studyName + ":", "")
        }
        return patientSubjectMap
    }


    Map getPatientGenderMap() {
        Map patientGenderMap = [:]
        // 1 -> "Male"; 2 -> "Female"; 9 -> "Unknown"
        String qry = "select patient_num, upper(sex_cd) sex from patient_dimension where sourcesystem_cd like '" + studyName + ":%'"
        i2b2demodata.eachRow(qry) {
            String gender = ""
            if (it.sex.toString().indexOf("F") != -1) gender = 2
            else if (it.sex.toString().indexOf("M") != -1) gender = 1
            else gender = 9
            patientGenderMap[it.patient_num] = gender
        }
        return patientGenderMap
    }

    /**
     *   use SOURCESYSTEM_CD to retrieve patient_num from PATIENT_DIMENSION table
     *
     * @param i2b2demodata database handler, point to I2B2DEMODATA schema
     * @param sourceId sourecesystem_cd = family_id:individual_id
     * @return
     */

    long getPatientNumberBySubjectId(String subjectId) {
        long pid = 0
        String qry = "select patient_num from patient_dimension where sourcesystem_cd=?"

        def indId = getIndividualId(subjectId)

        i2b2demodata.eachRow(qry, [
                studyName + ":" + indId
        ]) { pid = it.patient_num }
        return pid
    }

    /**
     * 	use FAM file's individual id to lookup patient_num, if it exists, then use it,
     * 	otherwise create a new record in patient_dimension
     *
     * @param sourcesystem_cd
     * @return
     */
    long getPatientNumberByIndividualId(String sourcesystem_cd) {
        long pid = 0
        String qry = "select patient_num from patient_dimension where sourcesystem_cd=?"
        def p = i2b2demodata.firstRow(qry, [sourcesystem_cd])
        if (!p.equals(null)) pid = p[0]
        return pid
    }

    /**
     *
     * @param subjectId family_id:individual_id
     * @return
     */
    def getIndividualId(String subjectId) {
        String[] str = subjectId.split(":")
        def indId = str[1].replace("G", "").replace("T", "").replace("_2", "").replace("NA", "")
        return indId
    }
}
