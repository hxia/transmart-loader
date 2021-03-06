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

import org.apache.log4j.Logger;

import groovy.sql.Sql

abstract class I2b2 {

	private static final Logger log = Logger.getLogger(I2b2)

//	Properties props
    Sql i2b2metadata
	String studyName, i2b2metadataSchema
//	Map visualAttrs

    abstract void insertI2b2(String c_hlevel, String c_fullnamne, String c_name, String c_visualattributes, String c_comment)

    abstract void insertI2b2(String c_hlevel, String c_fullnamne, String c_basecode, String c_name, String c_visualattributes, String c_comment)

    abstract void insertI2b2(ArrayList<HashMap<String, String>> concepts)

    abstract void insertI2b2(HashMap<String, String> concept)

	abstract boolean isI2b2Exist(String conceptPath)

	void setStudyName(String studyName){
		this.studyName = studyName
	}


    void setI2b2metadata(Sql i2b2metadata){
        this.i2b2metadata = i2b2metadata
    }

    void setI2b2metadataSchema(String i2b2metadataSchema){
        this.i2b2metadataSchema = i2b2metadataSchema
    }
}

