package org.opendatamesh.cli.extensionstarter.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.cli.extensions.ExtensionInfo;
import org.opendatamesh.cli.extensions.ExtensionOption;
import org.opendatamesh.cli.extensions.importer.ImporterArguments;
import org.opendatamesh.cli.extensions.importer.ImporterExtension;
import org.opendatamesh.dpds.model.core.StandardDefinitionDPDS;
import org.opendatamesh.dpds.model.definitions.DefinitionReferenceDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.dpds.model.interfaces.PromisesDPDS;
import org.opendatamesh.dpds.utils.ObjectMapperFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ImporterStarterExtension implements ImporterExtension<PortDPDS> {
    private static final String SUPPORTED_FROM = "jdbc";
    private static final String SUPPORTED_TO = "output-port";
    private static final String OUTPUT_DIR = "ports/";
    private final Map<String, String> parameters = new HashMap<>();

    private final PersistenceInterface persistenceInterface;

    public ImporterStarterExtension() {
        this.persistenceInterface = new PersistenceInterfaceFileSystemImpl();
    }

    ImporterStarterExtension(PersistenceInterface persistenceInterface) {
        this.persistenceInterface = persistenceInterface;
    }

    @Override
    public boolean supports(String from, String to) {
        return SUPPORTED_FROM.equalsIgnoreCase(from) && SUPPORTED_TO.equalsIgnoreCase(to);
    }


    @Override
    public Class<PortDPDS> getTargetClass() {
        return PortDPDS.class;
    }

    @Override
    public List<ExtensionOption> getExtensionOptions() {
        ExtensionOption databaseName = new ExtensionOption.Builder()
                .names("--databaseName")
                .description("The name of the database")
                .required(true)
                .interactive(true)
                .defaultValueFromConfig("cli.env.custom")
                .setter(value -> parameters.put("databaseName", value))
                .getter(() -> parameters.get("databaseName"))
                .build();

        ExtensionOption schemaName = new ExtensionOption.Builder()
                .names("--schemaName")
                .description("The name of the schema")
                .required(true)
                .interactive(true)
                .setter(value -> parameters.put("schemaName", value))
                .getter(() -> parameters.get("schemaName"))
                .build();
        ExtensionOption name = new ExtensionOption.Builder()
                .names("--portName")
                .description("The name of the port")
                .required(true)
                .interactive(true)
                .setter(value -> parameters.put("portName", value))
                .getter(() -> parameters.get("portName"))
                .build();
        ExtensionOption version = new ExtensionOption.Builder()
                .names("--portVersion")
                .description("The version of the port")
                .required(true)
                .interactive(true)
                .setter(value -> parameters.put("portVersion", value))
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
    public PortDPDS importElement(PortDPDS portDPDS, ImporterArguments ImporterArguments) {
        PortDPDS outputPort = initOutputPortFromOutParams(ImporterArguments);
        persistenceInterface.saveOutputPort(ImporterArguments, outputPort);
        ObjectNode api = buildApiObjectNode();
        persistenceInterface.saveOutputPortApi(ImporterArguments, outputPort, api);
        return outputPort;
    }

    private ObjectNode buildApiObjectNode() {
        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        ObjectNode api = mapper.createObjectNode();
        api.put("datastoreapi", "1.0.0");
        ObjectNode schema = mapper.createObjectNode();
        if (parameters.containsKey("databaseName")) {
            schema.put("databaseName", parameters.get("databaseName"));
        }
        if (parameters.containsKey("schemaName")) {
            schema.put("schemaName", parameters.get("schemaName"));
        }
        ArrayNode tables = mapper.createArrayNode();
        schema.set("tables", tables);
        api.set("schema", schema);
        return api;
    }

    private PortDPDS initOutputPortFromOutParams(ImporterArguments ImporterArguments) {
        PortDPDS outputPort = new PortDPDS();
        outputPort.setName(parameters.get("portName"));
        outputPort.setVersion(parameters.get("portVersion") != null ? parameters.get("portVersion") : "1.0.0");
        String target = ImporterArguments.getParentCommandOptions().get("target");
        Path outputPortPath = Paths.get(OUTPUT_DIR, target, outputPort.getName(), "port.json");
        outputPort.setRef(outputPortPath.toString());

        PromisesDPDS promises = new PromisesDPDS();
        outputPort.setPromises(promises);

        StandardDefinitionDPDS apiStdDef = new StandardDefinitionDPDS();
        promises.setApi(apiStdDef);
        apiStdDef.setSpecification("datastoreapi");
        apiStdDef.setSpecificationVersion("1.0.0");

        DefinitionReferenceDPDS definition = new DefinitionReferenceDPDS();
        apiStdDef.setDefinition(definition);
        definition.setMediaType("text/json");
        Path definitionPath = Paths.get(OUTPUT_DIR, target, outputPort.getName(), "api.json");
        definition.setRef(definitionPath.toString());

        if (outputPort.getName() == null || outputPort.getName().isEmpty()) {
            outputPort.setName(UUID.randomUUID().toString());
        }
        return outputPort;
    }
}
