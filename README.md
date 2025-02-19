<!-- TOC -->

* [Open Data Mesh Command Line Extensions Starter Project](#open-data-mesh-command-line-extensions-starter-project)
    * [About](#about)
    * [Getting Started](#getting-started)
    * [Project Configuration](#project-configuration)
        * [GitHub Workflows](#github-workflows)
        * [pom.xml](#pomxml)
        * [src/main/resources/META-INF/services](#srcmainresourcesmeta-infservices)

<!-- TOC -->

# Open Data Mesh Command Line Extensions Starter Project

## About

This repository provides a starting project to implement a ImporterExtension, an Extension of the Open Data Mesh Command
Line. This extension adds functionalities to the `import` command.

## Getting Started

To begin your implementation, fork this repository.
After forking, update the necessary project files to fit your extension’s requirements.

## Project Configuration

After setting up your fork, modify the following files:

### GitHub Workflows

Located in `.github/workflows`, these files define CI/CD pipelines. You should:

- Update the workflow names to reflect your extension.
- Modify the target `.jar` filename to match your implementation.
- Add any additional steps required for your extension.

Files to modify:

- `ci.yml` – Defines a workflows to execute tests.
- `cicd.yml` – Extends CI by adding .jar deployment step.

### pom.xml

The `pom.xml` file is essential for project metadata and dependencies. Modify:

- `groupId`, `artifactId`, `version`, `name`, and `description` to align with your extension.
- Add any required dependencies for additional functionality.

### src/main/resources/META-INF/services

This directory contains service provider configuration files that allow Java's Service Provider Interface (SPI) to
dynamically discover and load implementations.

- `org.opendatamesh.cli.extensions.importer.ImporterExtension`: this file should contain the fully qualified class name
  of the implementation of the `ImporterExtension` interface.
  Java’s SPI mechanism will use this to load your custom importer implementation dynamically.
  Example:  `org.opendatamesh.cli.extensionstarter.importer.ImporterStarterExtension`

