package com.recomdata.pipeline.transmart.loader

import com.recomdata.pipeline.reader.ClinicalDataReader
import com.recomdata.pipeline.reader.ColumnMappingReader
import com.recomdata.pipeline.transmart.conceptdimension.ConceptDimension
import com.recomdata.pipeline.transmart.conceptdimension.ConceptDimensionFactory
import com.recomdata.pipeline.transmart.i2b2.I2b2
import com.recomdata.pipeline.transmart.i2b2.I2b2Factory
import com.recomdata.pipeline.transmart.i2b2secure.I2b2Secure
import com.recomdata.pipeline.transmart.i2b2secure.I2b2SecureFactory
import com.recomdata.pipeline.transmart.observationfact.ObservationFact
import com.recomdata.pipeline.transmart.observationfact.ObservationFactFactory
import com.recomdata.pipeline.transmart.patientdimension.PatientDimension
import com.recomdata.pipeline.transmart.patientdimension.PatientDimensionFactory
import com.recomdata.pipeline.util.NumericUtil
import com.recomdata.pipeline.util.PrinterUtil
import com.recomdata.pipeline.util.Util
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator

class ClinicalDataLoader {

    private static final Logger log = Logger.getLogger(ClinicalDataLoader)

    Properties props
    String studyBaseNode, studyName
    String columnMapping, clinicalData
    HashMap<String, String> valueBasecodeMap

    static main(args) {

        PropertyConfigurator.configure("conf/log4j.properties");
        log.info("Current Root Path: " + System.getProperty("user.dir"))

        // dynamically change LOG LEVEL
//        LogManager.getRootLogger().setLevel(Level.TRACE)

        // load into configuration files
        Properties props = Util.loadConfiguration("conf/Study.properties", "conf/transmart.properties")

        ClinicalDataLoader cdl = new ClinicalDataLoader()
        cdl.setProps(props)

        cdl.setStudyName(props.get("study.name"))
        log.info("01. Study Name: " + cdl.getStudyName())

        cdl.setStudyBaseNode(props.get("study.top_node"))
        log.info("02. Study Base Node: " + cdl.getStudyBaseNode())

        cdl.setColumnMapping(props.get("study.column_mapping"))
        cdl.setClinicalData(props.get("study.clinical_data"))

        cdl.loadClinicalData()
    }


    void loadClinicalData() {

        // load the content of _columns.txt mapping file (tab delimited) and return it as StringBuffer
        ColumnMappingReader cmr = new ColumnMappingReader()
        ArrayList<String> columnMapping = cmr.getColumnMapping(columnMapping)
        PrinterUtil.printArrayList("10. Content of Column Mapping File: " + columnMapping, columnMapping, "trace")

        HashMap<String, String> columnDataTypeMap = cmr.getColumnDataTypeMap(columnMapping)
        PrinterUtil.printHashMap("11. Column Data Type Map", columnDataTypeMap)

        cmr.setStudyTopNode(studyBaseNode)
        HashMap<String, String> columnConceptMap = cmr.getColumnConceptMap(columnMapping)
        PrinterUtil.printHashMap("12. Column Concept Map", columnConceptMap)

        HashSet<String> columnConceptSet = cmr.getColumnConceptSet(columnConceptMap)
        PrinterUtil.printHashSet("13. Column Concept Set", columnConceptSet)

        // load _clinical_data.txt mapping file (tab delimited)
        ClinicalDataReader cdr = new ClinicalDataReader()
        cdr.parseClinicalData(clinicalData)

        int numOfColumns = cdr.getNumberOfColumn()
        log.info("14. Number of Columns in Clinical Data File: " + numOfColumns)

        StringBuffer sbClinicalData = cdr.getClinicalData()
        PrinterUtil.printStringBuffer("3. Content of Clinical Data: " + clinicalData, sbClinicalData, "trace")

        ArrayList<String> columnHeader = cdr.getClinicalDataColumnHeader()
        PrinterUtil.printArrayList("5. Reformatted Column Header of Clinical Data in " + clinicalData, columnHeader, "trace")

        ArrayList<HashSet> columnConcept = cdr.getDistinctColumnValues(numOfColumns, sbClinicalData)
        PrinterUtil.printHashSetArray("4. Distinct Column Values in " + clinicalData, columnConcept, "trace")

//        ArrayList conceptPathList = getConceptPath(concepts, columnHeader)
//        if (conceptPathList.size() > 0) PrinterUtil.printArrayList("7. Concept Path", conceptPathList)

        HashMap<String, String> vcpMap = getValueConceptPath(columnHeader, columnDataTypeMap, columnConceptMap, columnConcept)
        PrinterUtil.printHashMap("6. Concept Path for Column Values", vcpMap, "info")

        ArrayList<HashMap<String, String>> concepts = getConceptPath(columnDataTypeMap, columnConceptSet, vcpMap)
        PrinterUtil.printHashMapArray("7. Concept HashMap", concepts)
        PrinterUtil.printHashMap("8. Column Value - C_BASECODE Map", valueBasecodeMap)

        ArrayList<HashMap<String, String>> patientDimensionData = getPatientDimension(sbClinicalData)
        PrinterUtil.printHashMapArray("9. Patient Dimension Record", patientDimensionData, "trace")

        loadI2b2(concepts)
        loadI2b2Secure(concepts)
        loadConceptDimension(concepts)
        loadPatientDimension(patientDimensionData)

        Map subjectPatientMap = getSubjectPatientMap()
        ArrayList<String> observationData = getObservationData(sbClinicalData, valueBasecodeMap, subjectPatientMap)
        loadObservationData(observationData)
    }


