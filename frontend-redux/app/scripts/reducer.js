import { combineReducers } from 'redux'
import { routerStateReducer } from 'redux-router'
import homeReducer from './reducers/home'
import planetsReducer from './reducers/planets'

export default combineReducers({
  router: routerStateReducer,
  home: homeReducer,
  planets: planetsReducer
})
