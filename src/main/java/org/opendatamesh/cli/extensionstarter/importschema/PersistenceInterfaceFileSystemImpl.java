package org.opendatamesh.cli.extensionstarter.importschema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaArguments;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class PersistenceInterfaceFileSystemImpl implements PersistenceInterface {

    private final ObjectMapper objectMapper;

    public PersistenceInterfaceFileSystemImpl() {
        this.objectMapper = new ObjectMapper();
        configObjectMapper(objectMapper);
    }

    @Override
    public void saveOutputPort(ImportSchemaArguments importSchemaArguments, PortDPDS outputPort) {
        try {
            File outputPortFile = new File(importSchemaArguments.getRootDescriptorPath().getParent().toFile(), outputPort.getRef());
            String portDefContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(outputPort);
            writeFile(outputPortFile.toPath(), portDefContent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveOutputPortApi(ImportSchemaArguments importSchemaArguments, PortDPDS outputPort, ObjectNode api) {
        try {
            String outputPortApiPath = outputPort.getPromises().getApi().getDefinition().getRef();
            File outputPortApiFile = new File(importSchemaArguments.getRootDescriptorPath().getParent().toFile(), outputPortApiPath);
            writeFile(
                    outputPortApiFile.toPath(),
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(api)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private void writeFile(Path filePath, String content) {
        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, content.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error writing file: " + filePath, e);
        }
    }

    private void configObjectMapper(ObjectMapper objectMapper) {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public Object findSerializer(Annotated am) {
                return null;
            }

            @Override
            public Object findDeserializer(Annotated am) {
                return null;
            }
        });
    }
}
