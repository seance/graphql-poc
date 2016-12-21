import { combineReducers } from 'redux'
import { routerStateReducer } from 'redux-router'
import homeReducer from './reducers/home'
import planetsReducer from './reducers/planets'
import speciesReducer from './reducers/species'
import characterPageReducer from './reducers/characterPage'
import planetPageReducer from './reducers/planetPage'

export default combineReducers({
  router: routerStateReducer,
  home: homeReducer,
  planets: planetsReducer,
  species: speciesReducer,
  characterPage: characterPageReducer,
  planetPage: planetPageReducer
})
