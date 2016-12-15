import React, { Component, PropTypes as P } from 'react'
import R from 'ramda'

export default class CharacterList extends Component {

  static propTypes = {
    characters    : P.arrayOf(P.object).isRequired,
    withAssociates: P.bool.isRequired
  }

  render() {
    const { characters, withAssociates } = this.props
    const associates = withAssociates ? (c =>
      <ul>
        {c.associates.map(a => (
          <li key={`${c.id}-${a.character.id}`}>
            {a.character.name} ({a.relation})
          </li>
        ))}
      </ul>
    ) : R.always(null);

    return (
      <div>
        Characters
        <ul>
          {characters.map(c => (
            <li key={`character-${c.id}`}>
              {c.name}
              {associates(c)}
            </li>
          ))}
        </ul>
      </div>
    )
  }
}
