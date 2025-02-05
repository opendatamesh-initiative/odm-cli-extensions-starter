package org.opendatamesh.cli.extensions.starter.importschema;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaOptions;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;

interface PersistenceInterface {
    void saveOutputPort(ImportSchemaOptions importSchemaOptions, PortDPDS outputPort);

    void saveOutputPortApi(ImportSchemaOptions importSchemaOptions, PortDPDS outputPort, ObjectNode api);
}
