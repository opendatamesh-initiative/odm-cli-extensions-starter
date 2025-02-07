package org.opendatamesh.cli.extensions.starter.importschema;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaArguments;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;

interface PersistenceInterface {
    void saveOutputPort(ImportSchemaArguments importSchemaArguments, PortDPDS outputPort);

    void saveOutputPortApi(ImportSchemaArguments importSchemaArguments, PortDPDS outputPort, ObjectNode api);
}
