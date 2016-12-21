import React, { Component, PropTypes as P } from 'react'
import { connect } from 'react-redux'

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
      <div className="main-home">
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
        <CharacterList characters={characters}/>
      </div>
    )
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.home.toJS()
}), {
  // mapDispatchToProps
  fetchCharacters
})(Home)
