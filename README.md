# Project Board
A web-based bulletin board for open positions in projects. Users can view and apply for projects.

Currently in development.

## Frontend


## Backend
The backend primarily uses the [Spring framework](https://spring.io). Projects in the underlying database get updated by a scheduled job which uses a `AbstractProjectReader` implementation to retrieve projects from a data source. The update interval can be customized by setting a custom interval in the spring application properties file. The projects can be retrieved via a REST interface, which is secured by [Spring Security](https://spring.io/projects/spring-security) and [Keycloak](https://www.keycloak.org), but you can customize it as well by disabling the "adesso-keycloak" profile.

To customize the project board behaviour, there are only a few interfaces you have to implement and expose as spring `Components`: 

1. `AbstractProjectReader` - The implementation of this interface is used to retrieve projects from a data source. The implemented `JiraProjectReader` retrieves projects from a JIRA issue tracker by utilizing a spring `RestTemplate`. To use your custom implementation you only have to disable the "adesso-jira" spring profile in the `application.properties`file and expose your custom implementation as a spring `Component`. By returning a list of custom `AbstractProject`s you can modify the data structure of your projects. But don't forget to set your project class in the `application.properties` file under `projectboard.project-class` if you do so!

2. `ProjectApplicationHandler` - The implementation of this interface is used to handle incoming position applications. To use your custom implementation you have to disable the "adesso-jira" spring profile and expose your implementation as a spring `Component`.

3. `ExpressionEvaluator` - To further customize REST interface access this interface can be implemented to restrict access to authenticated users as well and create a fine grained auhorization pattern. This can be very handy if you want, for example, be able to manually authorize every user to see projects. The auto-configured implementation (`AllowAccessExpressionEvaluator`) allows every authenticated user access by default.
