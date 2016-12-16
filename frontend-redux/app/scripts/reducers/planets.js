import Immutable, { Map, List } from 'immutable'
import { FetchPlanets } from '../actions/planets'

const initialState = Map({
  isFetching: false,
  planets   : List()
})

export default (state = initialState, action) => {
  switch (action.type) {
    case FetchPlanets.Request:
      return state.set('isFetching', true)
    case FetchPlanets.Success:
      return state.merge(Immutable.fromJS({
        isFetching: false,
        planets: action.payload.planets
      }))
  }

  return state
}
