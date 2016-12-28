import Immutable, { Map, List } from 'immutable'
import { FetchSpeciesAndRelated } from '../actions/speciesPage'

const initialState = Map({
  isFetching: false,
  species: null
})

export default (state = initialState, action) => {
  switch (action.type) {
    case FetchSpeciesAndRelated.Request:
      return state.set('isFetching', true)
    case FetchSpeciesAndRelated.Success:
      return state.merge(Immutable.fromJS({
        isFetching: false,
        species: action.payload.species1
      }))
  }

  return state
}
