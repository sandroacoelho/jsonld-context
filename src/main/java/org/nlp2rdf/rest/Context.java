package org.nlp2rdf.rest;

import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;
import org.nlp2rdf.ContextJSONLD;
import org.nlp2rdf.NIFFormat;
import org.nlp2rdf.nif21.impl.NIF21;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@RestController
public class Context {

    @ResponseBody
    @CrossOrigin
    @RequestMapping(value = "/api/v1", method = RequestMethod.GET)
    public String getContext(@RequestParam(name = "ontology") String[] ontology,
                             @RequestParam(name = "template", required = false) String template,
                             @RequestParam(name = "language", required = false, defaultValue ="en") String language) {

        Preconditions.checkNotNull(ontology);

        ContextJSONLD context = new NIF21();
        Set<String> ontologies = new HashSet<>();


        for (int i=0; i < ontology.length; i++) {
            ontologies.add(ontology[i]);
        }

        String templateFileName = getTemplate(template);

        if (!templateFileName.isEmpty()) {
            return context.getContextForJSONLD(ontologies, templateFileName, language);
        }

        return context.getContextForJSONLD(ontologies, language);

    }

    private String getTemplate(String template) {
        if (template != null && !template.isEmpty()) {
            try {
                String result = IOUtils.toString(new URL(template), Charset.defaultCharset());
                String fileName = template.replace("http://", "").replace("/", "").replace(":", "").replace("-","").replace(".","");
                fileName = String.format(NIFFormat.TEMPLATE_ROOT.concat("%s"), fileName);
                Files.write(Paths.get(fileName), result.getBytes());
                return fileName;
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

}
