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

import com.recomdata.pipeline.util.Util
import groovy.sql.Sql
import org.apache.log4j.Logger

class BioMarkerFactory {

    private static final Logger log = Logger.getLogger(BioMarkerFactory)

    BioMarker createBioMarker(Properties props) {

        String databaseType = props.get("common.databaseType")
        Sql biomart = Util.createSqlFromPropertyFile(props, databaseType, "biomart")

        BioMarker bioMarker = selectBioMarker(databaseType)
        bioMarker.setBiomart(biomart)
        bioMarker.setBiomartSchemaName(props.get("common.biomart.schema"))

        return bioMarker
    }


    private BioMarker selectBioMarker(String databaseType) {
        if (databaseType.equals("oracle")) {
            return new OracleBioMarker()
        } else if (databaseType.equals("netezza")) {
            return new NetezzaBioMarker()
        } else if (databaseType.equals("postgresql")) {
            return new PostgresqlBioMarker()
        } else if (databaseType.equals("db2")) {
            //return new DB2GeneInfo()
        } else {
            log.info "Database support for $databaseType will be added soon ... "
            return null
        }
    }
}
