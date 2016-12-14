import 'es6-promise'
import React from 'react'
import ReactDOM from 'react-dom'
import { createHistory } from 'history';
import { Provider } from 'react-redux'
import { createStore, applyMiddleware, compose } from 'redux'
import { reduxReactRouter } from 'redux-router'
import { ReduxRouter } from 'redux-router'
import createLogger from 'redux-logger'
import reduxThunk from 'redux-thunk'

import routes from './routes'
import reducer from './reducer'

const createStoreWithMiddleware = compose(
  reduxReactRouter({ routes, createHistory }),
  applyMiddleware(reduxThunk, createLogger())
)(createStore)

ReactDOM.render(
  <Provider store={createStoreWithMiddleware(reducer)}>
    <ReduxRouter/>
  </Provider>,
  document.getElementById('root'))
