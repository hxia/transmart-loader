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

package com.recomdata.pipeline.loader.entrezloader

import org.apache.log4j.Logger

class OracleEntrez extends Entrez {

    private static final Logger log = Logger.getLogger(OracleEntrez)

    void loadGeneInfo(ArrayList<String> geneInfo) {

        String qry = "insert into $geneTable (organism, gene_id, gene_symbol, gene_descr) values (?, ?, ?, ?)"

        if (geneInfo.size() > 0) {
            log.info "Start loading Entrez genes into table $geneTable ..."
            biomart.withTransaction {
                biomart.withBatch(1000, qry, { stmt ->
                    geneInfo.each {
                        String[] str = it.split(/\t/)
                        stmt.addBatch([str[0], str[1], str[2], str[3]])
                    }
                })
            }
            log.info "End loading Entrez genes into table $geneTable ..."
        } else {
            log.error("No genes found.")
        }
    }


    void loadGeneSynonym(ArrayList<String> geneSynonym) {

        String qry = "insert into $synonymTable (organism, gene_id, gene_synonym) values (?, ?, ?)"

        if (geneSynonym.size() > 0) {
            log.info "Start loading Entrez gene synonyms into table $synonymTable ..."
            biomart.withTransaction {
                biomart.withBatch(1000, qry, { stmt ->
                    geneSynonym.each {
                        String[] str = it.split(/\t/)
                        stmt.addBatch([str[0], str[1],str[2]])
                    }
                })
            }
            log.info "End loading Entrez gene synonyms into table $synonymTable ..."
        } else {
            log.error("No gene synonym found.")
        }
    }


    void createGeneTable() {

        String qry = "select count(1) from user_tables where table_name=upper(?)"
        if (biomart.firstRow(qry, [geneTable])[0] > 0) {
            log.info "Start dropping table $geneTable ..."
            qry = "drop table $geneTable purge"
            biomart.execute(qry)
            log.info "End dropping table $geneTable ..."
        }

        log.info "Start creating table $geneTable ..."
        qry = """ create table $geneTable (
						organism      nvarchar2(200),
						gene_id       nvarchar2(20),
						gene_symbol   nvarchar2(200),
						gene_descr    nvarchar2(2000)
				 ) """
        biomart.execute(qry)
        log.info "End creating table $geneTable ..."
    }


    void createSynonymTable() {

        String qry = "select count(1) from user_tables where table_name=upper(?)"
        if (biomart.firstRow(qry, [synonymTable])[0] > 0) {
            log.info "Start dropping table $synonymTable ..."
            qry = "drop table $synonymTable purge"
            biomart.execute(qry)
            log.info "End dropping table $synonymTable ..."
        }

        log.info "Start creating table $synonymTable ..."
        qry = """ create table $synonymTable (
						organism        nvarchar2(200),
						gene_id         nvarchar2(20),
						gene_synonym    nvarchar2(200)
				  ) """
        biomart.execute(qry)

        log.info "End creating table $synonymTable ..."
    }

}
