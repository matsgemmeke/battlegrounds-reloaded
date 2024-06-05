# Battlegrounds
A multi-gamemode shooter plugin

## Cloning the repository
To be able to build the project after cloning the repository, it is required to have a local maven repository for the
following spigot versions:

- 1_20_R2

If you have no local maven repository you can install it with the `BuildTools.jar` from the spigot website 
[here](https://www.spigotmc.org/wiki/buildtools/).

### Running unit tests
To run the unit tests you can use the JUnit feature inside Intellij. In some cases the test run may give an exception
when ran using Java 17. To solve this you have to add the VM argument `--add-opens <my-module>/<my-package>=ALL-UNNAMED`
to the run configuration.

### Building the project
To build the project into a jar file you can use the `mvn clean install` command.