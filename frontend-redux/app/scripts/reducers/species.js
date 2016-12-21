import Immutable, { Map, List } from 'immutable'
import { FetchSpecies } from '../actions/species'

const initialState = Map({
  isFetching: false,
  species   : List()
})

export default (state = initialState, action) => {
  switch (action.type) {
    case FetchSpecies.Request:
      return state.set('isFetching', true)
    case FetchSpecies.Success:
      return state.merge(Immutable.fromJS({
        isFetching: false,
        species: action.payload.species
      }))
  }

  return state
}
