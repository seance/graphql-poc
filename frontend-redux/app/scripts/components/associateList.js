import React, { Component, PropTypes as P } from 'react'
import { Link } from 'react-router'
import R from 'ramda'

import Character from './character'

export default class AssociateList extends Component {

  static propTypes = {
    associates: P.arrayOf(P.object).isRequired
  }

  render() {
    const { associates } = this.props
    return (
      <div className="associate-list">
        {associates.map(a => (
          <Link to={`/character/${a.character.id}`} key={a.character.id}>
            <div className="associate">
              <div className={(a.character.kind === 'organic')
                  ? 'associate-organic'
                  : 'associate-droid'}>
                <span>{a.character.name} </span>
                <span className="associate-faction">
                  of {this.factionName(a)}
                </span>
              </div>
              <div className="associate-relation">
                ({a.relation})
              </div>
            </div>
          </Link>
        ))}
      </div>
    )
  }

  factionName(a) {
    switch (a.character.faction) {
      case 'RebelAlliance':
        return 'the Rebel Alliance'
      case 'GalacticEmpire':
        return 'the Galactic Empire'
      default:
        return 'unknown faction'
    }
  }
}
