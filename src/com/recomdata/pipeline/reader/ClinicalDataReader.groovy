/**
 * Created by haxia on 9/14/2015.
 */

package com.recomdata.pipeline.reader
import com.recomdata.pipeline.util.PrinterUtil
import com.recomdata.pipeline.util.Util
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator

class ClinicalDataReader {
    private static final Logger log = Logger.getLogger(ClinicalDataReader)

    StringBuffer clinicalData
    String clinicalDataHeader
    ArrayList columnConcepts

    static main(args) {

        PropertyConfigurator.configure("conf/log4j.properties")
        log.info("Current Root Path: " + System.getProperty("user.dir"))

        // dynamically change LOG LEVEL
//        getRootLogger().setLevel(Level.TRACE)

        // load into configuration files
        Properties props = Util.loadConfiguration("conf/Study.properties", "conf/transmart.properties")

        ClinicalDataReader cdr = new ClinicalDataReader()

        String clinicalData = props.get("study.clinical_data")
        log.info("01. Clinical Data File: " + clinicalData)

        cdr.parseClinicalData(clinicalData)
        String clinicalDataHeader = cdr.getClinicalDataHeader()
        PrinterUtil.printString("2. Clinical Data Header",clinicalDataHeader, "trace")

        int numOfColumns = cdr.getNumberOfColumn()
        log.info("02. Number of Clinical Data Columns: " + numOfColumns.toString())

        StringBuffer sbClinicalData =  cdr.getClinicalData()
        PrinterUtil.printStringBuffer("3. Clinical Data Record", sbClinicalData, "trace")

        ArrayList <String> clinicalDataColumnHeader =   cdr.getClinicalDataColumnHeader()
        PrinterUtil.printArrayList("4. Clinical Data Column Header", clinicalDataColumnHeader)

        ArrayList <HashSet> leafConcepts = cdr.getDistinctColumnValues(numOfColumns, cdr.getClinicalData())
        PrinterUtil.printHashSetArray("5. Column Leaf Concepts", leafConcepts, "info")
    }


    /**
     * read clinical data and return an Array of HashSet [column index: column value]
     *
     * @param numOfColumns
     * @param clinicalData
     * @return
     */
    ArrayList <HashSet> getDistinctColumnValues(int numOfColumns, StringBuffer clinicalData) {

        ArrayList<HashSet> cc = new ArrayList<HashSet>(numOfColumns)
        for (int i in 0..numOfColumns) cc[i] = new HashSet()

        clinicalData.eachLine {
            String[] str = it.split("\t")
            for (int k in 3..str.size() - 1) if (str[k]) cc[k].add(str[k])
        }
        return cc
    }


    /**
     * read clinical data file and return its column header as String and records as StringBuffer
     *
     * @param clinicalDataFile
     */
    void parseClinicalData(String clinicalDataFile) {
        StringBuffer sb = new StringBuffer()
        File cf = new File(clinicalDataFile)

        int index = 0
        cf.eachLine {
            if (index > 0) {
                sb.append(it + "\n")
            } else setClinicalDataHeader(it)
            index++
        }
        setClinicalData(sb)
    }


    /**
     *  pass in Column Header of clinical data file (_clinical_data.txt), and return an array of column header
     *
     * @param header
     * @return
     */
    ArrayList <String> getClinicalDataColumnHeader() {
        ArrayList al = new ArrayList()
        clinicalDataHeader.split("\t").each {
            al.add(it)
        }
        return al
    }


    /**
     * count total number of columns
     *
     * @param clinicalDataHeader
     * @return
     */
    int getNumberOfColumn() {
        return clinicalDataHeader.split("\t").size()
    }

}

