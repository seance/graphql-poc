import {
  GraphQLSchema,
  GraphQLObjectType,
  GraphQLList,
  GraphQLID,
  GraphQLString,
  GraphQLInt
} from 'graphql';

import { aql } from 'arangojs';
import db from './database';

const queryAll = query =>
  db.query(query).then(cursor => cursor.all());

const foos = () =>
  queryAll(aql`FOR foo IN foos RETURN foo`);

const FooType = new GraphQLObjectType({
  name: 'Foo',
  fields: () => ({
    id: {
      type: GraphQLID
    },
    bar: {
      type: GraphQLString
    },
    zut: {
      type: GraphQLInt
    }
  })
});

const QueryType = new GraphQLObjectType({
  name: 'Query',
  fields: () => ({
    foos: {
      type: new GraphQLList(FooType),
      resolve: (root, args, context, info) => {
        return foos();
      }
    }
  })
});

export default new GraphQLSchema({
  query: QueryType
});
