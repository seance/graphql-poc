import React, { Component, PropTypes } from 'react'
import R from 'ramda'

export default class CharacterList extends Component {

  static propTypes = {
    characters: PropTypes.arrayOf(PropTypes.object).isRequired,
    withAssociates: PropTypes.bool.isRequired
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
