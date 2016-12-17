import React, { Component, PropTypes as P } from 'react'
import R from 'ramda'

import Character from './character'

export default class AssociateList extends Component {

  static propTypes = {
    associates    : P.arrayOf(P.object).isRequired,
    clickCharacter: P.func.isRequired
  }

  render() {
    const {
      associates,
      clickCharacter
    } = this.props

    return (
      <div className="associate-list">
        {associates.map(a => (
          <div key={a.character.id} className="associate"
            onClick={clickCharacter(a.character.id)}>
            <div className={(a.character.kind === 'organic')
                ? 'associate-organic'
                : 'associate-droid'}>
              {a.character.name}
            </div>
            <div className="associate-relation">
              ({a.relation})
            </div>
          </div>
        ))}
      </div>
    )
  }
}
