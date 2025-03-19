package org.opendatamesh.cli.extensionstarter.importer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.cli.extensions.ExtensionInfo;
import org.opendatamesh.cli.extensions.ExtensionOption;
import org.opendatamesh.cli.extensions.importer.ImporterArguments;
import org.opendatamesh.cli.extensions.importer.ImporterExtension;
import org.opendatamesh.dpds.model.core.ComponentBase;
import org.opendatamesh.dpds.model.core.StandardDefinition;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.dpds.model.interfaces.Promises;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ImporterStarterExtension implements ImporterExtension<Port> {
    private static final String SUPPORTED_FROM = "starter";
    private static final String SUPPORTED_TO = "port";
    private static final String OUTPUT_DIR = "ports/";
    private final Map<String, String> parameters = new HashMap<>();


    public ImporterStarterExtension() {
    }

    @Override
    public boolean supports(String from, String to) {
        return SUPPORTED_FROM.equalsIgnoreCase(from) &&
                //Supporting all types of ports
                to.toLowerCase().endsWith(SUPPORTED_TO);
    }


    @Override
    public Class<Port> getTargetClass() {
        //What is the class type of the entity being imported.
        return Port.class;
    }

    @Override
    public List<ExtensionOption> getExtensionOptions() {
        //The additional command options required by this extension implementation
        ExtensionOption databaseName = new ExtensionOption.Builder()
                .names("--databaseName")
                .description("The name of the database")
                .required(true)
                .interactive(true)
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

        return List.of(databaseName, schemaName);
    }

    @Override
    public ExtensionInfo getExtensionInfo() {
        //The metadata information of this extension implementation
        //This includes a short description of the extension
        return new ExtensionInfo.Builder()
                .description("Extension to import a simple port")
                .build();
    }


    @Override
    public Port importElement(Port port, ImporterArguments importerArguments) {
        //The logic to import the element
        Port outputPort = initPort(port, importerArguments);
        ObjectNode schema = buildPortSchema();
        try {
            outputPort.getPromises().getApi().setDefinition(new ObjectMapper().treeToValue(schema, ComponentBase.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return outputPort;
    }

    private Port initPort(Port port, ImporterArguments importerArguments) {
        if (port == null) {
            port = new Port();
        }
        if (port.getName() == null) {
            String target = importerArguments.getParentCommandOptions().get("target");
            port.setName(target);
            if (target == null) {
                port.setName(UUID.randomUUID().toString());
            }
        }
        if (port.getVersion() == null) {
            port.setVersion("1.0.0");
        }

        //Setting the reference of the port.
        //In the cli, if the save-format is set to NORMALIZED, all the content of the port will
        //be saved in a file located at this path
        String toOption = importerArguments.getParentCommandOptions().get("to");
        Path outputPortPath = Paths.get(OUTPUT_DIR, toOption, port.getName(), "port.json");
        port.setRef(outputPortPath.toString());

        Promises promises = new Promises();
        port.setPromises(promises);
        StandardDefinition apiStdDef = new StandardDefinition();
        promises.setApi(apiStdDef);
        apiStdDef.setSpecification("datastoreapi");
        apiStdDef.setSpecificationVersion("1.0.0");

        return port;
    }

    private ObjectNode buildPortSchema() {
        //Building the schema of the port
        //following the DatastoreApi specification

        //The schema can either be a Json Object
        //or can be a DefinitionReference object pointing
        //to an existing file.
        ObjectMapper mapper = new ObjectMapper();
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
}
