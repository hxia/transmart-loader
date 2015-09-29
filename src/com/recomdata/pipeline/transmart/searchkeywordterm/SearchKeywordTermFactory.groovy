
/*************************************************************************
 * tranSMART - translational medicine data mart
 *
 * Copyright 2008-2012 2015 ConvergeHEALTH by Deloitte, Deloitte Consulting LLP.
 *
 * This product includes software developed at 2015 ConvergeHEALTH by Deloitte, Deloitte Consulting LLP.
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


package com.recomdata.pipeline.transmart.searchkeyword

import com.recomdata.pipeline.transmart.searchkeywordterm.NetezzaSearchKeywordTerm
import com.recomdata.pipeline.transmart.searchkeywordterm.OracleSearchKeywordTerm
import com.recomdata.pipeline.transmart.searchkeywordterm.PostgresqlSearchKeywordTerm
import com.recomdata.pipeline.transmart.searchkeywordterm.SearchKeywordTerm
import com.recomdata.pipeline.util.Util
import groovy.sql.Sql
import org.apache.log4j.Logger

class SearchKeywordTermFactory {

    private static final Logger log = Logger.getLogger(SearchKeywordTermFactory)

    SearchKeywordTerm createSearchKeywordTerm(Properties props) {

        String databaseType = props.get("common.databaseType")
        Sql searchapp = Util.createSqlFromPropertyFile(props, databaseType, "searchapp")

        SearchKeywordTerm searchKeywordTerm = getSearchKeywordTerm(databaseType)
        searchKeywordTerm.setSearchapp(searchapp)

        return searchKeywordTerm
    }


    public static SearchKeywordTerm getSearchKeywordTerm(String databaseType) {
        if (databaseType.equals("oracle")) {
            return new OracleSearchKeywordTerm()
        } else if (databaseType.equals("netezza")) {
            return new NetezzaSearchKeywordTerm()
        } else if (databaseType.equals("postgresql")) {
            return new PostgresqlSearchKeywordTerm()
        } else if (databaseType.equals("db2")) {
//            loadDB2ConceptPaths(conceptPathToCode)
        } else {
            log.info("The database $databaseType is not supported.")
            return null
        }
    }

}
