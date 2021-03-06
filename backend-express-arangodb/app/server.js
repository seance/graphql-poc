import express from 'express'
import graphqlHTTP from 'express-graphql'
import * as habitat from 'habitat'
import { schema, charLoader } from './schema'

const app = express()

app.use('/graphql', graphqlHTTP(request => ({
  schema: schema,
  context: { charLoader: charLoader() },
  graphiql: true
})))

const config = habitat.get('SERVER')
const server = app.listen(config.port, config.interface, () => {
  const { address, port } = server.address()
  console.log(`Server listening at ${address}:${port}`)
})
