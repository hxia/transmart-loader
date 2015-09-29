package com.recomdata.pipeline.transmart.loader

import com.recomdata.pipeline.transmart.biomarker.BioMarker
import com.recomdata.pipeline.transmart.biomarker.BioMarkerFactory
import com.recomdata.pipeline.transmart.i2b2.I2b2
import com.recomdata.pipeline.transmart.i2b2.I2b2Factory
import com.recomdata.pipeline.util.Util
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator

class TransmartLoader {

    private static final Logger log = Logger.getLogger(TransmartLoader)

    static main(args) {

        PropertyConfigurator.configure("conf/log4j.properties");
        log.info("Current Root Path: " + System.getProperty("user.dir"))

        // load the default property file: conf/transmart.properties
        Properties props = Util.loadConfiguration()

        TransmartLoader tl = new TransmartLoader()
//        tl.loadBioMarker(props)
        tl.loadI2b2(props)
    }


    void loadI2b2(Properties props) {


        if (props.get("common.skip_i2b2").toString().toLowerCase().equals("yes")) {
            log.info "Skip loading data into i2b2 table ..."
        } else {
            I2b2 bm = new I2b2Factory().createI2b2(props)
        }
//        bm.setProperties(props)

    }


    void loadBioMarker(Properties props) {
        BioMarker bm = new BioMarkerFactory().createBioMarker(props)
        bm.setProperties(props)

        File entrezGene = new File(props.get("entrez.gene_info_source") + ".tsv")

        StringBuffer sb = new StringBuffer()
        log.info(entrezGene.toString() + ":" + entrezGene.size())
        entrezGene.eachLine {
            String[] str = it.split("\t")
            sb.append(str[1] + "\t" + str[2] + "\t" + str[3] + "\n")
        }

        bm.insertGenes(sb)
    }
}
