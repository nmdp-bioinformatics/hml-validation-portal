import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.nmdp.hmlDataModels.*;
import org.springframework.xml.transform.StringSource;

import javax.xml.bind.JAXBContext;
import java.io.File;
import java.util.Scanner;


public class HelloWorld
{
    public static void main(String[] args)
    {

        try {
//            XMLStreamReader xsr = XMLInputFactory.newFactory().createXMLStreamReader(new FileInputStream("src/main/resources/sample.hml"));
//            xsr.next();
//
            JacksonXmlModule aXmlModule = new JacksonXmlModule();
            aXmlModule.setDefaultUseWrapper(false);
            ObjectMapper xmlMapper = new XmlMapper(aXmlModule);
            String aFileContent = (new Scanner(new File("src/main/resources/sample.hml"))).useDelimiter("\\Z").next();
//            XmlMapper xmlMapper = new XmlMapper(
//                    new XmlFactory(new com.fasterxml.aalto.stax.InputFactoryImpl(), new com.fasterxml.aalto.stax.OutputFactoryImpl()), aXmlModule);
            xmlMapper.registerModule(new JaxbAnnotationModule());

            xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//            String aFileContent = new String(Files.readAllBytes(Paths.get("src/main/resources/sample.hml")), StandardCharsets.UTF_8);
            Hml aHml = xmlMapper.readValue(aFileContent, Hml.class);
            Sample sample = aHml.getSample().get(0);
            System.out.println("\nSampleid = " + sample.getId());

//            String aFileContent = (new Scanner(new File("src/main/resources/sample.hml"))).useDelimiter("\\Z").next();
            JAXBContext context = JAXBContext
                    .newInstance("org.nmdp.hmlDataModels");
            Hml hml = context
                    .createUnmarshaller()
                    .unmarshal(new StringSource(aFileContent), Hml.class)
                    .getValue();
            Sample aSample = hml.getSample().get(0);
            System.out.println("\nSampleid = " + aSample.getId());
        } catch (Exception e)
        {
            System.out.print("Exception in xml mapping");
        }

    }
}