    ArrayList<HashMap<String, String>> getPatientDimension(StringBuffer clinicalData) {

        HashMap<String, String> patientDataMap = getPatientDataMap()
        PrinterUtil.printHashMap("Patient Data Map", patientDataMap, "debug")

        ArrayList<HashMap<String, String>> patient = new ArrayList<HashMap<String, String>>()
//        HashMap<String, String> patientRecord = new HashMap<String, String>()

        int index = 0
        clinicalData.eachLine {
            index++
            HashMap<String, String> patientRecord = new HashMap<String, String>()
            String[] rec = it.split("\t")
            for (int i in 0..rec.size() - 1) {
                String columnName = patientDataMap[i + ""]
                if (columnName) {
                    patientRecord[columnName] = rec[i]
                }
            }
            patient.add(patientRecord)
        }

        return patient
    }


    HashMap<String, String> getPatientDataMap() {
        String dataMapping = props.get("study.patient")
        HashMap<String, String> patientDataMap = new HashMap<String, String>()

        String[] str = dataMapping.split(",")
        str.each {
            String[] rec = it.split(":")
            if (rec.size() > 1) patientDataMap[rec[1]] = rec[0]
        }
        return patientDataMap
    }


    ArrayList<String> getObservationData(StringBuffer clinicalData, HashMap<String, String> valueBasecodeMap, HashMap<String, String> subjectPatientMap) {

        PrinterUtil.printHashMap("21. Column Value - C_BASECODE Map", valueBasecodeMap)

        ArrayList<String> observationData = new ArrayList<String>()

        // subject_id|patient_num:concept_cd:valtype_cd:tval_char|nval_num
        clinicalData.eachLine {
            String[] rec = it.split("\t")
            for (int i in 3..rec.size() - 1) {
                String key = i + ":" + rec[i]
                if (valueBasecodeMap[key]) {
                    observationData.add(subjectPatientMap[rec[2]] + "\t" + valueBasecodeMap[key] + "\tT\t" + rec[i])
                    log.info rec[2] + "\t" + subjectPatientMap[rec[2]] + "\t" + valueBasecodeMap[key] + "\tT\t" + rec[i]
                } else {
                    observationData.add(subjectPatientMap[rec[2]] + "\t" + valueBasecodeMap[i + ""] + "\tN\t" + rec[i])
                    log.info rec[2] + "\t" + subjectPatientMap[rec[2]] + "\t" + valueBasecodeMap[i + ""] + "\tN\t" + rec[i]
                }
            }
        }
        return observationData
    }


