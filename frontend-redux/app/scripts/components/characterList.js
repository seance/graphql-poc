import React, { Component, PropTypes as P } from 'react'
import R from 'ramda'

import Character from './character'

export default class CharacterList extends Component {

  static propTypes = {
    characters    : P.arrayOf(P.object).isRequired,
    clickCharacter: P.func.isRequired,
    clickPlanet   : P.func.isRequired,
    clickSpecies  : P.func.isRequired
  }

  render() {
    const {
      characters,
      clickCharacter,
      clickPlanet,
      clickSpecies
    } = this.props

    return (
      <div className="character-list">
        {characters.map(c => (
          <Character key={c.id} character={c}
            clickCharacter={clickCharacter}
            clickPlanet={clickPlanet}
            clickSpecies={clickSpecies}/>
        ))}
      </div>
    )
  }
}
