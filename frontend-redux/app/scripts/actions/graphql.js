import * as GraphQL from '../graphql'

export const fetchGraphQL = (actions, query, variables) => (dispatch, getState) => {
  const backend = getState().app.get('backend')
  dispatch({ type: actions.Request })
  return GraphQL.fetchGraphQL(backend, query, variables)
    .then(json => dispatch({
      type: actions.Success,
      payload: json.data
    }))
    .catch(err => dispatch({
      type: actions.Failure,
      payload: err
    }))
}
