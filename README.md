# Project Board
A web-based bulletin board for open positions in projects.

Currently in development.

## Frontend


## Backend
The backend primarily uses the [Spring framework](https://spring.io). Projects in the underlying database get updated by a scheduled job which uses a `AbstractProjectReader` implementation to retrieve projects from a data source. The update interval can be customized by setting a custom interval in the spring application properties file. The projects can be retrieved via a REST interface.

To customize the project data structure, project retrieval and position application behaviour you only have to implement two interfaces: 

1. `AbstractProjectReader` - The implementation of this interface is used to retrieve projects from a data source. The standard implementation `JiraProjectReader` retrieves projects from a JIRA issue tracker. To use your custom implementation you only have to expose a named bean called "projectReaderBean". By returning a list of custom `AbstractProject`s you can modify the data structure of your projects.

2. `ProjectApplicationHandler` - The implementation of this interface is used to handle incoming position applications.
