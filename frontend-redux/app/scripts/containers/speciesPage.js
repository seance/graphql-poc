import React, { Component, PropTypes as P } from 'react'
import { connect } from 'react-redux'

import { fetchSpeciesAndRelated } from '../actions/speciesPage'
import Species from '../components/species'
import PlanetList from '../components/planetList'
import CharacterList from '../components/characterList'

class SpeciesPage extends Component {

  static propTypes = {
    isFetching: P.bool.isRequired,
    species   : P.object
  }

  componentDidMount() {
    const { fetchSpeciesAndRelated, params } = this.props
    fetchSpeciesAndRelated({
      speciesId: params.speciesId
    })
  }

  render() {
    const { isFetching, species } = this.props
    const content = isFetching || !species
      ? this.renderFetching()
      : this.renderSpeciesPage(species)

    return (
      <div className="species-page">
        {content}
      </div>
    )
  }

  renderFetching() {
    return <div className="progress"></div>
  }

  renderSpeciesPage(species) {
    return (
      <div>
        <h2>The Species</h2>
        <Species species={species}/>
        <h2>Found On</h2>
        <PlanetList planets={species.foundOn}/>
        <h2>Notable Members</h2>
        <CharacterList characters={species.notableMembers}/>
      </div>
    )
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.router,
  ...state.speciesPage.toJS()
}), {
  // mapDispatchToProps
  fetchSpeciesAndRelated
})(SpeciesPage)
