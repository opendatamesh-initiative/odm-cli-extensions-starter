package org.opendatamesh.cli.extensions.starter.importschema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaOptions;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.dpds.utils.ObjectMapperFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class PersistenceInterfaceFileSystemImpl implements PersistenceInterface {
    private static final String OUTPUT_DIR = "ports/output/";

    @Override
    public void saveOutputPort(ImportSchemaOptions importSchemaOptions, PortDPDS outputPort) {
        try {
            ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
            String outputPortPath = OUTPUT_DIR + outputPort.getName() + "/port.json";
            outputPort.setRef(outputPortPath);
            outputPort.setRawContent(" {\"$ref\": \"" + outputPortPath + "\"}");
            File outputPortFile = new File(importSchemaOptions.getRootDescriptorPath().getParent().toFile(), outputPortPath);
            String portDefContent = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(outputPort);
            writeFile(outputPortFile.toPath(), portDefContent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveOutputPortApi(ImportSchemaOptions importSchemaOptions, PortDPDS outputPort, ObjectNode api) {
        try {
            ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
            String outputPortApiPath = OUTPUT_DIR + outputPort.getName() + "/api.json";
            File outputPortApiFile = new File(importSchemaOptions.getRootDescriptorPath().getParent().toFile(), outputPortApiPath);
            writeFile(
                    outputPortApiFile.toPath(),
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(api)
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
}
