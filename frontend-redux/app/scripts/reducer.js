import { combineReducers } from 'redux'
import { routerStateReducer } from 'redux-router'
import appReducer from './reducers/app'
import homeReducer from './reducers/home'
import planetsReducer from './reducers/planets'
import speciesReducer from './reducers/species'
import characterPageReducer from './reducers/characterPage'
import planetPageReducer from './reducers/planetPage'
import speciesPageReducer from './reducers/speciesPage'

export default combineReducers({
  router: routerStateReducer,
  app: appReducer,
  home: homeReducer,
  planets: planetsReducer,
  species: speciesReducer,
  characterPage: characterPageReducer,
  planetPage: planetPageReducer,
  speciesPage: speciesPageReducer
})
