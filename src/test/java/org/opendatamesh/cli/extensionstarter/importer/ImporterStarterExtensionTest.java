package org.opendatamesh.cli.extensionstarter.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.opendatamesh.cli.extensions.importer.ImporterArguments;
import org.opendatamesh.cli.extensions.importer.ImporterExtension;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.dpds.utils.ObjectMapperFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


public class ImporterStarterExtensionTest {
    @Test
    public void testImportSchemaStarterExtension() throws IOException {
        ImporterExtension importSchemaExtension = new ImporterStarterExtension(new PersistenceInterfaceMock());
        String SUPPORTED_FROM = "jdbc";
        String SUPPORTED_TO = "output-port";
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

        ImporterArguments arguments = new ImporterArguments();
        arguments.setParentCommandOptions(Map.of("target", "output-port"));

        PortDPDS outputPort = (PortDPDS) importSchemaExtension.importElement(new PortDPDS(),arguments);

        String outputPortStringified = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(outputPort);
        String expectedOutputPortStringified = Files.readString(Path.of(getClass().getResource("test_import_schema_starter_extension_expected_output.json").getPath()));
        Assert.assertEquals(
                expectedOutputPortStringified.replaceAll("\\s+", ""),
                outputPortStringified.replaceAll("\\s+", "")
        );
    }
}
