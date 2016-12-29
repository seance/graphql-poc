## graphql-poc

This repository contains a small *Star Wars* themed example web app for using [GraphQL](http://graphql.org), built as a study.

As such, the project is divided into multiple implementations of the frontend and backend parts, each exercising a different technology stack to achieve the same end result.

The *Star Wars* theme was chosen since the official GraphQL examples use this theme.

**Prerequisites**
* ![Docker](https://github.com/seance/graphql-poc/blob/master/readme-files/docker.png) [Docker](https://www.docker.com/) 1.13+ with Docker Compose

Also make sure the ports forwarded from the containers (see docker-compose.yml) are available, or remove the port mappings. The database container port mappings exist for debugging purposes.

**Running**

* Run `docker-compose up`

Note that the initial build of the images builds also the distributables (JARs etc) and this will take some time.

After the startup has completed, navigate to [http://localhost:8000](http://localhost:8000).

## Frontend

Each frontend implementation should be able to query any backend. The currently used backend should be changeable from the user interface.

### Redux (`frontend-redux`)

![React](https://github.com/seance/graphql-poc/blob/master/readme-files/react.png)
![Redux](https://github.com/seance/graphql-poc/blob/master/readme-files/redux.png)
![GraphQL](https://github.com/seance/graphql-poc/blob/master/readme-files/graphql.png)

Served on port 8000. Uses `isomorphic-fetch` to directly access the GraphQL API endpoint.


### Relay (`frontend-relay`)

*Not implemented yet*

## Backend

Since GraphQL gives the frontend much freedom in querying the backend, the interface between the GraphQL schema and the data source is an important area of study.

The schema used exercises a range of GraphQL's facilities, including interfaces and deferred execution.

### JVM + Relational (`backend-akka-postgres`)

![Scala](https://github.com/seance/graphql-poc/blob/master/readme-files/scala.png)
![Akka](https://github.com/seance/graphql-poc/blob/master/readme-files/akka.png)
![Sangria](https://github.com/seance/graphql-poc/blob/master/readme-files/sangria.png)
![Slick](https://github.com/seance/graphql-poc/blob/master/readme-files/slick.png)
![PostgreSQL](https://github.com/seance/graphql-poc/blob/master/readme-files/postgres.png)

Explores using a relational data source with GraphQL, with Scala's usual Slick database access and query library.

* Persistence: [PostgreSQL](https://www.postgresql.org/)
* Query abstraction: [Slick](http://slick.lightbend.com/)
* GraphQL implementation: [Sangria](http://sangria-graphql.org/)
* Web server/routing: [Akka HTTP](http://doc.akka.io/docs/akka-http/current/scala.html)

### JVM + Graph (`backend-akka-orientdb`)

![Scala](https://github.com/seance/graphql-poc/blob/master/readme-files/scala.png)
![Akka](https://github.com/seance/graphql-poc/blob/master/readme-files/akka.png)
![Sangria](https://github.com/seance/graphql-poc/blob/master/readme-files/sangria.png)
![Gremlin](https://github.com/seance/graphql-poc/blob/master/readme-files/gremlin.png)
![OrientDB](https://github.com/seance/graphql-poc/blob/master/readme-files/orientdb.png)

Explores using a graph data source with GraphQL using Apache Tinkerpop 3 Gremlin query language implementation for Scala.

* Persistence: [OrientDB](http://orientdb.com/orientdb/)
* Query abstraction: [Gremlin](http://tinkerpop.apache.org/gremlin.html)
* GraphQL implementation: [Sangria](http://sangria-graphql.org/)
* Web server/routing: [Akka HTTP](http://doc.akka.io/docs/akka-http/current/scala.html)

### Node + Graph (`backend-express-arangodb`)

![NodeJS](https://github.com/seance/graphql-poc/blob/master/readme-files/node.png)
![Express](https://github.com/seance/graphql-poc/blob/master/readme-files/express.png)
![GraphQL.js](https://github.com/seance/graphql-poc/blob/master/readme-files/graphqljs.png)
![ArangoDB](https://github.com/seance/graphql-poc/blob/master/readme-files/arangodb.png)

Explores using a graph data source with GraphQL using ArangoDB's AQL language via the ArangoJS driver.

* Persistence: [ArangoDB](https://www.arangodb.com/)
* Query language: [AQL](https://docs.arangodb.com/3.1/AQL/)
* GraphQL implementation: [GraphQL.js](https://github.com/graphql/graphql-js)
* Web server/routing: [Express](http://expressjs.com/)
