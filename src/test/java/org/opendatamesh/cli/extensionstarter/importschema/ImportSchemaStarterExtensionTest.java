package org.opendatamesh.cli.extensionstarter.importschema;

import org.junit.Assert;
import org.junit.Test;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaArguments;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaExtension;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.dpds.utils.ObjectMapperFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


public class ImportSchemaStarterExtensionTest {
    @Test
    public void testImportSchemaStarterExtension() throws IOException {
        ImportSchemaExtension importSchemaExtension = new ImportSchemaStarterExtension();
        String SUPPORTED_FROM = "jdbc";
        String SUPPORTED_TO = "port";
        Assert.assertTrue(importSchemaExtension.supports(SUPPORTED_FROM, SUPPORTED_TO));

        Map<String, String> internalParameters = Map.of(
                "portName", "port_name",
                "portVersion", "1.0.0",
                "databaseName", "demo_db",
                "schemaName", "demo_schema"
        );

        importSchemaExtension.getExtensionOptions()
                .forEach(extensionOption -> {
                    String key = extensionOption.getNames().stream().findFirst().get().replace("-", "");
                    extensionOption.getSetter().accept(internalParameters.get(key));
                });

        ImportSchemaArguments arguments = new ImportSchemaArguments();
        arguments.setParentCommandOptions(Map.of("target", "output-port"));

        PortDPDS outputPort = importSchemaExtension.importElement(arguments);

        String outputPortStringified = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(outputPort);
        String expectedOutputPortStringified = Files.readString(Path.of(getClass().getResource("test_import_schema_starter_extension_expected_output.json").getPath()));
        Assert.assertEquals(
                expectedOutputPortStringified.replaceAll("\\s+", ""),
                outputPortStringified.replaceAll("\\s+", "")
        );
    }
}
