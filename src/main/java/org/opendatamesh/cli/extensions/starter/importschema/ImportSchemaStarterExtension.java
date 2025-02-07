package org.opendatamesh.cli.extensions.starter.importschema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.cli.extensions.ExtensionInfo;
import org.opendatamesh.cli.extensions.ExtensionOption;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaArguments;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaExtension;
import org.opendatamesh.dpds.model.core.StandardDefinitionDPDS;
import org.opendatamesh.dpds.model.definitions.DefinitionReferenceDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.dpds.model.interfaces.PromisesDPDS;
import org.opendatamesh.dpds.utils.ObjectMapperFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ImportSchemaStarterExtension implements ImportSchemaExtension {
    private final String SUPPORTED_FROM = "jdbc";
    private final String SUPPORTED_TO = "port";
    private Map<String, String> parameters = new HashMap<>();

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
    public List<ExtensionOption> getExtensionOptions() {
        ExtensionOption databaseName = new ExtensionOption.Builder()
                .names("--databaseName")
                .description("The name of the database")
                .required(true)
                .interactive(true)
                .setter((value) -> {
                    parameters.put("databaseName", value);
                })
                .getter(() -> parameters.get("databaseName"))
                .build();

        ExtensionOption schemaName = new ExtensionOption.Builder()
                .names("--schemaName")
                .description("The name of the schema")
                .required(true)
                .interactive(true)
                .setter((value) -> {
                    parameters.put("schemaName", value);
                })
                .getter(() -> parameters.get("schemaName"))
                .build();
        ExtensionOption name = new ExtensionOption.Builder()
                .names("--portName")
                .description("The name of the port")
                .required(true)
                .interactive(true)
                .setter((value) -> {
                    parameters.put("portName", value);
                })
                .getter(() -> parameters.get("portName"))
                .build();
        ExtensionOption version = new ExtensionOption.Builder()
                .names("--portVersion")
                .description("The version of the port")
                .required(true)
                .interactive(true)
                .setter((value) -> {
                    parameters.put("portVersion", value);
                })
                .getter(() -> parameters.get("portVersion"))
                .build();
        return List.of(databaseName, schemaName, name, version);
    }

    @Override
    public ExtensionInfo getExtensionInfo() {
        return new ExtensionInfo.Builder()
                .description("Extension to import a simple output port")
                .build();
    }


    @Override
    public PortDPDS importElement(ImportSchemaArguments importSchemaArguments) {
        PortDPDS outputPort = initOutputPortFromOutParams();
        persistenceInterface.saveOutputPort(importSchemaArguments, outputPort);
        ObjectNode api = buildApiObjectNode(importSchemaArguments);
        persistenceInterface.saveOutputPortApi(importSchemaArguments, outputPort, api);
        return outputPort;
    }

    private ObjectNode buildApiObjectNode(ImportSchemaArguments importSchemaArguments) {
        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        ObjectNode api = mapper.createObjectNode();
        api.put("datastoreapi", "1.0.0");
        ObjectNode schema = mapper.createObjectNode();
        if (parameters.containsKey("databaseName")) {
            schema.put("databaseName", parameters.get("database"));
        }
        if (parameters.containsKey("schemaName")) {
            schema.put("schemaName", parameters.get("schema"));
        }
        ArrayNode tables = mapper.createArrayNode();
        schema.set("tables", tables);
        api.set("schema", schema);
        return api;
    }

    private PortDPDS initOutputPortFromOutParams() {
        PortDPDS outputPort = new PortDPDS();
        outputPort.setName(parameters.get("portName"));
        outputPort.setVersion(parameters.get("portVersion") != null ? parameters.get("portVersion") : "1.0.0");

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
