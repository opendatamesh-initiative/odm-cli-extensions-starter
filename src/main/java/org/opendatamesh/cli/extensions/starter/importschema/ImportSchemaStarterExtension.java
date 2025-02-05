package org.opendatamesh.cli.extensions.starter.importschema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaExtension;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaOptions;
import org.opendatamesh.dpds.model.core.StandardDefinitionDPDS;
import org.opendatamesh.dpds.model.definitions.DefinitionReferenceDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.dpds.model.interfaces.PromisesDPDS;
import org.opendatamesh.dpds.utils.ObjectMapperFactory;

import java.util.Map;
import java.util.UUID;

public class ImportSchemaStarterExtension implements ImportSchemaExtension {
    private final String SUPPORTED_FROM = "jdbc";
    private final String SUPPORTED_TO = "port";

    private final PersistenceInterface persistenceInterface;

    public ImportSchemaStarterExtension() {
        this.persistenceInterface = new PersistenceInterfaceFileSystemImpl();
    }

    ImportSchemaStarterExtension(PersistenceInterface persistenceInterface) {
        this.persistenceInterface = persistenceInterface;
    }

    @Override
    public boolean supports(String from, String to) {
        return SUPPORTED_FROM.equalsIgnoreCase(from) && SUPPORTED_TO.equalsIgnoreCase(to);
    }

    @Override
    public PortDPDS importElement(ImportSchemaOptions importSchemaOptions) {
        PortDPDS outputPort = initOutputPortFromOutParams(importSchemaOptions.getCommandCliOutputParameters());
        persistenceInterface.saveOutputPort(importSchemaOptions, outputPort);
        ObjectNode api = buildApiObjectNode(importSchemaOptions);
        persistenceInterface.saveOutputPortApi(importSchemaOptions, outputPort, api);
        return outputPort;

    }


    private ObjectNode buildApiObjectNode(ImportSchemaOptions importSchemaOptions) {
        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        ObjectNode api = mapper.createObjectNode();
        api.put("datastoreapi", "1.0.0");
        ObjectNode schema = mapper.createObjectNode();
        if (importSchemaOptions.getCommandCliOutputParameters().containsKey("database")) {
            schema.put("databaseName", importSchemaOptions.getCommandCliOutputParameters().get("database"));
        }
        if (importSchemaOptions.getCommandCliOutputParameters().containsKey("schema")) {
            schema.put("schemaName", importSchemaOptions.getCommandCliOutputParameters().get("schema"));
        }
        ArrayNode tables = mapper.createArrayNode();
        schema.set("tables", tables);
        api.set("schema", schema);
        return api;
    }

    PortDPDS initOutputPortFromOutParams(Map<String, String> outParams) {
        PortDPDS outputPort = new PortDPDS();
        outputPort.setName(outParams.get("name"));
        outputPort.setVersion(outParams.get("version") != null ? outParams.get("version") : "1.0.0");

        PromisesDPDS promises = new PromisesDPDS();
        outputPort.setPromises(promises);

        StandardDefinitionDPDS apiStdDef = new StandardDefinitionDPDS();
        promises.setApi(apiStdDef);
        apiStdDef.setSpecification("datastoreapi");
        apiStdDef.setSpecification("1.0.0");

        DefinitionReferenceDPDS definition = new DefinitionReferenceDPDS();
        apiStdDef.setDefinition(definition);
        definition.setMediaType("text/json");
        definition.setRef("api.json");

        if (outputPort.getName() == null || outputPort.getName().isEmpty()) {
            outputPort.setName(UUID.randomUUID().toString());
        }
        return outputPort;
    }

}
