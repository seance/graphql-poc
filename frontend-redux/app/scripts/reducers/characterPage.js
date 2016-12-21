import Immutable, { Map, List } from 'immutable'
import { FetchCharacterAndAssocs } from '../actions/characterPage'

const initialState = Map({
  isFetching: false,
  character: null
})

export default (state = initialState, action) => {
  switch (action.type) {
    case FetchCharacterAndAssocs.Request:
      return state.set('isFetching', true)
    case FetchCharacterAndAssocs.Success:
      return state.merge(Immutable.fromJS({
        isFetching: false,
        character: action.payload.character
      }))
  }

  return state
}
