package org.opendatamesh.cli.extensionstarter.importer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.cli.extensions.importer.ImporterArguments;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;

class PersistenceInterfaceMock implements PersistenceInterface {
    @Override
    public void saveOutputPort(ImporterArguments importSchemaOptions, PortDPDS outputPort) {

    }

    @Override
    public void saveOutputPortApi(ImporterArguments importSchemaOptions, PortDPDS outputPort, ObjectNode api) {

    }
}
