package org.opendatamesh.cli.extensionstarter.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.opendatamesh.cli.extensions.importer.ImporterArguments;
import org.opendatamesh.cli.extensions.importer.ImporterExtension;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class ImporterStarterExtensionTest {
    @Test
    public void testImportSchemaStarterExtension() throws IOException {
        //Checking that the Extension Implementation actually supports the expected from and to.
        ImporterExtension<PortDPDS> importSchemaExtension = new ImporterStarterExtension();
        String SUPPORTED_FROM = "starter";
        String SUPPORTED_TO = "output-port";
        assertThat(importSchemaExtension.supports(SUPPORTED_FROM, SUPPORTED_TO)).isTrue();

        //Mocking the Extension Options.
        Map<String, String> internalParameters = Map.of(
                "databaseName", "demo_db",
                "schemaName", "demo_schema"
        );
        importSchemaExtension.getExtensionOptions()
                .forEach(extensionOption -> {
                    String key = extensionOption.getNames().stream().findFirst().get().replace("-", "");
                    extensionOption.getSetter().accept(internalParameters.get(key));
                });

        //Mocking the parent commands options (in this case only the import options).
        ImporterArguments arguments = new ImporterArguments();
        arguments.setParentCommandOptions(Map.of("to", "output-port", "target", "port_name"));

        //Executing the logic.
        PortDPDS outputPort = importSchemaExtension.importElement(new PortDPDS(), arguments);

        //Checking that the output is like the expected one.
        PortDPDS expectedOutputPort = new ObjectMapper().readValue(getClass().getResource("test_import_schema_starter_extension_expected_output.json"), PortDPDS.class);
        assertThat(outputPort).usingRecursiveComparison()
                .ignoringFields("promises.api.definition")
                .isEqualTo(expectedOutputPort);
    }
}
