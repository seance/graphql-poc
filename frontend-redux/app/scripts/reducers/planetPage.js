import Immutable, { Map, List } from 'immutable'
import { FetchPlanetAndRelated } from '../actions/planetPage'

const initialState = Map({
  isFetching: true,
  planet: null
})

export default (state = initialState, action) => {
  switch (action.type) {
    case FetchPlanetAndRelated.Request:
      return state.set('isFetching', true)
    case FetchPlanetAndRelated.Success:
      return state.merge(Immutable.fromJS({
        isFetching: false,
        planet: action.payload.planet
      }))
  }

  return state
}
