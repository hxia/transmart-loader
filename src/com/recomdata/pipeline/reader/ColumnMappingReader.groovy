/**
 * Created by haxia on 9/8/2015.
 *
 * Read and process xxx_columns.txt file
 */

package com.recomdata.pipeline.reader

import com.recomdata.pipeline.util.NumericUtil
import com.recomdata.pipeline.util.PrinterUtil
import com.recomdata.pipeline.util.Util
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator

class ColumnMappingReader {

    private static final Logger log = Logger.getLogger(ColumnMappingReader)

    String studyTopNode

    static main(args) {

        PropertyConfigurator.configure("conf/log4j.properties")
        log.info("Current Root Path: " + System.getProperty("user.dir"))

        // load into configuration files
        Properties props = Util.loadConfiguration("conf/Study.properties", "conf/transmart.properties")

        ColumnMappingReader cmr = new ColumnMappingReader()

        String studyTopNode = props.get("study.top_node")
        cmr.setStudyTopNode(studyTopNode)

        ArrayList<String> columnMapping = cmr.getColumnMapping(props.get("study.column_mapping"))
        PrinterUtil.printArrayList("1. Column Mapping", columnMapping, "trace")

        HashMap<String, String> columnConceptMap = cmr.getColumnConceptMap(columnMapping)
        PrinterUtil.printHashMap("2. Column Concept Mapping", columnConceptMap)

        HashSet<String> hashSet = cmr.getColumnConceptSet(columnConceptMap)
        PrinterUtil.printHashSet("3. Column Concept Set", hashSet)

        HashMap<String, String> columnDataTypeMap = cmr.getColumnDataTypeMap(columnMapping)
        PrinterUtil.printHashMap("4. Column Data Type Mapping", columnDataTypeMap)
    }

    /**
     * read column mapping file and return its content as StringBuffer
     *
     * @param columnMappingFile the name of column mapping file
     * @return the content of column mapping as StringBuffer
     */
    ArrayList<String> getColumnMapping(String columnMappingFile) {
        ArrayList<String> columnMapping = new ArrayList<String>()
        File cf = new File(columnMappingFile)
        cf.eachLine {
            columnMapping.add(it)
        }
        return columnMapping
    }


    HashMap<String, String> getColumnDataTypeMap(ArrayList<String> al) {
        HashMap<String, String> hMap = new HashMap<String, String>()
        al.each {
            String[] str = it.split("\t")
            if (NumericUtil.isNumeric(str[2])) {
                // column count start from 0 instead of 1
                hMap[(Integer.parseInt(str[2])-1).toString()] = str[4]
            }
        }

        return hMap
    }


    /**
     * read column mapping as StringBuffer and return a HashMap with
     *      [column index, partial concept:column visual attributes:column data type]
     *
     * @param columnMapping
     * @return
     */
    HashMap<String, String> getColumnConceptMap(ArrayList<String> columnMapping) {
        HashMap<String, String> hMap = new HashMap<String, String>()
        columnMapping.each {
            String[] str = it.split("\t")
            if (NumericUtil.isNumeric(str[2])) {
//                if(str[1]) hMap[str[2]] = str[1] + "\t" + str[3] + "\t" + str[4]
                // column count start from 0 instead of 1
                if(str[1]) hMap[(Integer.parseInt(str[2])-1).toString()] = studyTopNode + "/" + str[1].replace("_", " ").replace("+", "/")
                else hMap[str[2]] =studyTopNode
            }
        }
        return (hMap)
    }


    /**
     * top-level concept paths (folder nodes) from column mapping file
     *
     * @param columnConceptMap
     * @return
     */
    HashSet<String> getColumnConceptSet(HashMap<String, String> columnConceptMap) {
        HashSet<String> columnConceptSet = new HashSet<String>()

        // add node for Study
        columnConceptSet.add(studyTopNode)

        // add nodes for columns
        columnConceptMap.each { k, v ->
                columnConceptSet.add(v)
        }
        return columnConceptSet.sort()
    }

}
