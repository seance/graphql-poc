import { combineReducers } from 'redux'
import { routerStateReducer } from 'redux-router'
import homeReducer from './reducers/home'

export default combineReducers({
  router: routerStateReducer,
  home: homeReducer
})
