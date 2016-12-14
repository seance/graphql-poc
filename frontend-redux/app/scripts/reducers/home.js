import * as Actions from '../actions/graphql'

const initialState = {
  isFetching: false,
  characters: [],
  withAssociates: true
}

export default (state = initialState, action) => {
  switch (action.type) {
    case Actions.GRAPHQL_REQUEST:
      return Object.assign({}, state, {
        isFetching: true
      })
    case Actions.GRAPHQL_SUCCESS:
      return Object.assign({}, state, {
        isFetching: false,
        characters: action.payload.characters
      })
  }

  return state
}
