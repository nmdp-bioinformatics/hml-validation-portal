package org.nmdp.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.nmdp.miring.MiringReport;
import io.swagger.annotations.ApiParam;
import io.swagger.api.hml.HmlApi;
import org.nmdp.b2b.spec.ws.api101.ValidateHMLResponse;
import org.nmdp.hmlDataModels.Hml;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.xml.transform.StringSource;

import org.nmdp.utils.TypingInfo;
import javax.xml.bind.JAXBContext;
import java.net.URI;


@RestController
@RequestMapping("/hml")
@CrossOrigin
public class HmlInputController implements HmlApi
{
    @RequestMapping(path = "/validation", headers="Accept=application/xml",  consumes = MediaType.APPLICATION_XML_VALUE, produces   = MediaType.APPLICATION_OCTET_STREAM_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> validate(String xml, Boolean hmlgateway, Boolean miring, Boolean glstringSanity, Boolean glstringValid)
    {
        RestTemplate restTemplate = new RestTemplate();
        StringBuilder aSb = new StringBuilder();
        try {

            if (hmlgateway.booleanValue())
            {
                String resourceUrl = "https://api.nmdp.org/hml_gw/v1/validate";
                RequestEntity<String> request =
                        RequestEntity.post(new URI(resourceUrl))
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                                //  .header("Accept", "application/xml")
                                .body(xml);
                ResponseEntity<String> aResponse = restTemplate.exchange(request, String.class);
//                ResponseEntity<ValidateHMLResponse> response = restTemplate.exchange(request, ValidateHMLResponse.class);
                JAXBContext context = JAXBContext
                        .newInstance("org.nmdp.b2b.spec.ws.api101");
                ValidateHMLResponse aValidateResponse = context
                        .createUnmarshaller()
                        .unmarshal(new StringSource(aResponse.getBody()), ValidateHMLResponse.class)
                        .getValue();
//                return new ResponseEntity<>(response.getBody().getMessage(), HttpStatus.OK );
                aSb.append("Hml Validation result - " + aValidateResponse.getMessage());
//                return new ResponseEntity<>(aValidateResponse.getMessage(), HttpStatus.OK );
            }
            if (miring.booleanValue())
            {
                String resourceUrl = "http://miring.b12x.org/validator/ValidateMiring/";
                RequestEntity<String> request =
                        RequestEntity.post(new URI(resourceUrl))
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                                .header("--data-urlencode")
                                //  .header("Accept", "application/xml")
                                .body("xml="+xml);
                ResponseEntity<String> response = restTemplate.exchange(request, String.class);
                JAXBContext context = JAXBContext
                        .newInstance("org.nmdp.miring");
                MiringReport aMiringReport = context
                        .createUnmarshaller()
                        .unmarshal(new StringSource(response.getBody()), MiringReport.class)
                        .getValue();
                aSb.append("\n\nMiring Validation result - " + aMiringReport.getMiringCompliant());
            }
            if (glstringValid.booleanValue())
            {
                XmlMapper xmlMapper = new XmlMapper();
                TypingInfo aTypingInfo = xmlMapper.readValue(xml, TypingInfo.class);
                String resourceUrl = "https://hmldev.nmdp.org/mac/api/encode";
                //TODO: The temp values here will be updated in the future with an option for User to input this information
                String trialRun = "false";
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(resourceUrl)
                        .queryParam("trialRun",trialRun)
                        .queryParam("email", aTypingInfo.getEmail())
                        .queryParam("imgtHlaRelease", aTypingInfo.getVersion());

                RequestEntity<String> request =
                        RequestEntity.post(new URI(uriBuilder.toUriString()))
                                .header("--data-raw")
                                .body(aTypingInfo.getGlstring());
                ResponseEntity<String> response = restTemplate.exchange(request, String.class);
                boolean isValidGlString = response.getBody().contains("invalid") ? false : true;
                aSb.append("\n\nGlString validity - " + (isValidGlString ? "glstring is valid" : "glstring is invalid"));
            }

            if (!hmlgateway.booleanValue() && !glstringSanity.booleanValue() && !miring.booleanValue() && !glstringValid.booleanValue())
            {
                return new ResponseEntity<>("No Validator Selected", HttpStatus.NOT_IMPLEMENTED);
            }
            return new ResponseEntity<>(aSb.toString(), HttpStatus.OK);
        } catch (Exception e)
        {
            return new ResponseEntity<>("ERRORS ENCOUNTERED", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