    ArrayList<HashMap<String, String>> getConceptPath(HashMap<String, String> columnDataTypeMap,
                                                      HashSet<String> columnConceptSet, HashMap<String, String> vcpMap) {

        ArrayList<HashMap<String, String>> concepts = new ArrayList<HashMap<String, String>>()
        HashMap<String, String> concept //= new  HashMap<String, String>()

        valueBasecodeMap = new HashMap<String, String>()

        int index = 0
        columnConceptSet.each {
            index++
            String c_name = it.split("/")[-1]

            concept = new HashMap<String, String>()

//            log.info "C_FULLNAME: " + it
//            log.info "C_NAME: " + c_name
//            log.info "C_VISUALATTRIBUTES: FA"
//            log.info "C_BASECODE: " + studyName + "N" + index
//            log.info "SOURCESYSTEM_CD: " + studyName
//            log.info ""

            concept["C_HLEVEL"] = it.split("/").size() - 2
            concept["C_FULLNAME"] = it + "/"
            concept["C_NAME"] = c_name
            concept["C_VISUALATTRIBUTES"] = "FA"
            concept["C_BASECODE"] = studyName + "N" + index
            concept["SOURCESYSTEM_CD"] = studyName
            concepts.add(concept)
        }

//        index = 0
        for (String key : vcpMap.keySet().sort()) {
            index++
            String val = vcpMap[key]
            String c_name = val.split("/")[-1]
            String c_visualAttributes = ""
            String c_basecode = ""

            if ((key.indexOf(":") > -1) || (columnDataTypeMap[key].toUpperCase().equals("N"))) {
                c_visualAttributes = "LA"
                c_basecode = studyName + "C" + index
            } else {
                c_visualAttributes = "FA"
                c_basecode = studyName + "N" + index
            }

//            log.info "C_FULLNAME: " + val
//            log.info "C_NAME: " + c_name
//            log.info "C_VISUALATTRIBUTES: " + c_visualAttributes
//            log.info "C_BASECODE: " + studyName + "C" + index
//            log.info "SOURCESYSTEM_CD: " + studyName
//            log.info ""

            concept = new HashMap<String, String>()

            concept["C_HLEVEL"] = val.split("/").size() - 2
            concept["C_FULLNAME"] = val + "/"
            concept["C_NAME"] = c_name
            concept["C_VISUALATTRIBUTES"] = c_visualAttributes
            concept["C_BASECODE"] = c_basecode
            concept["SOURCESYSTEM_CD"] = studyName
            concepts.add(concept)

            valueBasecodeMap[key] = c_basecode
        }

        return concepts
    }


    HashMap<String, String> getValueConceptPath(ArrayList<String> columnHeader, HashMap<String, String> columnDataTypeMap,
                                                HashMap<String, String> columnConceptMap, ArrayList<HashSet> columnConcept) {
        HashMap<String, String> vcpMap = new HashMap<String, String>()
        for (int i in 0..columnConcept.size() - 1) {
            if (columnConcept[i]) {
//                log.info(i + ":" + columnConceptMap[i.toString()] + "/" + columnHeader[i])
                vcpMap[i.toString()] = columnConceptMap[i.toString()] + "/" + columnHeader[i]

                for (Object object : columnConcept[i]) {
                    String dataType = columnDataTypeMap[i.toString()]
                    String value = (String) object
                    if (dataType.equals("T")) {
                        String conceptPath = columnConceptMap[i.toString()] + "/" + columnHeader[i] + "/" + value
//                        log.info(i + ":\t" + columnDataTypeMap[i.toString()] + "\t" + columnConceptMap[i.toString()] + "/" + columnHeader[i] + "/" + value)
//                        log.info(i + ":" + value + "\t" + conceptPath)
                        vcpMap[i + ":" + value] = conceptPath
                    } else if (dataType.equals("N")) {
                        if (NumericUtil.isNumeric(value)) {
//                            log.info(i + ":\t" + columnDataTypeMap[i.toString()] + "\t" + columnConceptMap[i.toString()] + "/" + columnHeader[i] + "/\t" + value)
                        } else {
                            log.warn(i + ":\t" + columnDataTypeMap[i.toString()] + "\t" + columnConceptMap[i.toString()] + "/" + columnHeader[i] + "/\t" + value)
                        }
                    }
                }
            }
        }

        return vcpMap
    }


    ArrayList getColumnConceptPath(ArrayList concepts, ArrayList clinicalDataHeader) {

        ArrayList al = new ArrayList()
        for (int i in 1..concepts.size() - 1) {
            String[] str = concepts[i].split("\t")
//            al.add(str[1] + "\t" + str[2] + "\t" + str[3] + "\t" + str[4])
            int index = Integer.parseInt(str[1]) - 1
            log.debug("C_FULLNAME: " + studyBaseNode + "/" + str[0].replace("+", "/") + "/" + clinicalDataHeader[index])
            log.debug("C_BASECODE: " + studyName + "C" + str[1])
            log.trace str[0] + "\t" + str[1] + "\t" + str[2] + "\t" + str[3] + "\t" + clinicalDataHeader[index]
            log.debug("\n")

        }
        return al
    }

