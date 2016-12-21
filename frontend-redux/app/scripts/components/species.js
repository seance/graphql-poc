import React, { Component, PropTypes as P } from 'react'
import { Link } from 'react-router'
import R from 'ramda'

export default class Species extends Component {

  static propTypes = {
    species: P.object.isRequired
  }

  render() {
    const { species: s } = this.props
    return (
      <div className="species">
        <div className="icon-panel">
          <div className="species-icon"></div>
        </div>
        <div className="info-panel">
          <div className="name">
            <Link to={`/species/${s.id}`}>{s.name}</Link>
          </div>
        </div>
      </div>
    )
  }
}
