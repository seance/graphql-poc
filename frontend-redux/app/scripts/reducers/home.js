import Immutable, { Map, List } from 'immutable'
import { FetchCharacters } from '../actions/home'

const initialState = Map({
  isFetching: false,
  characters: List()
})

export default (state = initialState, action) => {
  switch (action.type) {
    case FetchCharacters.Request:
      return state.set('isFetching', true)
    case FetchCharacters.Success:
      return state.merge(Immutable.fromJS({
        isFetching: false,
        characters: action.payload.characters
      }))
  }

  return state
}
