import React, { Component, PropTypes as P } from 'react'
import { connect } from 'react-redux'

import { fetchPlanetAndRelated } from '../actions/planetPage'
import Planet from '../components/planet'
import CharacterList from '../components/characterList'

class PlanetPage extends Component {

  static propTypes = {
    isFetching: P.bool.isRequired,
    planet    : P.object
  }

  componentDidMount() {
    const { fetchPlanetAndRelated, params } = this.props
    fetchPlanetAndRelated({
      planetId: params.planetId
    })
  }

  render() {
    const { isFetching, planet } = this.props
    const content = isFetching || !planet
      ? this.renderFetching()
      : this.renderPlanetPage(planet)

    return (
      <div className="planet-page">
        {content}
      </div>
    )
  }

  renderFetching() {
    return <div className="progress"></div>
  }

  renderPlanetPage(planet) {
    return (
      <div>
        <h2>The Planet</h2>
        <Planet planet={planet}/>
        <h2>Known Species</h2>
        TBD
        <h2>Notable Natives</h2>
        <CharacterList characters={planet.natives}/>
      </div>
    )
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.router,
  ...state.planetPage.toJS()
}), {
  // mapDispatchToProps
  fetchPlanetAndRelated
})(PlanetPage)
