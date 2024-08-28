package com.ericsson.nms.rv.taf.test.arne.operator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;

import com.ericsson.cifwk.taf.utils.FileFinder;
import com.ericsson.cifwk.taf.utils.csv.CsvWriter;
import com.ericsson.nms.rv.taf.test.arne.element.*;
import com.ericsson.nms.rv.taf.test.netsim.NetsimOperator;

/**
 * Operator to parse ARNE Xml file.
 *
 * Created by ewandaf on 17/07/14.
 */
public class ArneOperator {

    private static final Logger logger = LoggerFactory
            .getLogger(ArneOperator.class);
    private Model model;
    private final String arne_xml_file;
    private final String dest_file;

    /**
     *
     * @param arne_xml_file
     *            ARNE xml file to be parsed
     * @param dest_file
     *            The generated file containing node info
     */
    public ArneOperator(final String arne_xml_file, final String dest_file) {
        this.dest_file = dest_file;
        this.arne_xml_file = arne_xml_file;
        initializeJAXB();
    }

    private void initializeJAXB() {
        try {
            final JAXBContext context = JAXBContext.newInstance(Model.class);
            final SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature(
                    "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                    false);
            spf.setFeature("http://xml.org/sax/features/validation", false);
            final XMLReader xmlReader = spf.newSAXParser().getXMLReader();
            final List<String> files = FileFinder.findFile(arne_xml_file);
            if (files.isEmpty()) {
                throw new FileNotFoundException();
            }
            final String file = files.get(0);
            final InputSource inputSource = new InputSource(
                    new FileReader(file));
            final SAXSource source = new SAXSource(xmlReader, inputSource);
            final Unmarshaller um = context.createUnmarshaller();
            model = (Model) um.unmarshal(source);
        } catch (final JAXBException e) {
            e.printStackTrace();
        } catch (final SAXNotSupportedException e) {
            e.printStackTrace();
        } catch (final SAXNotRecognizedException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            logger.error("SAXB exception.");
            e.printStackTrace();
        } catch (final FileNotFoundException e) {
            logger.error("Couldn't find ARNE file: " + arne_xml_file + ".");
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * If you need update the headers of generated file, update headers array in
     * this method and line array in WriteToCsv method
     */
    public void generateNodeCsv() {
        final String filePath = "target" + File.separator + this.dest_file;
        logger.info("Generating CSV file: {}", filePath);
        final List<ManagedElement> managedElements = getManagedElemnetList();
        final CsvWriter csvWriter = new CsvWriter(filePath);
        csvWriter.remove();
        final String[] headers = new String[] { "ManagedElementId",
                "ENodeBFunctionModelVersion", "OssModelIdentity",
                "ERBSConnectivityInfoModelVersion", "ipAddress", "simulation",
                "netsim", "SecureUserName", "SecureUserPassword",
                "NormalUserName", "NormalUserPassword" };
        csvWriter.write(headers);
        WriteToCsv(csvWriter, managedElements);
    }

    public List<ManagedElement> getManagedElemnetList() {
        final List<Object> managedElmentOrSubNetwork = model.getCreateList()
                .getManagedElementOrSubNetwork();
        final List<ManagedElementXml> managedElementXmlList = new ArrayList<>();
        // The children elements of Create element could be either SubNetwork or
        // ManagedElement
        for (final Object o : managedElmentOrSubNetwork) {
            if (o instanceof ManagedElementXml) {
                managedElementXmlList.add((ManagedElementXml) o);
            } else if (o instanceof SubNetwork) {
                final List<ManagedElementXml> managedElementXmls = ((SubNetwork) o)
                        .getManagedElementList();
                managedElementXmlList.addAll(managedElementXmls);
            }
        }
        final List<ManagedElement> toReturn = new ArrayList<ManagedElement>(
                managedElementXmlList.size());
        for (final ManagedElementXml managedElementXml : managedElementXmlList) {
            final ManagedElement managedElement = new ManagedElement(
                    managedElementXml);
            managedElement.setSimulation(NetsimOperator
                    .getSimulation(arne_xml_file));
            managedElement.setNetsimHost(NetsimOperator
                    .getNetsim(arne_xml_file));
            toReturn.add(managedElement);
        }
        return toReturn;
    }

    /**
     * If you need update the headers of the generated CSV file, update the line
     * array in this method and the line headers in the generateNodeCsv method
     */
    public static void WriteToCsv(final CsvWriter csvWriter,
            final List<ManagedElement> managedElements) {
        for (final ManagedElement managedElement : managedElements) {
            final String[] line = new String[] {
                    managedElement.getManagedElementId(),
                    managedElement.getEnodeBFunctionModelVersion(),
                    managedElement.getOssModelIdentity(),
                    managedElement.getErbsConnectivityInfoModelVersion(),
                    managedElement.getIpAddress(),
                    managedElement.getSimulation(),
                    managedElement.getNetsimHost(),
                    managedElement.getSecureUserName(),
                    managedElement.getSecureUserPassword(),
                    managedElement.getNormalUserName(),
                    managedElement.getNormalUserPassword() };
            csvWriter.write(line);
        }
    }
}
