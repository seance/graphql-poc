import * as GraphQL from '../graphql'

export const fetchGraphQL = (actions, query, variables) => (dispatch, getState) => {
  dispatch({ type: actions.Request })
  return GraphQL.fetchGraphQL(query, variables)
    .then(json => dispatch({
      type: actions.Success,
      payload: json.data
    }))
    .catch(err => dispatch({
      type: actions.Failure,
      payload: err
    }))
}
