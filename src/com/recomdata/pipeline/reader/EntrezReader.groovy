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

package com.recomdata.pipeline.reader

import com.recomdata.pipeline.util.Util
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator

class EntrezReader {

    private static final Logger log = Logger.getLogger(EntrezReader)

    HashMap<String, ArrayList<String>> geneArray, synonymArray

    static main(args) {

        PropertyConfigurator.configure("conf/log4j.properties");

        // load the default property file: conf/transmart.properties and conf/Entrez.properties
        Properties props = Util.loadConfiguration("conf/Entrez.properties", "conf/transmart.properties")
        String databaseType = props.get("common.databaseType")

        if (props.get("entrez.skip").toString().toLowerCase().equals("yes")) {
            log.info "Skip processing Entrez Gene data ..."
        } else {
            String geneInfo = props.get("entrez.gene_info_source")

            EntrezReader entrezReader = new EntrezReader()

            Map selectedOrganism = entrezReader.getSelectedOrganism(props.get("entrez.selected_organism"))
            entrezReader.extractEntrez(geneInfo, selectedOrganism)

            HashMap<String, ArrayList<String>> geneList = entrezReader.getGeneArray()
            HashMap<String, ArrayList<String>> synonymList = entrezReader.getSynonymArray()
        }
    }

    /**
     * extract TaxId:Species paris from Entrez.properties file and store them into a HashMap<String, String>
     *
     * @param selectedOrganism
     * @return
     */
    Map getSelectedOrganism(String selectedOrganism) {

        Map selectedOrganismMap = [:]

        if (selectedOrganism.indexOf(";")) {
            String[] oragnisms = selectedOrganism.split(";")
            for (int n in 0..oragnisms.size() - 1) {
                String[] temp = oragnisms[n].split(":")
                selectedOrganismMap[temp[0]] = temp[1]
            }
        } else {
            selectedOrganismMap[selectedOrganism.split(":")[0]] = selectedOrganism.split(":")[1]
        }

        return selectedOrganismMap
    }


    /**
     * extract multiple species from Entrez gene_info file
     *
     * @param geneInfo
     * @param selectedOrganism
     */
    void extractEntrez(String geneInfo, Map selectedOrganism) {

        geneArray = new HashMap<String, ArrayList<String>> ()
        synonymArray = new HashMap<String, ArrayList<String>> ()

        selectedOrganism.each { taxId, species ->
            extractEntrez(geneInfo, taxId, species)
        }
    }


    /**
     * extract a single specifies from Entrez gene_info file
     *
     * @param geneInfo
     * @param taxId
     * @param species
     */
    void extractEntrez(String geneInfo, String taxId, String species) {

        ArrayList<String> gene = new ArrayList<String>()
        ArrayList<String> synonym = new ArrayList<String>()

        File entrez = new File(geneInfo)
        if (entrez.size() > 0) {
            entrez.eachLine {
                log.trace it
                String[] str = it.split(/\t/)

                if (str[0] == taxId) {
//                    gene.add(str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + str[8])
                    gene.add(species + "\t" + str[1] + "\t" + str[2] + "\t" + str[8])
                    if(str[4].size() > 0){
//                        if(!str[4].trim().equals("-")) synonym.addAll(getSynonyms(str[0], str[1], str[4]))
                        if(!str[4].trim().equals("-")) synonym.addAll(getSynonyms(species, str[1], str[4]))
                    }
                }
            }
        } else {
            log.error("The file \"" + geneInfo.toString() + "\" is empty.")
        }

        geneArray.put(species, gene)
        synonymArray.put(species, synonym)
    }


    /**
     * convert "|" delimited synonyms into ArrayList
     *
     * @param synonyms
     * @return
     */
    ArrayList<String> getSynonyms(String species, String geneId, String synonyms) {

        ArrayList<String> synonym = new ArrayList<String>()

        if (synonyms.indexOf("|") != -1) {
            String[] rec = synonyms.split(/\|/)
            rec.each {
                String s = it.trim()
                if (!s.equals(null) && s.size() > 0) synonym.add("$species\t$geneId\t$s")
            }
        } else {
            synonym.add("$species\t$geneId\t$synonyms")
        }
        return synonym
    }
}
