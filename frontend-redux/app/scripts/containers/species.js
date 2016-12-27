import React, { Component, PropTypes as P } from 'react'
import { connect } from 'react-redux'

import { fetchSpecies } from '../actions/species'
import SpeciesList from '../components/speciesList'

class Species extends Component {

  static propTypes = {
    isFetching  : P.bool.isRequired,
    species     : P.arrayOf(P.object).isRequired,
    fetchSpecies: P.func.isRequired
  }

  componentDidMount() {
    const { fetchSpecies } = this.props
    fetchSpecies()
  }

  render() {
    const { isFetching, species } = this.props
    const content = isFetching
      ? this.renderFetching()
      : this.renderSpeciesList(species)

    return (
      <div className="main-species">
        {content}
      </div>
    )
  }

  renderFetching() {
    return <div className="progress"></div>
  }

  renderSpeciesList(species) {
    return (
      <div>
        <h2>The Species</h2>
        <SpeciesList species={species}/>
      </div>
    )
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.species.toJS()
}), {
  // mapDispatchToProps
  fetchSpecies
})(Species)
