import React from 'react'
import { Route, IndexRoute } from 'react-router'

import App from './containers/app'
import Home from './containers/home'
import Planets from './containers/planets'
import Species from './containers/species'
import CharacterPage from './containers/characterPage'
import PlanetPage from './containers/planetPage'

export default (
  <Route path="/" component={App}>
    <IndexRoute component={Home}/>
    <Route path="planets" component={Planets}/>
    <Route path="species" component={Species}/>
    <Route path="character/:characterId" component={CharacterPage}/>
    <Route path="planet/:planetId" component={PlanetPage}/>
  </Route>
)
