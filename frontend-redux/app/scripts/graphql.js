import fetch from 'isomorphic-fetch';
import { print as printGraphQL } from 'graphql-tag/printer';

const GRAPHQL_ENDPOINT = '/backend1/graphql'

export const fetchGraphQL = (query, variables) => {
  const opName = query.definitions[0].name;
  return fetch(GRAPHQL_ENDPOINT, {
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
      throw new Error(`[${response.status}] ${response.statusText}`)
    }
    return response.json()
  });
};
