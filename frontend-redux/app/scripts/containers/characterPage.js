import React, { Component, PropTypes as P } from 'react'
import { connect } from 'react-redux'

import { fetchCharacterAndAssocs } from '../actions/characterPage'
import Character from '../components/character'
import AssociateList from '../components/associateList'

class CharacterPage extends Component {

  static propTypes = {
    isFetching: P.bool.isRequired,
    character : P.object
  }

  componentDidMount() {
    const { params } = this.props
    this.fetchCharacterAndAssocs(params.characterId)
  }

  componentWillReceiveProps(nextProps) {
    const { isFetching, character } = this.props
    const nextCharacterId = nextProps.params.characterId

    if (!isFetching && (!character || character.id != nextCharacterId)) {
      this.fetchCharacterAndAssocs(nextCharacterId)
    }
  }

  fetchCharacterAndAssocs(characterId) {
    this.props.fetchCharacterAndAssocs({
      characterId
    })
  }

  render() {
    const { isFetching, character } = this.props
    const content = isFetching || !character
      ? this.renderFetching()
      : this.renderCharacterPage(character)

    return (
      <div className="character-page">
        {content}
      </div>
    )
  }

  renderFetching() {
    return <div className="progress"></div>
  }

  renderCharacterPage(character) {
    return (
      <div>
        <h2>The Character</h2>
        <Character character={character}/>
        <h2>Known Associates</h2>
        <AssociateList associates={character.associates}/>
      </div>
    )
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.router,
  ...state.characterPage.toJS()
}), {
  // mapDispatchToProps
  fetchCharacterAndAssocs
})(CharacterPage)
