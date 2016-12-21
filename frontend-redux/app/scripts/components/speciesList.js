import React, { Component, PropTypes as P } from 'react'
import R from 'ramda'

import Species from './species'

export default class SpeciesList extends Component {

  static propTypes = {
    species: P.arrayOf(P.object).isRequired
  }

  render() {
    const { species, clickSpecies } = this.props
    return (
      <div className="species-list">
        {species.map(s => (
          <Species key={s.id} species={s}/>
        ))}
      </div>
    )
  }
}
