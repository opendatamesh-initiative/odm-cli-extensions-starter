package org.opendatamesh.cli.extensionstarter.importer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.cli.extensions.importer.ImporterArguments;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;

interface PersistenceInterface {
    void saveOutputPort(ImporterArguments ImporterArguments, PortDPDS outputPort);

    void saveOutputPortApi(ImporterArguments ImporterArguments, PortDPDS outputPort, ObjectNode api);
}
