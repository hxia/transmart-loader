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

package com.recomdata.pipeline.transmart.i2b2secure

import com.recomdata.pipeline.transmart.i2b2.PostgresqlI2b2Secure
import com.recomdata.pipeline.util.Util
import groovy.sql.Sql
import org.apache.log4j.Logger

class I2b2SecureFactory {

    private static final Logger log = Logger.getLogger(I2b2SecureFactory)

    /**
     * create and initialize I2b2Secure instance
     *
     * @param props
     * @return
     */
    I2b2Secure createI2b2Secure(Properties props) {

        String databaseType = props.get("common.databaseType")
        Sql i2b2metadata = Util.createSqlFromPropertyFile(props, databaseType, "i2b2metadata")

        I2b2Secure i2b2Secure = getI2b2Secure(databaseType)
        i2b2Secure.setI2b2metadata(i2b2metadata)
        i2b2Secure.setStudyName(props.get("study.name"))
        i2b2Secure.setI2b2metadataSchema(props.get("common.i2b2metadata.schema"))

        return i2b2Secure
    }


    /**
     * return I2b2Secure instance based on Database Type
     *
     * @param databaseType
     * @return
     */
    public static I2b2Secure getI2b2Secure(String databaseType) {
        if (databaseType.equals("oracle")) {
            return new OracleI2b2Secure()
        } else if (databaseType.equals("netezza")) {
            return new NetezzaI2b2Secure()
        } else if (databaseType.equals("postgresql")) {
            return new PostgresqlI2b2Secure()
        } else if (databaseType.equals("db2")) {
//            loadDB2ConceptPaths(conceptPathToCode)
        } else {
            log.info("The database $databaseType is not supported.")
            return null
        }
    }
}

