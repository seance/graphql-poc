import fetch from 'isomorphic-fetch';
import { print as printGraphQL } from 'graphql-tag/printer';

const graphQLEndpoint = backend => `/${backend}/graphql`

export const fetchGraphQL = (backend, query, variables) => {
  const opName = query.definitions[0].name
  const endpoint = graphQLEndpoint(backend)

  return fetch(endpoint, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      query: printGraphQL(query),
      operationName: opName ? opName.value : null,
      variables: variables
    })
  }).then(response => {
    if (response.status >= 400) {
      return response.json().then(err => {
        console.error(JSON.stringify(err.errors))
        throw new Error(err.errors)
      })
    }
    return response.json()
  });
};
