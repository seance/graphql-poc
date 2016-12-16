import React, { Component, PropTypes as P } from 'react'
import R from 'ramda'

import Planet from './planet'

export default class PlanetList extends Component {

  static propTypes = {
    planets: P.arrayOf(P.object).isRequired
  }

  render() {
    const { planets } = this.props
    return (
      <div className="planet-list">
        <h2>The Planets</h2>
        <div className="planet-list-items">
          {planets.map(p => (
            <Planet key={p.id} planet={p}/>
          ))}
        </div>
      </div>
    )
  }
}
