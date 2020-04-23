package org.nmdp.controller;

import org.apache.cxf.common.util.StringUtils;
import org.nmdp.b2b.spec.ws.api101.Allele;
import org.nmdp.mac.client.jaxrs.JaxrsAlleleCodeService;
import org.nmdp.mac.client.model.AlleleCodeService;
import org.nmdp.miring.MiringReport;
import io.swagger.annotations.ApiParam;
import io.swagger.api.hml.HmlApi;
import org.nmdp.b2b.spec.ws.api101.ValidateHMLResponse;
import org.nmdp.hmlDataModels.Hml;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.xml.transform.StringSource;

import javax.validation.Valid;
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
//                aSb.append("\n\nMiring Errors - " + aMiringReport.getMiringValidatio)
//                return new ResponseEntity<>(response.getBody(), HttpStatus.OK );
            }
            if (glstringValid.booleanValue())
            {
                String resourceUrl = "https://hmldev.nmdp.org/mac/";
                AlleleCodeService aMac = new JaxrsAlleleCodeService(resourceUrl);
                String aImgtHlaRelease = "3.29.0";
                RequestEntity<String> request =
                        RequestEntity.post(new URI(resourceUrl))
                                .header("--data-urlencode")
                                //  .header("Accept", "application/xml")
                                .body("xml="+xml);
                boolean isValidGlString = isValidGlString(xml,aMac,aImgtHlaRelease);

                aSb.append("\n\nGlString validity - " + (isValidGlString ? "glstring is valid" : "glstring is invalid"));
//                return new ResponseEntity<>(isValidGlString? "", HttpStatus.OK );
            }

            if (!hmlgateway && !glstringSanity && !miring && !glstringValid)
            {
                return new ResponseEntity<>("No Validator Selected", HttpStatus.NOT_IMPLEMENTED);
            }
            return new ResponseEntity<>(aSb.toString(), HttpStatus.OK);
        } catch (Exception e)
        {
            return new ResponseEntity<>("ERRORS ENCOUNTERED", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidGlString(String theGlString, AlleleCodeService theService, String theVersion)
    {
        return !StringUtils.isEmpty(testEncode(theGlString, theService, theVersion));
    }

    private String testEncode(String glstring, AlleleCodeService theService, String theVersion) {
        String myMacEncodedString = "";
        try {
            myMacEncodedString = theService.encode(theVersion, glstring);

        } catch (IllegalArgumentException e) {
            // Notify user to correct input values.
            e.printStackTrace();
        } catch (RuntimeException e) {
            // System or network issue.
            e.printStackTrace();
        }
        return myMacEncodedString;
    }
}
