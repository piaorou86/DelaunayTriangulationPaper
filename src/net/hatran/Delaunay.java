package net.hatran;

import org.apache.log4j.Logger;

public class Delaunay {

    private final static Logger logger = Logger.getLogger(Delaunay.class);  // use log4j to save output message

    public static void main(String[] args) {

        logger.info("Star processing...");

        GCanvas cvas = new GCanvas();

        cvas.processData();

    }
}

