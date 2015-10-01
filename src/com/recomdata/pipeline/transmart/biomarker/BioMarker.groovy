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

import groovy.sql.Sql
import org.apache.log4j.Logger

abstract class BioMarker {

	private static final Logger log = Logger.getLogger(BioMarker)

    Properties props
	Sql biomart
	String organism, primarySource, markerType, biomartSchemaName, geneTable

    void insertBioMarkers(ArrayList<String> genes){
        if(isBioMarkerExist(markerType, organism)) {
            log.info("Start incremental loading $organism:$markerType from $primarySource into bio_marker table ...")
            genes.each {
                String [] rec = it.split("\t")
                insertBioMarker(rec[0], rec[1], rec[2], rec[3])
            }
            log.info("End incremental loading $organism:$markerType from $primarySource into bio_marker table ...")
        } else {
            log.info("Start batch loading $organism:$markerType from $primarySource into bio_marker table ...")
            insertBioMarker(genes)
            log.info("End batch loading $organism:$markerType from $primarySource into bio_marker table ...")
        }
    }

    abstract void insertBioMarkers()

    abstract void insertBioMarker(ArrayList<String> genes)

	abstract void insertBioMarker(String organism, String geneId, String geneSymbol, String description)

    abstract HashMap<String, String> getGene2BioMarkerMap(String markerType, String organism, String primarySource)

    abstract  boolean isBioMarkerExist(String geneId, String markerType, String organism)

    abstract  boolean isBioMarkerExist(String markerType, String organism)

	void setOrganism(String organism){
		this.organism = organism
	}

    void setPrimarySource(String primarySource){
        this.primarySource = primarySource
    }

    void setMarkerType(String markerType)   {
       this.markerType = markerType
    }

	void setBiomart(Sql biomart){
		this.biomart = biomart
	}

    void setProperties(Properties props){
        this.props = props
    }

    void setBiomartSchemaName(String biomartSchemaName){
        this.biomartSchemaName = biomartSchemaName
    }
}
