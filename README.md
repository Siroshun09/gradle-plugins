# gradle-plugins

A repository for managing shared build logics between my projects.

## List of plugins

### jcommon

Plugin for setup project as Java project.

This can configure Java version shared repositories/dependencies, and more.

### aggregate-javadoc

Plugin for aggregating Javadoc from subprojects.

### aggregate-javadoc-collector

Plugin to collect Javadoc tasks from subprojects.

This needs to be applied to each subproject that you want to aggregate Javadocs.

It is also necessary to apply aggregate-javadoc plugin to root project for working.

### maven-publication

Plugin for setting up Maven publication.

This also have utilities of basic pom configuration.

### maven-central-portal

Plugin for uploading artifacts to Maven Central Portal. 

## License

This project is under the Apache License version 2.0. Please see [LICENSE](LICENSE) for more info.

Copyright © 2025, Siroshun09
