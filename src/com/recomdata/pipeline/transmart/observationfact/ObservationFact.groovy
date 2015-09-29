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

import org.apache.log4j.Logger;

import groovy.sql.Sql

abstract class ObservationFact {

    private static final Logger log = Logger.getLogger(ObservationFact)

    Sql i2b2demodata
    String studyName


    void insertObservationFact(ArrayList<String> observationFacts) {
        if (isObservationFactExist()) {
            log.warn("$studyName data already exists in observation_fact!")
        }
        observationFacts.each {
            insertObservationFact(it)
        }

    }

    abstract void insertObservationFact(String observationFact)


    /**
     * check if there are any records in observation_fact from the current study
     *
     * @return
     */
    abstract boolean isObservationFactExist()


    void setStudyName(String studyName) {
        this.studyName = studyName
    }


    void setI2b2demodata(Sql i2b2demodata) {
        this.i2b2demodata = i2b2demodata
    }
}
