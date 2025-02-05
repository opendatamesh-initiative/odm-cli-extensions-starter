package org.opendatamesh.cli.extensions.starter.importschema;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaOptions;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;

class PersistenceInterfaceMock implements PersistenceInterface {
    @Override
    public void saveOutputPort(ImportSchemaOptions importSchemaOptions, PortDPDS outputPort) {

    }

    @Override
    public void saveOutputPortApi(ImportSchemaOptions importSchemaOptions, PortDPDS outputPort, ObjectNode api) {

    }
}
