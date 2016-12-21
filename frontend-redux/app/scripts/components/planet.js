import React, { Component, PropTypes as P } from 'react'
import { Link } from 'react-router'
import R from 'ramda'

export default class Planet extends Component {

  static propTypes = {
    planet: P.object.isRequired
  }

  render() {
    const { planet: p } = this.props
    return (
      <div className="planet">
        <div className="icon-panel">
          <div className="planet-icon"></div>
        </div>
        <div className="info-panel">
          <div className="name">
            <Link to={`/planet/${p.id}`}>{p.name}</Link>
          </div>
          <div className="ecology-panel">
            <div className="ecology-icon">Ecology</div>
            <div className="ecology-name">{p.ecology}</div>
          </div>
        </div>
      </div>
    )
  }
}
