package org.opendatamesh.cli.extensions.starter.importschema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaExtension;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaOptions;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.dpds.utils.ObjectMapperFactory;

import java.io.IOException;
import java.util.Map;


public class ImportSchemaStarterExtensionTest {
    @Test
    public void testImportSchemaStarterExtension() throws IOException {
        ImportSchemaExtension importSchemaExtension = new ImportSchemaStarterExtension(new PersistenceInterfaceMock());
        String SUPPORTED_FROM = "jdbc";
        String SUPPORTED_TO = "port";
        Assert.assertTrue(importSchemaExtension.supports(SUPPORTED_FROM, SUPPORTED_TO));

        ImportSchemaOptions importSchemaOptions = new ImportSchemaOptions();
        importSchemaOptions.setCommandCliOutputParameters(
                Map.of(
                        "name", "port_name",
                        "version", "1.0.0",
                        "database", "demo_db",
                        "schema", "demo_schema"
                )
        );

        PortDPDS outputPort = importSchemaExtension.importElement(importSchemaOptions);
        PortDPDS expectedOutputPort = new ObjectMapper().readValue(
                Resources.toByteArray(
                        getClass().getResource("test_import_schema_starter_extension_expected_output.json")
                ),
                PortDPDS.class
        );

        String outputPortStringified = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(outputPort);
        String expectedOutputPortStringified = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(expectedOutputPort);
        Assert.assertEquals(expectedOutputPortStringified, outputPortStringified);
    }
}
