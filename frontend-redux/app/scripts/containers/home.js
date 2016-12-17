import React, { Component, PropTypes as P } from 'react'
import { connect } from 'react-redux'
import { push } from 'redux-router'

import { fetchCharacters } from '../actions/home'
import CharacterList from '../components/characterList'

class Home extends Component {

  static propTypes = {
    isFetching: P.bool.isRequired,
    characters: P.arrayOf(P.object).isRequired
  }

  componentDidMount() {
    const { fetchCharacters } = this.props
    fetchCharacters()
  }

  render() {
    const { isFetching, characters } = this.props
    const content = isFetching
      ? this.renderFetching()
      : this.renderCharacterList(characters)

    return (
      <div className="home">
        {content}
      </div>
    )
  }

  renderFetching() {
    return <div className="progress"></div>
  }

  renderCharacterList(characters) {
    return (
      <div>
        <h2>The Characters</h2>
        <CharacterList characters={characters}
          clickCharacter={this.clickHandler('/character', 'characterId')}
          clickPlanet={this.clickHandler('/planet', 'planetId')}
          clickSpecies={this.clickHandler('/species', 'speciesId')}/>
      </div>
    )
  }

  clickHandler(path, qname) {
    const { push } = this.props
    return id => e => {
      e.preventDefault()
      push({
        pathname: path,
        query: { [qname]: id }
      })
    }
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.home.toJS()
}), {
  // mapDispatchToProps
  fetchCharacters,
  push
})(Home)
