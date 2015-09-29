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
  

package com.recomdata.pipeline.transmart.patientdimension

import groovy.sql.Sql
import org.apache.log4j.Logger

abstract class PatientDimension {

	private static final Logger log = Logger.getLogger(PatientDimension)

	Sql i2b2demodata
	String studyName

    void insertPatientDimension(ArrayList<HashMap<String, String>> patients) {
        patients.each {
            insertPatientDimension(it)
        }
    }

    void insertPatientDimension(HashMap<String, String> patient) {
//        subject_id:2,age:3,gender:5,race:,vital_status:,birth_date:,death_date:,language:,marital_status:,religion:,zip:,statecityzip:,income:
        String subjectId = patient["subject_id"]

        String gender = ""
        if(patient["gender"]) gender = patient["gender"]

        int age = 0
        if(patient["age"]) age = Integer.parseInt(patient["age"])

        String vitalStatus = ""
        if(patient["vital_status"])  vitalStatus = patient["vital_status"]

        insertPatientDimension(subjectId, age, gender, vitalStatus)
    }

    abstract void insertPatientDimension(String subjectId, int age, String gender, String vitalStatus)

    abstract boolean isPatientDimensionExist(String subjectId)

    abstract Map getSubjectPatientMap()

	/**
	 * 
	 * @param i2b2demodata
	 */
	void setSqlForI2b2demodata(Sql i2b2demodata){
		this.i2b2demodata = i2b2demodata
	}


	void setStudyName(String studyName){
		this.studyName = studyName
	}
}
