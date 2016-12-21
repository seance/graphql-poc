import React, { Component, PropTypes as P } from 'react'
import R from 'ramda'

export default class Planet extends Component {

  static propTypes = {
    planet      : P.object.isRequired,
    clickPlanet : P.func.isRequired
  }

  render() {
    const { planet: p, clickPlanet } = this.props
    return (
      <div className="planet">
        <div className="icon-panel">
          <div className="planet-icon"></div>
        </div>
        <div className="info-panel">
          <div className="name" onClick={clickPlanet(p.id)}>{p.name}</div>
          <div className="ecology-panel">
            <div className="ecology-icon">Ecology</div>
            <div className="ecology-name">{p.ecology}</div>
          </div>
        </div>
      </div>
    )
  }
}
