import React, { Component, PropTypes as P } from 'react'
import R from 'ramda'

import Planet from './planet'

export default class PlanetList extends Component {

  static propTypes = {
    planets     : P.arrayOf(P.object).isRequired,
    clickPlanet : P.func.isRequired
  }

  render() {
    const { planets, clickPlanet } = this.props
    return (
      <div className="planet-list">
        {planets.map(p => (
          <Planet key={p.id} planet={p}
            clickPlanet={clickPlanet}/>
        ))}
      </div>
    )
  }
}
