import * as GraphQL from '../graphql'

export const GRAPHQL_REQUEST = 'GRAPHQL_REQUEST'
export const GRAPHQL_SUCCESS = 'GRAPHQL_SUCCESS'
export const GRAPHQL_FAILURE = 'GRAPHQL_FAILURE'

export const fetchGraphQL = (query, variables) => (dispatch, getState) => {
  dispatch({ type: GRAPHQL_REQUEST })
  return GraphQL.fetchGraphQL(query, variables)
    .then(json => dispatch({
      type: GRAPHQL_SUCCESS,
      payload: json.data
    }))
    .catch(err => dispatch({
      type: GRAPHQL_FAILURE,
      payload: err
    }))
}
