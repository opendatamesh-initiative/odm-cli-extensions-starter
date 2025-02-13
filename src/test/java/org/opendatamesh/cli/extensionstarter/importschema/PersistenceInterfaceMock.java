package org.opendatamesh.cli.extensionstarter.importschema;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.cli.extensions.importschema.ImportSchemaArguments;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;

class PersistenceInterfaceMock implements PersistenceInterface {
    @Override
    public void saveOutputPort(ImportSchemaArguments importSchemaOptions, PortDPDS outputPort) {

    }

    @Override
    public void saveOutputPortApi(ImportSchemaArguments importSchemaOptions, PortDPDS outputPort, ObjectNode api) {

    }
}
