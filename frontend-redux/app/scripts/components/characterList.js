import React, { Component, PropTypes as P } from 'react'
import R from 'ramda'

import Character from './character'

export default class CharacterList extends Component {

  static propTypes = {
    characters: P.arrayOf(P.object).isRequired
  }

  render() {
    const { characters } = this.props
    return (
      <div className="character-list">
        <h2>The Characters</h2>
        <div className="character-list-items">
          {characters.map(c => (
            <Character key={c.id} character={c}/>
          ))}
        </div>
      </div>
    )
  }
}
