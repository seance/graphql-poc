import React from 'react'
import { Route, IndexRoute } from 'react-router'

import App from './containers/app'
import Home from './containers/home'
import Planets from './containers/planets'

export default (
  <Route path="/" component={App}>
    <IndexRoute component={Home}/>
    <Route path="planets" component={Planets}/>
    <Route path="species" component={Home}/>
  </Route>
)