    /**
     * build concept paths from column mapping file (_columns.txt)
     *
     * @param columnMapping the content of _columns.txt
     *
     * @return _cclinical_data.txt's column's base concept path as an array of [col_index, conceptPath]
     */
    ArrayList getColumnConceptMap(ArrayList<String> columnMapping) {

        ArrayList al = new ArrayList()
        columnMapping.each { line ->
            String[] str = line.split("\t")
            if (str[1]) al.add(str[1] + "\t" + str[2] + "\t" + str[3] + "\t" + str[4])
        }
        return al
    }


    void loadI2b2(ArrayList<HashMap<String, String>> concepts) {

        if (props.get("common.skip_i2b2").toString().toLowerCase().equals("yes")) {
            log.info "Skip loading data into i2b2 table ..."
        } else {
            PrinterUtil.printHashMapArray("7. Concept HashMap", concepts, "trace")

            log.info "Start loading concept(s) into i2b2 table ..."
            I2b2 i2b2 = new I2b2Factory().createI2b2(props)
            i2b2.insertI2b2(concepts)
            log.info "End loading concept(s) into i2b2 table ..."
        }
    }


    void loadI2b2Secure(ArrayList<HashMap<String, String>> concepts) {

        if (props.get("common.skip_i2b2_secure").toString().toLowerCase().equals("yes")) {
            log.info "Skip loading data into i2b2_secure table ..."
        } else {
            PrinterUtil.printHashMapArray("7. Concept HashMap", concepts, "trace")

            log.info "Start loading concept(s) into i2b2_secure table ..."
            I2b2Secure i2b2Secure = new I2b2SecureFactory().createI2b2Secure(props)
            i2b2Secure.insertI2b2Secure(concepts)
            log.info "End loading concept(s) into i2b2_secure table ..."
        }
    }


    void loadConceptDimension(ArrayList<HashMap<String, String>> concepts) {

        if (props.get("common.skip_concept_dimension").toString().toLowerCase().equals("yes")) {
            log.info "Skip loading data into concept_dimension table ..."
        } else {
            PrinterUtil.printHashMapArray("Loading Concept HashMap into concept_dimension table", concepts, "trace")

            log.info "Start loading concept(s) into concept_dimension table ..."
            ConceptDimension conceptDimension = new ConceptDimensionFactory().createConceptDimension(props)
            conceptDimension.insertConceptDimension(concepts)
            log.info "End loading concept(s) into concept_dimension table ..."
        }
    }

    void loadPatientDimension(ArrayList<HashMap<String, String>> patients) {

        if (props.get("common.skip_patient_dimension").toString().toLowerCase().equals("yes")) {
            log.info "Skip loading patient(s) into patient_dimension table ..."
        } else {
            PrinterUtil.printHashMapArray("Loading patient(s) into patient_dimension table", patients, "trace")

            log.info "Start loading patient(s) into patient_dimension table ..."
            PatientDimension patientDimension = new PatientDimensionFactory().createPatientDimension(props)
            patientDimension.insertPatientDimension(patients)
            log.info "End loading patient(s) into patient_dimension table ..."
        }
    }


    void loadObservationData(ArrayList<String> observationData) {

        if (props.get("common.skip_observation_fact").toString().toLowerCase().equals("yes")) {
            log.info "Skip loading observation(s) into observation_fact table ..."
        } else {
            PrinterUtil.printArrayList("Loading patient(s) into observation_fact table", observationData, "trace")

            log.info "Start loading observation(s) into observation_fact table ..."
            ObservationFact observationFact = new ObservationFactFactory().createObservationFact(props)
            observationFact.insertObservationFact(observationData)
            log.info "End loading observation(s) into observation_fact table ..."
        }
    }


    Map getSubjectPatientMap() {
        log.info "Start querying subject_id:patient_num mapping from  patient_dimension table ..."
        PatientDimension patientDimension = new PatientDimensionFactory().createPatientDimension(props)
        Map subjectPatientMap = patientDimension.getSubjectPatientMap()
        log.info "End querying subject_id:patient_num mapping from patient_dimension table ..."

        return subjectPatientMap
    }
}
