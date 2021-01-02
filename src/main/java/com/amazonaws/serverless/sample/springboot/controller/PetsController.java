/*
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.amazonaws.serverless.sample.springboot.controller;

import com.amazonaws.serverless.sample.springboot.model.Pet;
import com.amazonaws.serverless.sample.springboot.model.PetData;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@RestController
@EnableWebMvc
public class PetsController {
    private static final String EXAMPLE_JSON_REQUEST = "{\n"
        + "  \"bucketName\": \"rewrite-indesign-bucket\",\n"
        + "  \"destinationBucket\": \"rewrite-indesign-converted-bucket\",\n"
        + "  \"objectKey\": \"indesigntest-xsrv-Mac_noerr.indd\",\n"
        + "  \"archiveFlag\": \"0\",\n"
        + "  \"artFolder\": \"\",\n"
        + "  \"lambda\": \"N\",\n"
        + "  \"conversionType\": \"EPS\",\n"
        + "  \"conversionOptions\": {\n"
        + "    \"BLEEDTOP\":\"30\",\n"
        + "    \"BLEEDBOTTOM\":\"30\",\n"
        + "    \"BLEEDINSIDE\":\"30\",\n"
        + "    \"BLEEDOUTSIDE\":\"30\",\n"
        + "    \"POSTSCRIPTLEVEL\":\"level3\",\n"
        + "    \"FONTEMBEDDING\":\"subset\",\n"
        + "    \"DATAFORMAT\":\"binary\",\n"
        + "    \"EPSSPREADS\":\"false\",\n"
        + "    \"PREVIEW\":\"none\",\n"
        + "    \"COLORSPACE\":\"gray\",\n"
        + "    \"PAGERANGE\":\"1\"\n"
        + "    }\n"
        + "}";
    @RequestMapping(path = "/pets", method = RequestMethod.POST)
    public Pet createPet(@RequestBody Pet newPet) {
        if (newPet.getName() == null || newPet.getBreed() == null) {
            return null;
        }

        Pet dbPet = newPet;
        dbPet.setId(UUID.randomUUID().toString());
        return dbPet;
    }

    @RequestMapping(path = "/pets", method = RequestMethod.GET)
    public Pet[] listPets(@RequestParam("limit") Optional<Integer> limit, Principal principal) {
        int queryLimit = 10;
        if (limit.isPresent()) {
            queryLimit = limit.get();
        }

        Pet[] outputPets = new Pet[queryLimit];

        for (int i = 0; i < queryLimit; i++) {
            Pet newPet = new Pet();
            newPet.setId(UUID.randomUUID().toString());
            newPet.setName(PetData.getRandomName());
            newPet.setBreed(PetData.getRandomBreed());
            newPet.setDateOfBirth(PetData.getRandomDoB());
            outputPets[i] = newPet;
        }
        String responseApi = callService();
        System.out.println(responseApi);
        return outputPets;
    }

    @RequestMapping(path = "/pets/{petId}", method = RequestMethod.GET)
    public Pet listPets() {
        Pet newPet = new Pet();
        newPet.setId(UUID.randomUUID().toString());
        newPet.setBreed(PetData.getRandomBreed());
        newPet.setDateOfBirth(PetData.getRandomDoB());
        newPet.setName(PetData.getRandomName());
        return newPet;
    }

    private static String callService() {
        List<Object> providers = new ArrayList<>();
        providers.add(new JacksonJsonProvider());

        WebClient client = WebClient
            .create("http://10.187.153.31:8080/convert", providers);

        client = client.accept("application/json").type("application/json");

        Response response = client.post(EXAMPLE_JSON_REQUEST);
        return response.readEntity(String.class);
    }
}
